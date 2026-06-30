package at.matschiner.meetminutes.core.naming

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Erzeugt Dateinamen gemäß der verbindlichen Nomenklatur des Pflichtenhefts:
 *
 *     {Datum}_{Art der Besprechung} - {Thema}.{ext}
 *
 * - Datum: JJJJ-MM-TT (ISO 8601)
 * - Für das Dateisystem unzulässige Zeichen werden sicher ersetzt.
 * - Bei Namenskollision wird ein Zähler " (n)" angehängt.
 */
object MeetingFileNamer {

    private val DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val ILLEGAL_CHARS = Regex("[\\\\/:*?\"<>|\\u0000-\\u001F]")
    private val MULTI_WHITESPACE = Regex("\\s+")

    /** Basisname ohne Endung, z. B. "2026-06-29_Meeting - Team-Meeting". */
    fun baseName(date: LocalDate, art: String, thema: String): String {
        val datePart = date.format(DATE_FORMAT)
        return "${datePart}_${sanitize(art)} - ${sanitize(thema)}"
    }

    /** Vollständiger Dateiname inkl. Endung (mit oder ohne führenden Punkt). */
    fun fileName(date: LocalDate, art: String, thema: String, extension: String): String {
        val ext = extension.trimStart('.').trim()
        return "${baseName(date, art, thema)}.$ext"
    }

    /**
     * Liefert einen kollisionsfreien Basisnamen. Ist [desired] bereits in
     * [existing] enthalten, wird " (2)", " (3)", … angehängt.
     */
    fun uniqueBaseName(desired: String, existing: Set<String>): String {
        if (desired !in existing) return desired
        var counter = 2
        while ("$desired ($counter)" in existing) counter++
        return "$desired ($counter)"
    }

    /**
     * Ersetzt die Dateiendung eines Dateinamens (z. B. ".wav" → ".md"),
     * ohne den Basisnamen zu verändern. So teilen sich alle drei Artefakte
     * einer Besprechung denselben Basisnamen.
     */
    fun withExtension(fileName: String, extension: String): String {
        val ext = extension.trimStart('.').trim()
        val dot = fileName.lastIndexOf('.')
        val base = if (dot > 0) fileName.substring(0, dot) else fileName
        return "$base.$ext"
    }

    private fun sanitize(value: String): String =
        value.replace(ILLEGAL_CHARS, "-")
            .replace(MULTI_WHITESPACE, " ")
            .trim()
            .ifEmpty { "Unbenannt" }
}
