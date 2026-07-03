package at.matschiner.meetminutes.docx

import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Minimaler, abhängigkeitsfreier Writer für DOCX (Office Open XML).
 * Eine .docx ist ein ZIP aus XML-Teilen – wir erzeugen die nötigen Teile selbst.
 * Unterstützt: Überschriften, Absätze (fett), Aufzählungen und einfache Tabellen.
 */
class DocxBuilder {

    private val body = StringBuilder()

    /** Überschrift, level 1..3. */
    fun heading(text: String, level: Int = 1): DocxBuilder {
        val size = when (level) {
            1 -> 36
            2 -> 28
            else -> 24
        }
        body.append("<w:p><w:pPr><w:spacing w:before=\"200\" w:after=\"80\"/></w:pPr>")
        body.append(run(text, bold = true, size = size))
        body.append("</w:p>")
        return this
    }

    /** Normaler Absatz (optional fett). */
    fun paragraph(text: String, bold: Boolean = false): DocxBuilder {
        body.append("<w:p>").append(run(text, bold = bold, size = 22)).append("</w:p>")
        return this
    }

    /** Absatz aus einem fetten Label und normalem Text: "Label: wert". */
    fun labeled(label: String, value: String): DocxBuilder {
        body.append("<w:p>")
            .append(run("$label: ", bold = true, size = 22))
            .append(run(value, bold = false, size = 22))
            .append("</w:p>")
        return this
    }

    /** Aufzählungspunkt. */
    fun bullet(text: String, bold: Boolean = false): DocxBuilder {
        body.append("<w:p><w:pPr><w:ind w:left=\"360\" w:hanging=\"360\"/></w:pPr>")
        body.append(run("•  ", bold = false, size = 22))
        body.append(run(text, bold = bold, size = 22))
        body.append("</w:p>")
        return this
    }

    /** Kleiner vertikaler Abstand. */
    fun spacer(): DocxBuilder {
        body.append("<w:p/>")
        return this
    }

    /** Einfache Tabelle mit Kopfzeile und Rändern. */
    fun table(headers: List<String>, rows: List<List<String>>): DocxBuilder {
        body.append("<w:tbl>")
        body.append("<w:tblPr><w:tblW w:w=\"0\" w:type=\"auto\"/>")
        body.append("<w:tblBorders>")
        for (edge in listOf("top", "left", "bottom", "right", "insideH", "insideV")) {
            body.append("<w:$edge w:val=\"single\" w:sz=\"4\" w:space=\"0\" w:color=\"999999\"/>")
        }
        body.append("</w:tblBorders></w:tblPr>")

        body.append(tableRow(headers, bold = true))
        for (row in rows) body.append(tableRow(row, bold = false))
        body.append("</w:tbl>")
        body.append("<w:p/>")
        return this
    }

    private fun tableRow(cells: List<String>, bold: Boolean): String = buildString {
        append("<w:tr>")
        for (cell in cells) {
            append("<w:tc><w:tcPr><w:tcW w:w=\"0\" w:type=\"auto\"/></w:tcPr>")
            append("<w:p>").append(run(cell, bold = bold, size = 20)).append("</w:p>")
            append("</w:tc>")
        }
        append("</w:tr>")
    }

    private fun run(text: String, bold: Boolean, size: Int): String = buildString {
        append("<w:r><w:rPr>")
        if (bold) append("<w:b/>")
        append("<w:sz w:val=\"$size\"/><w:szCs w:val=\"$size\"/>")
        append("</w:rPr>")
        append("<w:t xml:space=\"preserve\">").append(escape(text)).append("</w:t></w:r>")
    }

    /** Erzeugt die vollständige .docx als Byte-Array. */
    fun build(): ByteArray {
        val document = buildString {
            append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
            append("<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">")
            append("<w:body>")
            append(body)
            append("<w:sectPr><w:pgSz w:w=\"11906\" w:h=\"16838\"/>")
            append("<w:pgMar w:top=\"1134\" w:right=\"1134\" w:bottom=\"1134\" w:left=\"1134\"/></w:sectPr>")
            append("</w:body></w:document>")
        }

        val out = ByteArrayOutputStream()
        ZipOutputStream(out).use { zip ->
            zip.putEntry("[Content_Types].xml", CONTENT_TYPES)
            zip.putEntry("_rels/.rels", RELS)
            zip.putEntry("word/document.xml", document)
        }
        return out.toByteArray()
    }

    private fun ZipOutputStream.putEntry(name: String, content: String) {
        putNextEntry(ZipEntry(name))
        write(content.toByteArray(Charsets.UTF_8))
        closeEntry()
    }

    private fun escape(text: String): String = buildString {
        for (c in text) {
            when (c) {
                '&' -> append("&amp;")
                '<' -> append("&lt;")
                '>' -> append("&gt;")
                '"' -> append("&quot;")
                else -> append(c)
            }
        }
    }

    private companion object {
        const val CONTENT_TYPES =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">" +
                "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>" +
                "<Default Extension=\"xml\" ContentType=\"application/xml\"/>" +
                "<Override PartName=\"/word/document.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml\"/>" +
                "</Types>"

        const val RELS =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">" +
                "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"word/document.xml\"/>" +
                "</Relationships>"
    }
}
