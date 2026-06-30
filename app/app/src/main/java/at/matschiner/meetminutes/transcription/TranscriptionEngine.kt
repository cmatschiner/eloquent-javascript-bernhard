package at.matschiner.meetminutes.transcription

import java.io.File

/**
 * Abstraktion über die Spracherkennung (STT). Erlaubt austauschbare Engines
 * (On-Device Whisper als Default, Cloud optional – siehe ADR-01).
 */
interface TranscriptionEngine {
    /** Eindeutige Kennung, z. B. "whisper-ondevice". */
    val id: String

    /** true, wenn die Engine sofort transkribieren kann (z. B. Modell vorhanden). */
    suspend fun isReady(): Boolean

    /**
     * Transkribiert die WAV-Datei.
     * @param language ISO-Sprachcode, z. B. "de"
     * @param onProgress Fortschritt 0f..1f
     */
    suspend fun transcribe(
        wavFile: File,
        language: String,
        onProgress: (Float) -> Unit = {},
    ): Transcript
}
