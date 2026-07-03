package at.matschiner.meetminutes.docx

import org.junit.Assert.assertTrue
import org.junit.Test

class DocxBuilderTest {

    @Test
    fun build_producesValidDocxParts() {
        val docx = DocxBuilder().heading("Titel").paragraph("Hallo").build()
        val names = DocxTestUtil.entryNames(docx)
        assertTrue("[Content_Types].xml" in names)
        assertTrue("_rels/.rels" in names)
        assertTrue("word/document.xml" in names)
    }

    @Test
    fun documentContainsTextAndTable() {
        val docx = DocxBuilder()
            .heading("Protokoll")
            .paragraph("Ein Absatz")
            .table(listOf("A", "B"), listOf(listOf("1", "2")))
            .build()
        val xml = DocxTestUtil.readEntry(docx, "word/document.xml")
        assertTrue(xml.contains("Protokoll"))
        assertTrue(xml.contains("Ein Absatz"))
        assertTrue(xml.contains("<w:tbl>"))
        assertTrue(xml.contains(">A<") || xml.contains("A</w:t>"))
    }

    @Test
    fun specialCharactersAreEscaped() {
        val docx = DocxBuilder().paragraph("Müller & Co <Test>").build()
        val xml = DocxTestUtil.readEntry(docx, "word/document.xml")
        assertTrue(xml.contains("Müller &amp; Co &lt;Test&gt;"))
    }
}
