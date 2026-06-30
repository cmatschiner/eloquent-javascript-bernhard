package at.matschiner.meetminutes.core.naming

import org.junit.Assert.assertEquals
import org.junit.Test

class MeetingFileNamerExtensionTest {

    @Test
    fun withExtension_replacesWavWithMd() {
        assertEquals(
            "2026-06-29_Meeting - Team-Meeting.md",
            MeetingFileNamer.withExtension("2026-06-29_Meeting - Team-Meeting.wav", "md"),
        )
    }

    @Test
    fun withExtension_acceptsLeadingDot() {
        assertEquals(
            "2026-06-29_Meeting - Team-Meeting.docx",
            MeetingFileNamer.withExtension("2026-06-29_Meeting - Team-Meeting.wav", ".docx"),
        )
    }

    @Test
    fun withExtension_addsExtensionWhenNonePresent() {
        assertEquals("aufnahme.wav", MeetingFileNamer.withExtension("aufnahme", "wav"))
    }

    @Test
    fun withExtension_keepsDotsInBaseName() {
        assertEquals(
            "2026-06-29_Call - v1.2 Review.md",
            MeetingFileNamer.withExtension("2026-06-29_Call - v1.2 Review.wav", "md"),
        )
    }
}
