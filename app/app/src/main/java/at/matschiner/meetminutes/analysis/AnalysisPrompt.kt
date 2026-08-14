package at.matschiner.meetminutes.analysis

/**
 * System-Prompt und Tool-Schema (strukturierte Ausgabe) für die Claude-Analyse.
 * Ausgelegt auf die 13-Abschnitte-MoM-Vorlage (docs/04_MoM-Vorlage.md).
 */
object AnalysisPrompt {

    const val TOOL_NAME = "besprechungsprotokoll"

    fun systemPrompt(context: AnalysisContext): String = buildString {
        append("Du bist ein präziser Protokoll-Assistent. Analysiere das Transkript einer ")
        append("Besprechung (Art: ${context.art}, Thema: ${context.thema}, ")
        append("Datum: ${context.date}) und extrahiere die Inhalte für ein ")
        append("Ergebnisprotokoll nach fester Vorlage (Minutes of Meeting).\n")
        append("Regeln:\n")
        append("- Sprache der Ausgabe: Deutsch.\n")
        append("- Erfinde nichts. Was nicht im Transkript vorkommt, bleibt leer.\n")
        append("- Aufgaben (action_items) möglichst mit Verantwortlichem und Frist ")
        append("(due_date als JJJJ-MM-TT, wenn ableitbar).\n")
        append("- 'concerns_me' = true, wenn die Aufgabe die aufnehmende Person betrifft ")
        append("(Ich-Perspektive, z. B. \"ich mache das\").\n")
        append("- 'decisions' enthält nur tatsächlich Vereinbartes, klar und knapp, ")
        append("bei Bedarf mit kurzer Begründung.\n")
        append("- 'discussion_points' fasst je Tagesordnungspunkt die Diskussion und das ")
        append("Ergebnis zusammen.\n")
        append("- 'previous_*' nur füllen, wenn im Gespräch auf ein früheres Meeting ")
        append("Bezug genommen wird.\n")
        append("- Teilnehmer: erkennbare Namen verwenden; sonst neutral (z. B. \"Sprecher 1\").\n")
        append("- Antworte AUSSCHLIESSLICH über das Tool '$TOOL_NAME'.")
    }

    /** JSON-Schema (input_schema) für das erzwungene Tool. */
    fun toolInputSchema(): String =
        """
        {
          "type": "object",
          "properties": {
            "details": {
              "type": "object",
              "description": "Eckdaten, soweit im Gespräch erwähnt",
              "properties": {
                "location": { "type": "string" },
                "start_time": { "type": "string", "description": "z. B. 10:00" },
                "end_time": { "type": "string" }
              }
            },
            "participants": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "name": { "type": "string" },
                  "role": { "type": "string" },
                  "present": { "type": "string", "description": "ja / entschuldigt" }
                },
                "required": ["name"]
              }
            },
            "agenda": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "topic": { "type": "string" },
                  "responsible": { "type": "string" },
                  "start_time": { "type": "string" },
                  "duration": { "type": "string" }
                },
                "required": ["topic"]
              }
            },
            "previous_summary": {
              "type": "string",
              "description": "Zusammenfassung zum vorherigen Meeting, falls erwähnt"
            },
            "previous_action_items": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "description": { "type": "string" },
                  "responsible": { "type": "string" },
                  "status": { "type": "string", "description": "z. B. abgeschlossen / in Bearbeitung" }
                },
                "required": ["description"]
              }
            },
            "discussion_points": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "title": { "type": "string" },
                  "notes": { "type": "string", "description": "Diskussion und Ergebnis/Entscheidung" }
                },
                "required": ["title"]
              }
            },
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
            "decisions": { "type": "array", "items": { "type": "string" } },
            "risks": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "risk": { "type": "string" },
                  "mitigation": { "type": "string" }
                },
                "required": ["risk"]
              }
            },
            "next_steps": { "type": "array", "items": { "type": "string" } },
            "other_topics": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "item": { "type": "string" },
                  "description": { "type": "string" },
                  "result": { "type": "string" }
                },
                "required": ["item"]
              }
            },
            "milestones": {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "name": { "type": "string" },
                  "date": { "type": "string" }
                },
                "required": ["name"]
              }
            },
            "summary": { "type": "string", "description": "Fazit des Meetings (3-6 Sätze)" },
            "next_meeting": {
              "type": "object",
              "properties": {
                "date": { "type": "string", "description": "JJJJ-MM-TT" },
                "time": { "type": "string" },
                "location": { "type": "string" }
              }
            }
          },
          "required": ["summary"]
        }
        """.trimIndent()
}
