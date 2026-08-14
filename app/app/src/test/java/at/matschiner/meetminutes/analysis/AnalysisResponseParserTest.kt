package at.matschiner.meetminutes.analysis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalysisResponseParserTest {

    @Test
    fun parse_mapsAllSections() {
        val json = """
            {
              "details": {"location": "Büro", "start_time": "10:00", "end_time": "10:45"},
              "participants": [{"name": "Anna", "role": "PL", "present": "ja"}],
              "agenda": [{"topic": "Status", "responsible": "Anna", "start_time": "10:00", "duration": "10 min"}],
              "previous_summary": "Rückblick auf 22.06.",
              "previous_action_items": [{"description": "Freigabe", "responsible": "Anna", "status": "abgeschlossen"}],
              "discussion_points": [{"title": "Release", "notes": "Entscheidung: Freitag"}],
              "action_items": [
                {"description": "Bericht", "responsible": "Clara", "due_date": "2026-07-05", "concerns_me": true}
              ],
              "decisions": ["Release Freitag"],
              "risks": [{"risk": "API-Lücke", "mitigation": "Audit"}],
              "next_steps": ["QA starten"],
              "other_topics": [{"item": "Team-Event", "description": "kurz", "result": "Termin folgt"}],
              "milestones": [{"name": "Release", "date": "2026-07-03"}],
              "summary": "Kurzes Meeting.",
              "next_meeting": {"date": "2026-07-06", "time": "10:00", "location": "Büro"}
            }
        """.trimIndent()

        val a = AnalysisResponseParser.parse(json)

        assertEquals("Büro", a.details.location)
        assertEquals("10:45", a.details.endTime)
        assertEquals("Anna", a.participants[0].name)
        assertEquals("PL", a.participants[0].role)
        assertEquals("Status", a.agenda[0].topic)
        assertEquals("10 min", a.agenda[0].duration)
        assertEquals("Rückblick auf 22.06.", a.previousSummary)
        assertEquals("abgeschlossen", a.previousActionItems[0].status)
        assertEquals("Entscheidung: Freitag", a.discussionPoints[0].notes)
        assertEquals("Clara", a.actionItems[0].responsible)
        assertEquals("2026-07-05", a.actionItems[0].dueDate)
        assertTrue(a.actionItems[0].concernsMe)
        assertEquals(listOf("Release Freitag"), a.decisions)
        assertEquals("Audit", a.risks[0].mitigation)
        assertEquals(listOf("QA starten"), a.nextSteps)
        assertEquals("Termin folgt", a.otherTopics[0].result)
        assertEquals("2026-07-03", a.milestones[0].date)
        assertEquals("Kurzes Meeting.", a.summary)
        assertEquals("2026-07-06", a.nextMeeting.date)
    }

    @Test
    fun parse_handlesMissingOptionalSections() {
        val a = AnalysisResponseParser.parse("""{"summary": "Nur Zusammenfassung."}""")
        assertEquals("Nur Zusammenfassung.", a.summary)
        assertTrue(a.actionItems.isEmpty())
        assertTrue(a.participants.isEmpty())
        assertTrue(a.risks.isEmpty())
        assertTrue(a.previousSummary.isEmpty())
        assertNull(a.details.location)
        assertNull(a.nextMeeting.date)
    }

    @Test
    fun parse_skipsEntriesWithoutRequiredField() {
        val json = """
            {
              "summary": "x",
              "action_items": [{"responsible": "Anna"}, {"description": "Echt"}],
              "risks": [{"mitigation": "ohne Risiko"}, {"risk": "Echtes Risiko"}],
              "participants": [{"role": "ohne Name"}, {"name": "Bernd"}]
            }
        """.trimIndent()

        val a = AnalysisResponseParser.parse(json)

        assertEquals(1, a.actionItems.size)
        assertEquals("Echt", a.actionItems[0].description)
        assertFalse(a.actionItems[0].concernsMe)
        assertEquals(1, a.risks.size)
        assertEquals("Echtes Risiko", a.risks[0].risk)
        assertEquals(1, a.participants.size)
        assertEquals("Bernd", a.participants[0].name)
    }
}
