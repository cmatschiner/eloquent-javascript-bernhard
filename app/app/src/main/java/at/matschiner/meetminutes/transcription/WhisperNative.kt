package at.matschiner.meetminutes.transcription

/**
 * JNI-Brücke zur nativen whisper.cpp-Bibliothek (`libmeetminutes_whisper.so`).
 * Ist die Bibliothek nicht vorhanden, bleibt [available] false und die Engine
 * meldet einen klaren Fehler statt abzustürzen.
 */
object WhisperNative {

    @Volatile
    var available: Boolean = false
        private set

    init {
        available = runCatching { System.loadLibrary("meetminutes_whisper") }.isSuccess
    }

    /** Lädt das ggml-Modell; Rückgabe 0 = Fehler. */
    external fun nativeInit(modelPath: String): Long

    /** Transkribiert PCM-Float (16 kHz, Mono); Zeilen "startMs|endMs|text" oder null. */
    external fun nativeTranscribe(
        ctxPtr: Long,
        pcm: FloatArray,
        language: String,
        threads: Int,
    ): Array<String>?

    external fun nativeFree(ctxPtr: Long)
}
