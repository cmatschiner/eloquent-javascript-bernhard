package at.matschiner.meetminutes.transcription

/**
 * Ein einzelnes Transkript-Segment.
 * @param speaker optionale Sprecherkennung (Diarisierung), z. B. "Sprecher 1"
 */
data class TranscriptSegment(
    val startMs: Long,
    val endMs: Long,
    val text: String,
    val speaker: String? = null,
)

/** Vollständiges Transkript einer Aufnahme. */
data class Transcript(
    val language: String,
    val segments: List<TranscriptSegment>,
) {
    /** Reiner Fließtext über alle Segmente. */
    val fullText: String
        get() = segments.joinToString(" ") { it.text.trim() }.trim()
}
