package at.matschiner.meetminutes.transcription

import at.matschiner.meetminutes.data.SettingsRepository
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-Device-Transkription mit Whisper (ggml) – Default-Engine gemäß ADR-01.
 *
 * Schritt 2.1 liefert die Orchestrierung (Modell-Download, Zustände, UI-Anbindung).
 * Die eigentliche native Inferenz (whisper.cpp via JNI) wird in Schritt 2.2 ergänzt;
 * bis dahin meldet [transcribe] einen klaren Hinweis.
 */
@Singleton
class WhisperEngine @Inject constructor(
    private val modelManager: WhisperModelManager,
    private val settings: SettingsRepository,
) : TranscriptionEngine {

    override val id: String = "whisper-ondevice"

    override suspend fun isReady(): Boolean = modelManager.isDownloaded(settings.whisperModel)

    override suspend fun transcribe(
        wavFile: File,
        language: String,
        onProgress: (Float) -> Unit,
    ): Transcript {
        require(wavFile.exists()) { "Audiodatei nicht gefunden: ${wavFile.name}" }
        // Wird in Schritt 2.2 durch die native whisper.cpp-Inferenz ersetzt.
        throw UnsupportedOperationException(
            "On-Device-Whisper wird in Schritt 2.2 (native Einbindung) aktiviert.",
        )
    }
}
