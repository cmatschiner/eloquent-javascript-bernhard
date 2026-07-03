package at.matschiner.meetminutes.analysis

import org.json.JSONArray
import org.json.JSONObject

/**
 * Wandelt die (vom Tool gelieferte) JSON-Struktur in [MeetingAnalysis] um. Reine
 * Logik auf Basis von org.json – testbar.
 */
object AnalysisResponseParser {

    fun parse(inputJson: String): MeetingAnalysis = fromObject(JSONObject(inputJson))

    fun fromObject(obj: JSONObject): MeetingAnalysis = MeetingAnalysis(
        summary = obj.optString("summary", ""),
        participants = stringList(obj.optJSONArray("participants")),
        agenda = stringList(obj.optJSONArray("agenda")),
        topics = topics(obj.optJSONArray("topics")),
        decisions = stringList(obj.optJSONArray("decisions")),
        actionItems = actionItems(obj.optJSONArray("action_items")),
        followUps = followUps(obj.optJSONArray("follow_ups")),
        openPoints = stringList(obj.optJSONArray("open_points")),
    )

    private fun stringList(arr: JSONArray?): List<String> {
        if (arr == null) return emptyList()
        return (0 until arr.length()).map { arr.optString(it) }.filter { it.isNotBlank() }
    }

    private fun topics(arr: JSONArray?): List<TopicPoint> {
        if (arr == null) return emptyList()
        return (0 until arr.length()).mapNotNull { i ->
            val o = arr.optJSONObject(i) ?: return@mapNotNull null
            val title = o.optString("title").takeIf { it.isNotBlank() } ?: return@mapNotNull null
            TopicPoint(
                title = title,
                discussion = o.optString("discussion").takeIf { it.isNotBlank() },
                result = o.optString("result").takeIf { it.isNotBlank() },
            )
        }
    }

    private fun actionItems(arr: JSONArray?): List<ActionItem> {
        if (arr == null) return emptyList()
        return (0 until arr.length()).mapNotNull { i ->
            val o = arr.optJSONObject(i) ?: return@mapNotNull null
            val desc = o.optString("description").takeIf { it.isNotBlank() } ?: return@mapNotNull null
            ActionItem(
                description = desc,
                responsible = o.optString("responsible").takeIf { it.isNotBlank() },
                dueDate = o.optString("due_date").takeIf { it.isNotBlank() },
                concernsMe = o.optBoolean("concerns_me", false),
            )
        }
    }

    private fun followUps(arr: JSONArray?): List<FollowUp> {
        if (arr == null) return emptyList()
        return (0 until arr.length()).mapNotNull { i ->
            val o = arr.optJSONObject(i) ?: return@mapNotNull null
            val title = o.optString("title").takeIf { it.isNotBlank() } ?: return@mapNotNull null
            FollowUp(
                title = title,
                dateTime = o.optString("date_time").takeIf { it.isNotBlank() },
                participants = stringList(o.optJSONArray("participants")),
            )
        }
    }
}
