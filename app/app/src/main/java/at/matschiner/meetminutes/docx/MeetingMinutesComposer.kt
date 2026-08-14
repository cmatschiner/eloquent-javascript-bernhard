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
 * Erzeugt das Besprechungsprotokoll als DOCX in der verbindlichen
 * 13-Abschnitte-Vorlage (siehe docs/04_MoM-Vorlage.md).
 *
 * Abschnitte, die aus einer einzelnen Aufnahme nicht ableitbar sind, bleiben als
 * leeres Gerüst stehen und können in der App ergänzt werden.
 */
object MeetingMinutesComposer {

    private val DATE = DateTimeFormatter.ISO_LOCAL_DATE
    private const val EMPTY = "—"

    fun compose(meta: ProtocolMeta, analysis: MeetingAnalysis): ByteArray {
        val doc = DocxBuilder()

        doc.heading("MEETINGPROTOKOLL", level = 1)
        doc.heading("${meta.art} – ${meta.thema}  ·  ${meta.date.format(DATE)}", level = 2)

        section1Details(doc, meta, analysis)
        section2Agenda(doc, analysis)
        section3Previous(doc, analysis)
        section4Discussion(doc, analysis)
        section5ActionItems(doc, analysis)
        section6Decisions(doc, analysis)
        section7Risks(doc, analysis)
        section8NextSteps(doc, analysis)
        section9OtherTopics(doc, analysis)
        section10Milestones(doc, analysis)
        section11Conclusion(doc, meta, analysis)
        section12Attachments(doc, meta)
        section13Signatures(doc, analysis)

        doc.paragraph(
            "Erstellt mit MeetMinutes am ${meta.date.format(DATE)}. " +
                "KI-generiert – bitte inhaltlich prüfen.",
        )
        return doc.build()
    }

    private fun section1Details(doc: DocxBuilder, meta: ProtocolMeta, a: MeetingAnalysis) {
        doc.sectionBar("1. MEETINGDETAILS")
        doc.table(
            headers = listOf("DATUM", "ORT", "STARTZEIT", "ENDZEIT", "DAUER"),
            rows = listOf(
                listOf(
                    meta.date.format(DATE),
                    a.details.location ?: EMPTY,
                    a.details.startTime ?: EMPTY,
                    a.details.endTime ?: EMPTY,
                    TimeFormat.mmss(meta.durationMs),
                ),
            ),
        )
        doc.table(
            headers = listOf("NAME", "ROLLE", "ANWESEND"),
            rows = a.participants
                .map { listOf(it.name, it.role ?: EMPTY, it.present ?: "ja") }
                .ifEmpty { listOf(listOf(EMPTY, EMPTY, EMPTY)) },
        )
    }

    private fun section2Agenda(doc: DocxBuilder, a: MeetingAnalysis) {
        doc.sectionBar("2. AGENDA")
        doc.table(
            headers = listOf("TAGESORDNUNGSPUNKT", "VERANTWORTLICH", "STARTZEIT", "DAUER"),
            rows = a.agenda
                .map {
                    listOf(
                        it.topic,
                        it.responsible ?: EMPTY,
                        it.startTime ?: EMPTY,
                        it.duration ?: EMPTY,
                    )
                }
                .ifEmpty { listOf(listOf(EMPTY, EMPTY, EMPTY, EMPTY)) },
        )
    }

    private fun section3Previous(doc: DocxBuilder, a: MeetingAnalysis) {
        doc.sectionBar("3. BESPRECHUNG DES VORHERIGEN MEETINGS")
        doc.labeled("Zusammenfassung", a.previousSummary.ifBlank { EMPTY })
        doc.table(
            headers = listOf("AKTIONSPUNKT (VORHERIG)", "VERANTWORTLICH", "STATUS"),
            rows = a.previousActionItems
                .map { listOf(it.description, it.responsible ?: EMPTY, it.status ?: EMPTY) }
                .ifEmpty { listOf(listOf(EMPTY, EMPTY, EMPTY)) },
        )
    }

    private fun section4Discussion(doc: DocxBuilder, a: MeetingAnalysis) {
        doc.sectionBar("4. DISKUSSIONSPUNKTE")
        doc.table(
            headers = listOf("TAGESORDNUNGSPUNKT", "ANMERKUNGEN ZUR DISKUSSION / ENTSCHEIDUNG"),
            rows = a.discussionPoints
                .map { listOf(it.title, it.notes ?: EMPTY) }
                .ifEmpty { listOf(listOf(EMPTY, EMPTY)) },
        )
    }

    private fun section5ActionItems(doc: DocxBuilder, a: MeetingAnalysis) {
        doc.sectionBar("5. AKTIONSPUNKTE")
        doc.table(
            headers = listOf("AKTIONSPUNKT", "INHABER", "FÄLLIGKEIT", "MICH BETR."),
            rows = a.actionItems
                .map {
                    listOf(
                        it.description,
                        it.responsible ?: EMPTY,
                        it.dueDate ?: EMPTY,
                        if (it.concernsMe) "ja" else EMPTY,
                    )
                }
                .ifEmpty { listOf(listOf(EMPTY, EMPTY, EMPTY, EMPTY)) },
        )
    }

    private fun section6Decisions(doc: DocxBuilder, a: MeetingAnalysis) {
        doc.sectionBar("6. GETROFFENE ENTSCHEIDUNGEN")
        doc.table(
            headers = listOf("ENTSCHEIDUNG (INKL. BEGRÜNDUNG)"),
            rows = a.decisions.map { listOf(it) }.ifEmpty { listOf(listOf(EMPTY)) },
        )
    }

    private fun section7Risks(doc: DocxBuilder, a: MeetingAnalysis) {
        doc.sectionBar("7. RISIKEN UND PROBLEME")
        doc.table(
            headers = listOf("RISIKO ODER PROBLEM", "MILDERUNGSPLAN"),
            rows = a.risks
                .map { listOf(it.risk, it.mitigation ?: EMPTY) }
                .ifEmpty { listOf(listOf(EMPTY, EMPTY)) },
        )
    }

    private fun section8NextSteps(doc: DocxBuilder, a: MeetingAnalysis) {
        doc.sectionBar("8. NÄCHSTE SCHRITTE")
        doc.table(
            headers = listOf("NÄCHSTE SCHRITTE"),
            rows = a.nextSteps.map { listOf(it) }.ifEmpty { listOf(listOf(EMPTY)) },
        )
    }

    private fun section9OtherTopics(doc: DocxBuilder, a: MeetingAnalysis) {
        doc.sectionBar("9. SONSTIGE THEMEN")
        doc.table(
            headers = listOf("WEITERES ELEMENT", "BESCHREIBUNG", "ERGEBNIS"),
            rows = a.otherTopics
                .map { listOf(it.item, it.description ?: EMPTY, it.result ?: EMPTY) }
                .ifEmpty { listOf(listOf(EMPTY, EMPTY, EMPTY)) },
        )
    }

    private fun section10Milestones(doc: DocxBuilder, a: MeetingAnalysis) {
        doc.sectionBar("10. BEVORSTEHENDE MEILENSTEINE")
        doc.table(
            headers = listOf("MEILENSTEIN", "TERMIN"),
            rows = a.milestones
                .map { listOf(it.name, it.date ?: EMPTY) }
                .ifEmpty { listOf(listOf(EMPTY, EMPTY)) },
        )
    }

    private fun section11Conclusion(doc: DocxBuilder, meta: ProtocolMeta, a: MeetingAnalysis) {
        doc.sectionBar("11. FAZIT DES MEETINGS")
        doc.labeled("Zusammenfassung", a.summary.ifBlank { EMPTY })
        doc.table(
            headers = listOf("DATUM NÄCHSTES MEETING", "UHRZEIT", "ORT"),
            rows = listOf(
                listOf(
                    a.nextMeeting.date ?: EMPTY,
                    a.nextMeeting.time ?: EMPTY,
                    a.nextMeeting.location ?: EMPTY,
                ),
            ),
        )
    }

    private fun section12Attachments(doc: DocxBuilder, meta: ProtocolMeta) {
        doc.sectionBar("12. ANLAGEN ODER HILFSMATERIALIEN")
        doc.table(
            headers = listOf("MATERIAL / LINK"),
            rows = listOf(
                listOf("Transkript: ${meta.transcriptFileName}"),
                listOf("Audio: ${meta.audioFileName}"),
            ),
        )
    }

    private fun section13Signatures(doc: DocxBuilder, a: MeetingAnalysis) {
        doc.sectionBar("13. GENEHMIGUNG UND UNTERSCHRIFTEN")
        doc.table(
            headers = listOf("NAME DES TEILNEHMERS", "UNTERSCHRIFT"),
            rows = a.participants
                .map { listOf(it.name, " ") }
                .ifEmpty { listOf(listOf(EMPTY, " ")) },
        )
    }
}
