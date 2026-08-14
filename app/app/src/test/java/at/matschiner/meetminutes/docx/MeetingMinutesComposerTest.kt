package at.matschiner.meetminutes.docx

import at.matschiner.meetminutes.analysis.ActionItem
import at.matschiner.meetminutes.analysis.AgendaItem
import at.matschiner.meetminutes.analysis.DiscussionPoint
import at.matschiner.meetminutes.analysis.MeetingAnalysis
import at.matschiner.meetminutes.analysis.MeetingDetails
import at.matschiner.meetminutes.analysis.Milestone
import at.matschiner.meetminutes.analysis.NextMeeting
import at.matschiner.meetminutes.analysis.OtherTopic
import at.matschiner.meetminutes.analysis.Participant
import at.matschiner.meetminutes.analysis.PreviousActionItem
import at.matschiner.meetminutes.analysis.Risk
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
        details = MeetingDetails(location = "Büro", startTime = "10:00", endTime = "10:45"),
        participants = listOf(
            Participant("Anna Berger", "Projektleitung", "ja"),
            Participant("Bernd Huber", "Entwicklung", "ja"),
        ),
        agenda = listOf(AgendaItem("Status Projekt X", "Anna", "10:00", "10 min")),
        previousSummary = "Durchsicht der offenen Punkte vom 2026-06-22.",
        previousActionItems = listOf(
            PreviousActionItem("Prototyp-Freigabe einholen", "Anna", "abgeschlossen"),
        ),
        discussionPoints = listOf(
            DiscussionPoint("Release-Planung", "Termin besprochen. Entscheidung: 2026-07-03."),
        ),
        actionItems = listOf(
            ActionItem("Testbericht erstellen", "Clara", "2026-07-01", concernsMe = true),
        ),
        decisions = listOf("Release am 2026-07-03."),
        risks = listOf(Risk("Sicherheitslücke in API", "Sicherheitsprüfung durchführen")),
        nextSteps = listOf("Entwicklung bis 2026-07-01 abschließen."),
        otherTopics = listOf(OtherTopic("Team-Event", "Kurz besprochen", "Termin folgt")),
        milestones = listOf(Milestone("Release Projekt X", "2026-07-03")),
        summary = "Fortschritt im Plan, Release bestätigt.",
        nextMeeting = NextMeeting("2026-07-06", "10:00", "Büro"),
    )

    @Test
    fun compose_containsAllThirteenSectionHeaders() {
        val xml = DocxTestUtil.readEntry(MeetingMinutesComposer.compose(meta, analysis), DOC)
        val headers = listOf(
            "1. MEETINGDETAILS",
            "2. AGENDA",
            "3. BESPRECHUNG DES VORHERIGEN MEETINGS",
            "4. DISKUSSIONSPUNKTE",
            "5. AKTIONSPUNKTE",
            "6. GETROFFENE ENTSCHEIDUNGEN",
            "7. RISIKEN UND PROBLEME",
            "8. NÄCHSTE SCHRITTE",
            "9. SONSTIGE THEMEN",
            "10. BEVORSTEHENDE MEILENSTEINE",
            "11. FAZIT DES MEETINGS",
            "12. ANLAGEN ODER HILFSMATERIALIEN",
            "13. GENEHMIGUNG UND UNTERSCHRIFTEN",
        )
        for (h in headers) {
            assertTrue("Abschnitt fehlt: $h", xml.contains(h))
        }
    }

    @Test
    fun compose_includesAnalysisData() {
        val xml = DocxTestUtil.readEntry(MeetingMinutesComposer.compose(meta, analysis), DOC)
        assertTrue(xml.contains("MEETINGPROTOKOLL"))
        assertTrue(xml.contains("Meeting – Team-Meeting"))
        assertTrue(xml.contains("Anna Berger"))
        assertTrue(xml.contains("Status Projekt X"))
        assertTrue(xml.contains("Prototyp-Freigabe einholen"))
        assertTrue(xml.contains("Release-Planung"))
        assertTrue(xml.contains("Testbericht erstellen"))
        assertTrue(xml.contains("2026-07-01"))
        assertTrue(xml.contains("Sicherheitsprüfung durchführen"))
        assertTrue(xml.contains("Team-Event"))
        assertTrue(xml.contains("Release Projekt X"))
        assertTrue(xml.contains("Fortschritt im Plan"))
        assertTrue(xml.contains("2026-07-06"))
        // Dauer 45:00 (2.700.000 ms)
        assertTrue(xml.contains("45:00"))
    }

    @Test
    fun compose_emptyAnalysisStillRendersAllSections() {
        val xml = DocxTestUtil.readEntry(
            MeetingMinutesComposer.compose(meta, MeetingAnalysis()),
            DOC,
        )
        assertTrue(xml.contains("1. MEETINGDETAILS"))
        assertTrue(xml.contains("13. GENEHMIGUNG UND UNTERSCHRIFTEN"))
        // Anlagen werden immer aus den Metadaten befüllt
        assertTrue(xml.contains("2026-06-29_Meeting - Team-Meeting.md"))
    }

    private companion object {
        const val DOC = "word/document.xml"
    }
}
