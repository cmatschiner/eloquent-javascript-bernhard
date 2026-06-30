package at.matschiner.meetminutes.transcription

import at.matschiner.meetminutes.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * On-Device-Transkription mit Whisper (ggml) – Default-Engine gemäß ADR-01.
 *
 * Ablauf: Modell sicherstellen (Download beim 1. Start) → WAV in Float-PCM lesen →
 * native whisper.cpp-Inferenz → Segmente.
 */
@Singleton
class WhisperEngine @Inject constructor(
    private val modelManager: WhisperModelManager,
    private val settings: SettingsRepository,
) : TranscriptionEngine {

    override val id: String = "whisper-ondevice"

    override suspend fun isReady(): Boolean =
        WhisperNative.available && modelManager.isDownloaded(settings.whisperModel)

    override suspend fun transcribe(
        wavFile: File,
        language: String,
        onProgress: (Float) -> Unit,
    ): Transcript = withContext(Dispatchers.IO) {
        require(wavFile.exists()) { "Audiodatei nicht gefunden: ${wavFile.name}" }
        check(WhisperNative.available) {
            "Native Whisper-Bibliothek nicht verfügbar (nur arm64-v8a-Geräte)."
        }

        // 1) Modell sicherstellen (0 .. 0.6)
        val model = settings.whisperModel
        val modelFile = modelManager.ensureDownloaded(model) { p -> onProgress(p * 0.6f) }
        onProgress(0.62f)

        // 2) Audio lesen (0.62 .. 0.7)
        val pcm = WavPcmReader.readMonoFloat(wavFile)
        onProgress(0.7f)

        // 3) Native Inferenz (0.7 .. 1.0)
        val ctx = WhisperNative.nativeInit(modelFile.absolutePath)
        check(ctx != 0L) { "Modell konnte nicht geladen werden: ${modelFile.name}" }
        try {
            val threads = Runtime.getRuntime().availableProcessors().coerceIn(2, 8)
            val lines = WhisperNative.nativeTranscribe(ctx, pcm, language, threads)
                ?: error("Transkription fehlgeschlagen.")
            onProgress(1f)
            Transcript(language, lines.mapNotNull(::parseSegment))
        } finally {
            WhisperNative.nativeFree(ctx)
        }
    }

    /** Parst "startMs|endMs|text" aus der nativen Ausgabe. */
    private fun parseSegment(line: String): TranscriptSegment? {
        val parts = line.split("|", limit = 3)
        if (parts.size < 3) return null
        val start = parts[0].toLongOrNull() ?: return null
        val end = parts[1].toLongOrNull() ?: return null
        val text = parts[2].trim()
        if (text.isEmpty()) return null
        return TranscriptSegment(start, end, text)
    }
}
