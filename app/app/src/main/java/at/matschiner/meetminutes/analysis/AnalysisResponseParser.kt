package at.matschiner.meetminutes.analysis

import org.json.JSONArray
import org.json.JSONObject

/**
 * Wandelt die vom Tool gelieferte JSON-Struktur in [MeetingAnalysis] um
 * (13-Abschnitte-Vorlage). Reine Logik auf Basis von org.json – testbar.
 */
object AnalysisResponseParser {

    fun parse(inputJson: String): MeetingAnalysis = fromObject(JSONObject(inputJson))

    fun fromObject(obj: JSONObject): MeetingAnalysis = MeetingAnalysis(
        details = details(obj.optJSONObject("details")),
        participants = participants(obj.optJSONArray("participants")),
        agenda = agenda(obj.optJSONArray("agenda")),
        previousSummary = obj.optString("previous_summary", ""),
        previousActionItems = previousActionItems(obj.optJSONArray("previous_action_items")),
        discussionPoints = discussionPoints(obj.optJSONArray("discussion_points")),
        actionItems = actionItems(obj.optJSONArray("action_items")),
        decisions = stringList(obj.optJSONArray("decisions")),
        risks = risks(obj.optJSONArray("risks")),
        nextSteps = stringList(obj.optJSONArray("next_steps")),
        otherTopics = otherTopics(obj.optJSONArray("other_topics")),
        milestones = milestones(obj.optJSONArray("milestones")),
        summary = obj.optString("summary", ""),
        nextMeeting = nextMeeting(obj.optJSONObject("next_meeting")),
    )

    private fun JSONObject.opt(key: String): String? =
        optString(key).takeIf { it.isNotBlank() }

    private fun stringList(arr: JSONArray?): List<String> {
        if (arr == null) return emptyList()
        return (0 until arr.length()).map { arr.optString(it) }.filter { it.isNotBlank() }
    }

    /** Iteriert Objekte eines Arrays und mappt sie; null-Ergebnisse werden verworfen. */
    private fun <T> mapObjects(arr: JSONArray?, transform: (JSONObject) -> T?): List<T> {
        if (arr == null) return emptyList()
        return (0 until arr.length()).mapNotNull { i ->
            arr.optJSONObject(i)?.let(transform)
        }
    }

    private fun details(o: JSONObject?): MeetingDetails {
        if (o == null) return MeetingDetails()
        return MeetingDetails(
            location = o.opt("location"),
            startTime = o.opt("start_time"),
            endTime = o.opt("end_time"),
        )
    }

    private fun participants(arr: JSONArray?): List<Participant> = mapObjects(arr) { o ->
        val name = o.opt("name") ?: return@mapObjects null
        Participant(name = name, role = o.opt("role"), present = o.opt("present"))
    }

    private fun agenda(arr: JSONArray?): List<AgendaItem> = mapObjects(arr) { o ->
        val topic = o.opt("topic") ?: return@mapObjects null
        AgendaItem(
            topic = topic,
            responsible = o.opt("responsible"),
            startTime = o.opt("start_time"),
            duration = o.opt("duration"),
        )
    }

    private fun previousActionItems(arr: JSONArray?): List<PreviousActionItem> =
        mapObjects(arr) { o ->
            val desc = o.opt("description") ?: return@mapObjects null
            PreviousActionItem(
                description = desc,
                responsible = o.opt("responsible"),
                status = o.opt("status"),
            )
        }

    private fun discussionPoints(arr: JSONArray?): List<DiscussionPoint> = mapObjects(arr) { o ->
        val title = o.opt("title") ?: return@mapObjects null
        DiscussionPoint(title = title, notes = o.opt("notes"))
    }

    private fun actionItems(arr: JSONArray?): List<ActionItem> = mapObjects(arr) { o ->
        val desc = o.opt("description") ?: return@mapObjects null
        ActionItem(
            description = desc,
            responsible = o.opt("responsible"),
            dueDate = o.opt("due_date"),
            concernsMe = o.optBoolean("concerns_me", false),
        )
    }

    private fun risks(arr: JSONArray?): List<Risk> = mapObjects(arr) { o ->
        val risk = o.opt("risk") ?: return@mapObjects null
        Risk(risk = risk, mitigation = o.opt("mitigation"))
    }

    private fun otherTopics(arr: JSONArray?): List<OtherTopic> = mapObjects(arr) { o ->
        val item = o.opt("item") ?: return@mapObjects null
        OtherTopic(item = item, description = o.opt("description"), result = o.opt("result"))
    }

    private fun milestones(arr: JSONArray?): List<Milestone> = mapObjects(arr) { o ->
        val name = o.opt("name") ?: return@mapObjects null
        Milestone(name = name, date = o.opt("date"))
    }

    private fun nextMeeting(o: JSONObject?): NextMeeting {
        if (o == null) return NextMeeting()
        return NextMeeting(
            date = o.opt("date"),
            time = o.opt("time"),
            location = o.opt("location"),
        )
    }
}
