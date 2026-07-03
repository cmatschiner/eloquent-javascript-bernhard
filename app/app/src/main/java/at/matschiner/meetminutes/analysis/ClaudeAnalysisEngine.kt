package at.matschiner.meetminutes.analysis

import at.matschiner.meetminutes.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Inhaltsanalyse über die Claude API (Anthropic Messages API) mit erzwungenem
 * Tool-Use für strukturierte Ausgabe (ADR-02).
 */
@Singleton
class ClaudeAnalysisEngine @Inject constructor(
    private val settings: SettingsRepository,
) : AnalysisEngine {

    override suspend fun analyze(
        transcriptText: String,
        context: AnalysisContext,
    ): MeetingAnalysis = withContext(Dispatchers.IO) {
        val apiKey = settings.anthropicApiKey.trim()
        require(apiKey.isNotEmpty()) {
            "Kein Anthropic-API-Key hinterlegt. Bitte in den Einstellungen eintragen."
        }
        require(transcriptText.isNotBlank()) { "Transkript ist leer." }

        val requestBody = buildRequestBody(transcriptText, context)
        val response = postMessages(apiKey, requestBody)
        val toolInput = extractToolInput(response)
            ?: error("Keine strukturierte Antwort von der Claude API erhalten.")
        AnalysisResponseParser.fromObject(toolInput)
    }

    private fun buildRequestBody(transcriptText: String, context: AnalysisContext): String {
        val tool = JSONObject()
            .put("name", AnalysisPrompt.TOOL_NAME)
            .put("description", "Strukturiertes Besprechungsprotokoll aus dem Transkript.")
            .put("input_schema", JSONObject(AnalysisPrompt.toolInputSchema()))

        val message = JSONObject()
            .put("role", "user")
            .put(
                "content",
                "Hier ist das Transkript der Besprechung:\n\n$transcriptText",
            )

        return JSONObject()
            .put("model", settings.analysisModel)
            .put("max_tokens", 4096)
            .put("system", AnalysisPrompt.systemPrompt(context))
            .put("tools", JSONArray().put(tool))
            .put("tool_choice", JSONObject().put("type", "tool").put("name", AnalysisPrompt.TOOL_NAME))
            .put("messages", JSONArray().put(message))
            .toString()
    }

    private fun postMessages(apiKey: String, body: String): JSONObject {
        val connection = (URL(API_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 30_000
            readTimeout = 120_000
            doOutput = true
            setRequestProperty("content-type", "application/json")
            setRequestProperty("x-api-key", apiKey)
            setRequestProperty("anthropic-version", ANTHROPIC_VERSION)
        }
        try {
            connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val text = stream?.bufferedReader()?.use { it.readText() }.orEmpty()
            if (code !in 200..299) {
                throw IOException("Claude API Fehler ($code): ${errorMessage(text)}")
            }
            return JSONObject(text)
        } finally {
            connection.disconnect()
        }
    }

    private fun errorMessage(body: String): String = runCatching {
        JSONObject(body).optJSONObject("error")?.optString("message")
    }.getOrNull()?.takeIf { it.isNotBlank() } ?: body.take(200)

    /** Findet das tool_use-Element und liefert dessen input-Objekt. */
    private fun extractToolInput(response: JSONObject): JSONObject? {
        val content = response.optJSONArray("content") ?: return null
        for (i in 0 until content.length()) {
            val block = content.optJSONObject(i) ?: continue
            if (block.optString("type") == "tool_use") {
                return block.optJSONObject("input")
            }
        }
        return null
    }

    private companion object {
        const val API_URL = "https://api.anthropic.com/v1/messages"
        const val ANTHROPIC_VERSION = "2023-06-01"
    }
}
