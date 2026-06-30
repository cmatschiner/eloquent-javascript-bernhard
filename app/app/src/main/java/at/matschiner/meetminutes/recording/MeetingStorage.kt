package at.matschiner.meetminutes.recording

import android.content.Context
import at.matschiner.meetminutes.core.naming.MeetingFileNamer
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Verwaltet das Verzeichnis und die Dateinamen der Aufnahmen im
 * app-spezifischen Speicher (kein Speicher-Permission nötig).
 */
@Singleton
class MeetingStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    val recordingsDir: File by lazy {
        File(context.getExternalFilesDir(null) ?: context.filesDir, "recordings").apply { mkdirs() }
    }

    /** Liefert eine kollisionsfreie WAV-Zieldatei gemäß Nomenklatur. */
    fun resolveWavFile(date: LocalDate, art: String, thema: String): File {
        val existingBaseNames = recordingsDir.list()
            ?.filter { it.endsWith(".wav", ignoreCase = true) }
            ?.map { it.dropLast(4) }
            ?.toSet()
            ?: emptySet()

        val base = MeetingFileNamer.baseName(date, art, thema)
        val unique = MeetingFileNamer.uniqueBaseName(base, existingBaseNames)
        return File(recordingsDir, "$unique.wav")
    }

    /** Datei im Aufnahmeordner anhand des Dateinamens. */
    fun fileByName(fileName: String): File = File(recordingsDir, fileName)

    /** Transkript-Datei (.md) mit identischem Basisnamen wie die WAV-Datei. */
    fun transcriptFileFor(wavFile: File): File =
        File(recordingsDir, MeetingFileNamer.withExtension(wavFile.name, "md"))

    /** Schreibt das Markdown-Transkript und gibt die Datei zurück. */
    fun writeTranscript(wavFile: File, markdown: String): File =
        transcriptFileFor(wavFile).apply { writeText(markdown) }
}
