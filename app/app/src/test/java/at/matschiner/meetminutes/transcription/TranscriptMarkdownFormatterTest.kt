package at.matschiner.meetminutes.transcription

import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class TranscriptMarkdownFormatterTest {

    private val meta = TranscriptMeta(
        date = LocalDate.of(2026, 6, 29),
        art = "Meeting",
        thema = "Team-Meeting",
        audioFileName = "2026-06-29_Meeting - Team-Meeting.wav",
        durationMs = 65_000,
        language = "de",
    )

    @Test
    fun format_includesHeaderMetadata() {
        val md = TranscriptMarkdownFormatter.format(
            meta,
            Transcript("de", listOf(TranscriptSegment(0, 0, "Hallo Welt"))),
        )
        assertTrue(md.contains("# Transkript – Meeting – Team-Meeting"))
        assertTrue(md.contains("- **Datum:** 2026-06-29"))
        assertTrue(md.contains("- **Audiodatei:** 2026-06-29_Meeting - Team-Meeting.wav"))
        assertTrue(md.contains("- **Sprache:** de"))
        assertTrue(md.contains("- **Dauer:** 01:05"))
    }

    @Test
    fun format_plainTextWhenNoTimestamps() {
        val md = TranscriptMarkdownFormatter.format(
            meta,
            Transcript("de", listOf(TranscriptSegment(0, 0, "Erster Satz."), TranscriptSegment(0, 0, "Zweiter Satz."))),
        )
        assertTrue(md.contains("Erster Satz. Zweiter Satz."))
    }

    @Test
    fun format_timestampedLines() {
        val md = TranscriptMarkdownFormatter.format(
            meta,
            Transcript(
                "de",
                listOf(
                    TranscriptSegment(0, 3000, "Guten Morgen", speaker = "Sprecher 1"),
                    TranscriptSegment(3000, 6000, "Hallo"),
                ),
            ),
        )
        assertTrue(md.contains("[00:00] Sprecher 1: Guten Morgen"))
        assertTrue(md.contains("[00:03] Hallo"))
    }

    @Test
    fun format_handlesEmptyTranscript() {
        val md = TranscriptMarkdownFormatter.format(meta, Transcript("de", emptyList()))
        assertTrue(md.contains("Kein Transkripttext erkannt"))
    }
}
