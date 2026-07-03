package at.matschiner.meetminutes.analysis

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalysisResponseParserTest {

    @Test
    fun parse_mapsAllFields() {
        val json = """
            {
              "summary": "Kurzes Meeting.",
              "participants": ["Anna", "Bernd"],
              "agenda": ["Status"],
              "topics": [{"title": "Status", "discussion": "D", "result": "R"}],
              "decisions": ["Release Freitag"],
              "action_items": [
                {"description": "Bericht", "responsible": "Anna", "due_date": "2026-07-05", "concerns_me": true}
              ],
              "follow_ups": [{"title": "Folge", "date_time": "2026-07-12 09:00", "participants": ["Anna"]}],
              "open_points": ["Budget"]
            }
        """.trimIndent()

        val a = AnalysisResponseParser.parse(json)
        assertEquals("Kurzes Meeting.", a.summary)
        assertEquals(listOf("Anna", "Bernd"), a.participants)
        assertEquals(1, a.topics.size)
        assertEquals("R", a.topics[0].result)
        assertEquals(listOf("Release Freitag"), a.decisions)
        assertEquals(1, a.actionItems.size)
        assertEquals("Anna", a.actionItems[0].responsible)
        assertEquals("2026-07-05", a.actionItems[0].dueDate)
        assertTrue(a.actionItems[0].concernsMe)
        assertEquals("Folge", a.followUps[0].title)
        assertEquals(listOf("Budget"), a.openPoints)
    }

    @Test
    fun parse_handlesMissingOptionalFields() {
        val a = AnalysisResponseParser.parse("""{"summary": "Nur Zusammenfassung."}""")
        assertEquals("Nur Zusammenfassung.", a.summary)
        assertTrue(a.actionItems.isEmpty())
        assertTrue(a.participants.isEmpty())
    }

    @Test
    fun parse_skipsActionItemsWithoutDescription() {
        val json = """{"summary":"x","action_items":[{"responsible":"Anna"},{"description":"Echt"}]}"""
        val a = AnalysisResponseParser.parse(json)
        assertEquals(1, a.actionItems.size)
        assertEquals("Echt", a.actionItems[0].description)
        assertFalse(a.actionItems[0].concernsMe)
    }
}
