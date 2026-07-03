package at.matschiner.meetminutes.docx

import at.matschiner.meetminutes.analysis.ActionItem
import at.matschiner.meetminutes.analysis.FollowUp
import at.matschiner.meetminutes.analysis.MeetingAnalysis
import at.matschiner.meetminutes.analysis.TopicPoint
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class MeetingMinutesComposerTest {

    private val meta = ProtocolMeta(
        date = LocalDate.of(2026, 6, 29),
        art = "Meeting",
        thema = "Team-Meeting",
        audioFileName = "2026-06-29_Meeting - Team-Meeting.wav",
        transcriptFileName = "2026-06-29_Meeting - Team-Meeting.md",
        durationMs = 2_700_000,
    )

    private val analysis = MeetingAnalysis(
        summary = "Kurzes Team-Meeting zu Projekt X.",
        participants = listOf("Anna", "Bernd"),
        agenda = listOf("Status Projekt X"),
        topics = listOf(TopicPoint("Status Projekt X", "Diskussion zum Fortschritt.", "Fortschritt ok.")),
        decisions = listOf("Release am Freitag."),
        actionItems = listOf(
            ActionItem("Testbericht erstellen", responsible = "Anna", dueDate = "2026-07-05", concernsMe = true),
        ),
        followUps = listOf(FollowUp("Folge-Meeting", "2026-07-12 09:00", listOf("Anna", "Bernd"))),
        openPoints = listOf("Budget klären"),
    )

    @Test
    fun compose_includesAllSectionsAndData() {
        val docx = MeetingMinutesComposer.compose(meta, analysis)
        val xml = DocxTestUtil.readEntry(docx, "word/document.xml")

        assertTrue(xml.contains("BESPRECHUNGSPROTOKOLL"))
        assertTrue(xml.contains("Meeting – Team-Meeting"))
        assertTrue(xml.contains("Kurzes Team-Meeting zu Projekt X."))
        assertTrue(xml.contains("Release am Freitag."))
        assertTrue(xml.contains("Testbericht erstellen"))
        assertTrue(xml.contains("Anna"))
        assertTrue(xml.contains("2026-07-05"))
        assertTrue(xml.contains("Folge-Meeting"))
        assertTrue(xml.contains("Budget klären"))
        // Dauer 45:00 (2.700.000 ms)
        assertTrue(xml.contains("45:00"))
    }

    @Test
    fun compose_handlesEmptyAnalysisGracefully() {
        val docx = MeetingMinutesComposer.compose(meta, MeetingAnalysis())
        val xml = DocxTestUtil.readEntry(docx, "word/document.xml")
        assertTrue(xml.contains("BESPRECHUNGSPROTOKOLL"))
        assertTrue(xml.contains("Action Items"))
    }
}
