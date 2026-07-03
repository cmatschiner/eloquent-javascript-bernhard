package at.matschiner.meetminutes.docx

import at.matschiner.meetminutes.analysis.MeetingAnalysis
import at.matschiner.meetminutes.core.util.TimeFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Metadaten für den Protokollkopf. */
data class ProtocolMeta(
    val date: LocalDate,
    val art: String,
    val thema: String,
    val audioFileName: String,
    val transcriptFileName: String,
    val durationMs: Long,
)

/**
 * Erzeugt das Besprechungsprotokoll (Minutes of Meeting, Ergebnisprotokoll-Stil)
 * als DOCX gemäß docs/04_MoM-Vorlage.md.
 */
object MeetingMinutesComposer {

    private val DATE = DateTimeFormatter.ISO_LOCAL_DATE
    private const val DASH = "—"

    fun compose(meta: ProtocolMeta, analysis: MeetingAnalysis): ByteArray {
        val doc = DocxBuilder()

        doc.heading("BESPRECHUNGSPROTOKOLL", level = 1)
        doc.heading("${meta.art} – ${meta.thema}", level = 2)

        // 1. Eckdaten
        doc.heading("1. Eckdaten", level = 2)
        doc.table(
            headers = listOf("Feld", "Wert"),
            rows = listOf(
                listOf("Datum", meta.date.format(DATE)),
                listOf("Art", meta.art),
                listOf("Thema", meta.thema),
                listOf("Dauer", TimeFormat.mmss(meta.durationMs)),
                listOf("Protokollführung", "MeetMinutes (KI-generiert)"),
                listOf("Audiodatei", meta.audioFileName),
            ),
        )

        // 2. Teilnehmer
        doc.heading("2. Teilnehmer", level = 2)
        if (analysis.participants.isEmpty()) {
            doc.paragraph(DASH)
        } else {
            doc.table(
                headers = listOf("Name", "Anwesend"),
                rows = analysis.participants.map { listOf(it, "ja") },
            )
        }

        // 3. Agenda / TOP
        doc.heading("3. Agenda / Tagesordnungspunkte", level = 2)
        if (analysis.agenda.isEmpty()) doc.paragraph(DASH)
        else analysis.agenda.forEachIndexed { i, top -> doc.bullet("TOP ${i + 1}: $top") }

        // 4. Zusammenfassung
        doc.heading("4. Zusammenfassung", level = 2)
        doc.paragraph(analysis.summary.ifBlank { DASH })

        // 5. Besprechungsinhalt je TOP
        doc.heading("5. Besprechungsinhalt", level = 2)
        if (analysis.topics.isEmpty()) {
            doc.paragraph(DASH)
        } else {
            analysis.topics.forEach { topic ->
                doc.heading(topic.title, level = 3)
                topic.discussion?.takeIf { it.isNotBlank() }?.let { doc.labeled("Diskussion", it) }
                topic.result?.takeIf { it.isNotBlank() }?.let { doc.labeled("Ergebnis", it) }
            }
        }

        // 6. Beschlüsse
        doc.heading("6. Beschlüsse / Entscheidungen", level = 2)
        if (analysis.decisions.isEmpty()) doc.paragraph(DASH)
        else analysis.decisions.forEach { doc.bullet(it, bold = true) }

        // 7. Action Items
        doc.heading("7. Action Items", level = 2)
        if (analysis.actionItems.isEmpty()) {
            doc.paragraph(DASH)
        } else {
            doc.table(
                headers = listOf("#", "Aufgabe", "Verantwortlich", "Frist", "Mich betr."),
                rows = analysis.actionItems.mapIndexed { i, item ->
                    listOf(
                        (i + 1).toString(),
                        item.description,
                        item.responsible ?: DASH,
                        item.dueDate ?: DASH,
                        if (item.concernsMe) "ja" else DASH,
                    )
                },
            )
        }

        // 8. Follow-up-Termine
        doc.heading("8. Follow-up-Termine", level = 2)
        if (analysis.followUps.isEmpty()) {
            doc.paragraph(DASH)
        } else {
            doc.table(
                headers = listOf("Titel", "Datum/Uhrzeit", "Teilnehmer"),
                rows = analysis.followUps.map {
                    listOf(it.title, it.dateTime ?: DASH, it.participants.joinToString(", ").ifBlank { DASH })
                },
            )
        }

        // 9. Offene Punkte
        doc.heading("9. Offene Punkte", level = 2)
        if (analysis.openPoints.isEmpty()) doc.paragraph(DASH)
        else analysis.openPoints.forEach { doc.bullet(it) }

        // 10. Anhang
        doc.heading("10. Anhang", level = 2)
        doc.labeled("Transkript", meta.transcriptFileName)
        doc.labeled("Audio", meta.audioFileName)
        doc.paragraph(
            "Erstellt mit MeetMinutes am ${meta.date.format(DATE)}. " +
                "KI-generiert – bitte inhaltlich prüfen.",
        )

        return doc.build()
    }
}
