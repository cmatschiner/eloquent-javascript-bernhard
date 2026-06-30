package at.matschiner.meetminutes.transcription

import at.matschiner.meetminutes.core.util.TimeFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Metadaten für den Transkript-Kopf. */
data class TranscriptMeta(
    val date: LocalDate,
    val art: String,
    val thema: String,
    val audioFileName: String,
    val durationMs: Long,
    val language: String,
)

/**
 * Erzeugt das reine Transkript als Markdown (.md). Reine Logik – unit-testbar.
 */
object TranscriptMarkdownFormatter {

    private val DATE = DateTimeFormatter.ISO_LOCAL_DATE

    fun format(meta: TranscriptMeta, transcript: Transcript): String {
        val sb = StringBuilder()
        sb.appendLine("# Transkript – ${meta.art} – ${meta.thema}")
        sb.appendLine()
        sb.appendLine("- **Datum:** ${meta.date.format(DATE)}")
        sb.appendLine("- **Audiodatei:** ${meta.audioFileName}")
        sb.appendLine("- **Sprache:** ${meta.language}")
        sb.appendLine("- **Dauer:** ${TimeFormat.mmss(meta.durationMs)}")
        sb.appendLine()
        sb.appendLine("---")
        sb.appendLine()

        if (transcript.segments.isEmpty()) {
            sb.appendLine("_(Kein Transkripttext erkannt.)_")
        } else if (transcript.segments.all { it.endMs == 0L && it.startMs == 0L }) {
            // Keine Zeitmarken – reiner Fließtext.
            sb.appendLine(transcript.fullText)
        } else {
            for (segment in transcript.segments) {
                val time = "[${TimeFormat.mmss(segment.startMs)}]"
                val speaker = segment.speaker?.let { "$it: " }.orEmpty()
                sb.appendLine("$time $speaker${segment.text.trim()}")
            }
        }
        return sb.toString().trimEnd() + "\n"
    }
}
