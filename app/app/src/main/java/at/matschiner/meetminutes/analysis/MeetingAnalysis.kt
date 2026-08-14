package at.matschiner.meetminutes.analysis

/** Teilnehmer mit Rolle und Anwesenheit (Abschnitt 1). */
data class Participant(
    val name: String,
    val role: String? = null,
    val present: String? = null,
)

/** Tagesordnungspunkt (Abschnitt 2). */
data class AgendaItem(
    val topic: String,
    val responsible: String? = null,
    val startTime: String? = null,
    val duration: String? = null,
)

/** Aktionspunkt aus dem vorherigen Meeting (Abschnitt 3). */
data class PreviousActionItem(
    val description: String,
    val responsible: String? = null,
    val status: String? = null,
)

/** Diskussionspunkt: TOP + Anmerkungen/Entscheidung (Abschnitt 4). */
data class DiscussionPoint(
    val title: String,
    val notes: String? = null,
)

/** Aufgabe (Abschnitt 5). */
data class ActionItem(
    val description: String,
    val responsible: String? = null,
    val dueDate: String? = null,
    val concernsMe: Boolean = false,
)

/** Risiko + Milderungsplan (Abschnitt 7). */
data class Risk(
    val risk: String,
    val mitigation: String? = null,
)

/** Sonstiges Thema (Abschnitt 9). */
data class OtherTopic(
    val item: String,
    val description: String? = null,
    val result: String? = null,
)

/** Bevorstehender Meilenstein (Abschnitt 10). */
data class Milestone(
    val name: String,
    val date: String? = null,
)

/** Eckdaten, die aus dem Gespräch kommen können (Abschnitt 1). */
data class MeetingDetails(
    val location: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
)

/** Nächstes Meeting (Abschnitt 11). */
data class NextMeeting(
    val date: String? = null,
    val time: String? = null,
    val location: String? = null,
)

/**
 * Strukturiertes Analyseergebnis, ausgelegt auf die 13-Abschnitte-MoM-Vorlage
 * (siehe docs/04_MoM-Vorlage.md). Nicht ableitbare Abschnitte bleiben leer.
 */
data class MeetingAnalysis(
    val details: MeetingDetails = MeetingDetails(),
    val participants: List<Participant> = emptyList(),
    val agenda: List<AgendaItem> = emptyList(),
    val previousSummary: String = "",
    val previousActionItems: List<PreviousActionItem> = emptyList(),
    val discussionPoints: List<DiscussionPoint> = emptyList(),
    val actionItems: List<ActionItem> = emptyList(),
    val decisions: List<String> = emptyList(),
    val risks: List<Risk> = emptyList(),
    val nextSteps: List<String> = emptyList(),
    val otherTopics: List<OtherTopic> = emptyList(),
    val milestones: List<Milestone> = emptyList(),
    val summary: String = "",
    val nextMeeting: NextMeeting = NextMeeting(),
)
