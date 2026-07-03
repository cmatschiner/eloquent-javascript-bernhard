package at.matschiner.meetminutes.analysis

/**
 * Baut System-Prompt und das Tool-Schema (strukturierte Ausgabe) für die
 * Claude-API-Analyse. Reine Logik – testbar.
 */
object AnalysisPrompt {

    const val TOOL_NAME = "besprechungsprotokoll"

    fun systemPrompt(context: AnalysisContext): String = buildString {
        append("Du bist ein präziser Protokoll-Assistent. Analysiere das Transkript einer ")
        append("Besprechung (Art: ${context.art}, Thema: ${context.thema}, Datum: ${context.date}) ")
        append("und extrahiere die Inhalte für ein Ergebnisprotokoll (Minutes of Meeting).\n")
        append("Regeln:\n")
        append("- Sprache der Ausgabe: Deutsch.\n")
        append("- Jede Aufgabe (Action Item) braucht möglichst Verantwortlichen und Frist.\n")
        append("- 'concerns_me' = true, wenn die Aufgabe die aufnehmende Person (Ich-Perspektive) betrifft.\n")
        append("- Beschlüsse klar und knapp; nur tatsächlich Vereinbartes.\n")
        append("- Wenn Informationen fehlen, Felder leer lassen statt zu erfinden.\n")
        append("- Antworte AUSSCHLIESSLICH über das Tool '${TOOL_NAME}'.")
    }

    /** JSON-Schema (input_schema) für das erzwungene Tool. */
    fun toolInputSchema(): String =
        """
        {
          "type": "object",
          "properties": {
            "summary": { "type": "string", "description": "Kurze Zusammenfassung (3-6 Sätze)" },
            "participants": { "type": "array", "items": { "type": "string" } },
            "agenda": { "type": "array", "items": { "type": "string" } },
            "topics": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "title": { "type": "string" },
                  "discussion": { "type": "string" },
                  "result": { "type": "string" }
                },
                "required": ["title"]
              }
            },
            "decisions": { "type": "array", "items": { "type": "string" } },
            "action_items": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "description": { "type": "string" },
                  "responsible": { "type": "string" },
                  "due_date": { "type": "string", "description": "JJJJ-MM-TT falls bekannt" },
                  "concerns_me": { "type": "boolean" }
                },
                "required": ["description"]
              }
            },
            "follow_ups": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "title": { "type": "string" },
                  "date_time": { "type": "string" },
                  "participants": { "type": "array", "items": { "type": "string" } }
                },
                "required": ["title"]
              }
            },
            "open_points": { "type": "array", "items": { "type": "string" } }
          },
          "required": ["summary"]
        }
        """.trimIndent()
}
