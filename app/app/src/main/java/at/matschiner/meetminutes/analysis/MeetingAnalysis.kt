package at.matschiner.meetminutes.analysis

/** Eine Aufgabe (To-Do) mit Verantwortlichem und Frist. */
data class ActionItem(
    val description: String,
    val responsible: String? = null,
    val dueDate: String? = null, // ISO JJJJ-MM-TT oder frei, null = offen
    val concernsMe: Boolean = false,
)

/** Ein Follow-up-Termin. */
data class FollowUp(
    val title: String,
    val dateTime: String? = null,
    val participants: List<String> = emptyList(),
)

/** Ein Tagesordnungspunkt mit Diskussion und Ergebnis. */
data class TopicPoint(
    val title: String,
    val discussion: String? = null,
    val result: String? = null,
)

/**
 * Strukturiertes Ergebnis der Inhaltsanalyse (Ergebnisprotokoll-Stil).
 * Quelle: Claude API (Sprint 3.2). In Sprint 3.1 wird daraus das DOCX erzeugt.
 */
data class MeetingAnalysis(
    val summary: String = "",
    val participants: List<String> = emptyList(),
    val agenda: List<String> = emptyList(),
    val topics: List<TopicPoint> = emptyList(),
    val decisions: List<String> = emptyList(),
    val actionItems: List<ActionItem> = emptyList(),
    val followUps: List<FollowUp> = emptyList(),
    val openPoints: List<String> = emptyList(),
)
