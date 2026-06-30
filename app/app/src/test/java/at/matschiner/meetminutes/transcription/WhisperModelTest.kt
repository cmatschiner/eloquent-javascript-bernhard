package at.matschiner.meetminutes.transcription

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WhisperModelTest {

    @Test
    fun default_isSmall() {
        assertEquals(WhisperModel.SMALL, WhisperModel.DEFAULT)
    }

    @Test
    fun downloadUrl_pointsToHuggingFace() {
        assertEquals(
            "https://huggingface.co/ggerganov/whisper.cpp/resolve/main/ggml-small.bin",
            WhisperModel.SMALL.downloadUrl,
        )
        assertTrue(WhisperModel.BASE.downloadUrl.endsWith("ggml-base.bin"))
    }

    @Test
    fun fromKey_parsesKnownKeysAndFallsBackToDefault() {
        assertEquals(WhisperModel.TINY, WhisperModel.fromKey("tiny"))
        assertEquals(WhisperModel.BASE, WhisperModel.fromKey("base"))
        assertEquals(WhisperModel.DEFAULT, WhisperModel.fromKey("unbekannt"))
        assertEquals(WhisperModel.DEFAULT, WhisperModel.fromKey(null))
    }
}
