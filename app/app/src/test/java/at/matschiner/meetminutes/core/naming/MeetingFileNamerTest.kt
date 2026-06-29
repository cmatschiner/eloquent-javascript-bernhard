package at.matschiner.meetminutes.core.naming

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class MeetingFileNamerTest {

    private val date = LocalDate.of(2026, 6, 29)

    @Test
    fun baseName_followsNomenclature() {
        assertEquals(
            "2026-06-29_Meeting - Team-Meeting",
            MeetingFileNamer.baseName(date, "Meeting", "Team-Meeting"),
        )
    }

    @Test
    fun baseName_padsMonthAndDay() {
        assertEquals(
            "2026-01-05_Telefonat - Kunde A",
            MeetingFileNamer.baseName(LocalDate.of(2026, 1, 5), "Telefonat", "Kunde A"),
        )
    }

    @Test
    fun fileName_appendsExtension_withoutDuplicatingDot() {
        assertEquals(
            "2026-06-29_Meeting - Team-Meeting.wav",
            MeetingFileNamer.fileName(date, "Meeting", "Team-Meeting", "wav"),
        )
        assertEquals(
            "2026-06-29_Meeting - Team-Meeting.docx",
            MeetingFileNamer.fileName(date, "Meeting", "Team-Meeting", ".docx"),
        )
    }

    @Test
    fun sanitize_replacesIllegalCharacters() {
        assertEquals(
            "2026-06-29_Telefonat - Telefonat mit Firma X-Y",
            MeetingFileNamer.baseName(date, "Telefonat", "Telefonat mit Firma X/Y"),
        )
    }

    @Test
    fun sanitize_collapsesWhitespace() {
        assertEquals(
            "2026-06-29_Meeting - Team Meeting",
            MeetingFileNamer.baseName(date, "Meeting", "  Team    Meeting  "),
        )
    }

    @Test
    fun uniqueBaseName_returnsDesiredWhenFree() {
        assertEquals(
            "2026-06-29_Meeting - Team-Meeting",
            MeetingFileNamer.uniqueBaseName(
                "2026-06-29_Meeting - Team-Meeting",
                emptySet(),
            ),
        )
    }

    @Test
    fun uniqueBaseName_appendsCounterOnCollision() {
        val existing = setOf(
            "2026-06-29_Meeting - Team-Meeting",
            "2026-06-29_Meeting - Team-Meeting (2)",
        )
        assertEquals(
            "2026-06-29_Meeting - Team-Meeting (3)",
            MeetingFileNamer.uniqueBaseName("2026-06-29_Meeting - Team-Meeting", existing),
        )
    }
}
