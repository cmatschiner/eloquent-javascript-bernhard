package at.matschiner.meetminutes.docx

import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream

/** Liest einen benannten Eintrag aus einem DOCX/ZIP-Byte-Array als String. */
object DocxTestUtil {
    fun entryNames(docx: ByteArray): List<String> {
        val names = mutableListOf<String>()
        ZipInputStream(ByteArrayInputStream(docx)).use { zin ->
            var e = zin.nextEntry
            while (e != null) {
                names.add(e.name)
                e = zin.nextEntry
            }
        }
        return names
    }

    fun readEntry(docx: ByteArray, name: String): String {
        ZipInputStream(ByteArrayInputStream(docx)).use { zin ->
            var e = zin.nextEntry
            while (e != null) {
                if (e.name == name) return zin.readBytes().toString(Charsets.UTF_8)
                e = zin.nextEntry
            }
        }
        error("Eintrag nicht gefunden: $name")
    }
}
