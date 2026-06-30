package at.matschiner.meetminutes.transcription

/**
 * Verfügbare Whisper-ggml-Modelle. Download von Hugging Face (ggerganov/whisper.cpp).
 * Standard ist [SMALL] (Auftraggeber-Entscheidung Sprint 2).
 */
enum class WhisperModel(
    val key: String,
    val fileName: String,
    val approxBytes: Long,
) {
    TINY("tiny", "ggml-tiny.bin", 75_000_000L),
    BASE("base", "ggml-base.bin", 142_000_000L),
    SMALL("small", "ggml-small.bin", 466_000_000L);

    val downloadUrl: String
        get() = "$BASE_URL$fileName"

    companion object {
        private const val BASE_URL =
            "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/"

        val DEFAULT = SMALL

        fun fromKey(key: String?): WhisperModel =
            entries.firstOrNull { it.key == key } ?: DEFAULT
    }
}
