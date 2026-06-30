package at.matschiner.meetminutes.transcription

import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class WavPcmReaderTest {

    private fun pcmBytes(vararg samples: Short): ByteArray {
        val bb = ByteBuffer.allocate(samples.size * 2).order(ByteOrder.LITTLE_ENDIAN)
        samples.forEach { bb.putShort(it) }
        return bb.array()
    }

    @Test
    fun pcm16ToFloat_monoNormalizes() {
        val bytes = pcmBytes(0, 16384, -16384, Short.MAX_VALUE, Short.MIN_VALUE)
        val out = WavPcmReader.pcm16ToFloat(bytes, 0, bytes.size, channels = 1)
        assertEquals(5, out.size)
        assertEquals(0f, out[0], 1e-6f)
        assertEquals(0.5f, out[1], 1e-3f)
        assertEquals(-0.5f, out[2], 1e-3f)
        assertEquals(0.999f, out[3], 1e-2f)
        assertEquals(-1f, out[4], 1e-6f)
    }

    @Test
    fun pcm16ToFloat_stereoAveragesChannels() {
        // Frame 1: L=10000, R=20000 → avg 15000; Frame 2: L=-10000, R=-20000 → avg -15000
        val bytes = pcmBytes(10000, 20000, -10000, -20000)
        val out = WavPcmReader.pcm16ToFloat(bytes, 0, bytes.size, channels = 2)
        assertEquals(2, out.size)
        assertEquals(15000 / 32768f, out[0], 1e-4f)
        assertEquals(-15000 / 32768f, out[1], 1e-4f)
    }

    @Test
    fun pcm16ToFloat_ignoresTrailingOddByte() {
        val bytes = pcmBytes(100, 200) + byteArrayOf(7) // ungerades Reststück
        val out = WavPcmReader.pcm16ToFloat(bytes, 0, bytes.size, channels = 1)
        assertEquals(2, out.size)
    }
}
