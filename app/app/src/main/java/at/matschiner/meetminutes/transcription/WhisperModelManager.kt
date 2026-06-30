package at.matschiner.meetminutes.transcription

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Verwaltet die On-Device-Whisper-Modelle: Speicherort, Download (beim ersten
 * Start – Auftraggeber-Entscheidung) und Verfügbarkeitsprüfung.
 */
@Singleton
class WhisperModelManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val modelsDir: File by lazy {
        File(context.filesDir, "models").apply { mkdirs() }
    }

    fun modelFile(model: WhisperModel): File = File(modelsDir, model.fileName)

    fun isDownloaded(model: WhisperModel): Boolean {
        val f = modelFile(model)
        return f.exists() && f.length() > 0L
    }

    /**
     * Lädt das Modell herunter (falls noch nicht vorhanden) und meldet Fortschritt 0f..1f.
     * Lädt in eine .part-Datei und benennt erst nach Erfolg um (atomar gegen Abbruch).
     */
    suspend fun ensureDownloaded(
        model: WhisperModel,
        onProgress: (Float) -> Unit = {},
    ): File = withContext(Dispatchers.IO) {
        val target = modelFile(model)
        if (target.exists() && target.length() > 0L) return@withContext target

        val temp = File(modelsDir, "${model.fileName}.part")
        temp.delete()

        val connection = (URL(model.downloadUrl).openConnection() as HttpURLConnection).apply {
            connectTimeout = 30_000
            readTimeout = 60_000
            instanceFollowRedirects = true
        }
        try {
            connection.connect()
            if (connection.responseCode !in 200..299) {
                throw java.io.IOException("Download fehlgeschlagen: HTTP ${connection.responseCode}")
            }
            val total = connection.contentLengthLong.takeIf { it > 0 } ?: model.approxBytes
            connection.inputStream.use { input ->
                temp.outputStream().use { output ->
                    val buffer = ByteArray(64 * 1024)
                    var downloaded = 0L
                    while (true) {
                        val read = input.read(buffer)
                        if (read < 0) break
                        output.write(buffer, 0, read)
                        downloaded += read
                        onProgress((downloaded.toFloat() / total).coerceIn(0f, 1f))
                    }
                }
            }
            if (!temp.renameTo(target)) {
                temp.copyTo(target, overwrite = true)
                temp.delete()
            }
            target
        } finally {
            connection.disconnect()
        }
    }
}
