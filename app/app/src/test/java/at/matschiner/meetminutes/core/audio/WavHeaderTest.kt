package at.matschiner.meetminutes.core.audio

import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class WavHeaderTest {

    private fun ByteArray.ascii(from: Int, len: Int) =
        String(copyOfRange(from, from + len), Charsets.US_ASCII)

    private fun ByteArray.intLE(at: Int) =
        ByteBuffer.wrap(this, at, 4).order(ByteOrder.LITTLE_ENDIAN).int

    private fun ByteArray.shortLE(at: Int) =
        ByteBuffer.wrap(this, at, 2).order(ByteOrder.LITTLE_ENDIAN).short

    @Test
    fun header_hasCorrectSizeAndMagicBytes() {
        val h = WavHeader.build(sampleRate = 16000, channels = 1, bitsPerSample = 16, dataSize = 0)
        assertEquals(WavHeader.SIZE_BYTES, h.size)
        assertEquals("RIFF", h.ascii(0, 4))
        assertEquals("WAVE", h.ascii(8, 4))
        assertEquals("fmt ", h.ascii(12, 4))
        assertEquals("data", h.ascii(36, 4))
    }

    @Test
    fun header_encodesPcmFormatFields() {
        val h = WavHeader.build(sampleRate = 16000, channels = 1, bitsPerSample = 16, dataSize = 0)
        assertEquals(16, h.intLE(16))            // Subchunk1Size
        assertEquals(1.toShort(), h.shortLE(20)) // AudioFormat = PCM
        assertEquals(1.toShort(), h.shortLE(22)) // channels
        assertEquals(16000, h.intLE(24))         // sampleRate
        assertEquals(32000, h.intLE(28))         // byteRate = 16000*1*2
        assertEquals(2.toShort(), h.shortLE(32)) // blockAlign = 1*2
        assertEquals(16.toShort(), h.shortLE(34)) // bitsPerSample
    }

    @Test
    fun header_encodesChunkAndDataSizes() {
        val dataSize = 16000 * 2 // 1 Sekunde Mono 16 bit
        val h = WavHeader.build(16000, 1, 16, dataSize)
        assertEquals(36 + dataSize, h.intLE(4))  // ChunkSize
        assertEquals(dataSize, h.intLE(40))      // Subchunk2Size
    }

    @Test
    fun header_supportsStereo() {
        val h = WavHeader.build(sampleRate = 44100, channels = 2, bitsPerSample = 16, dataSize = 0)
        assertEquals(2.toShort(), h.shortLE(22))
        assertEquals(44100 * 2 * 2, h.intLE(28)) // byteRate
        assertEquals(4.toShort(), h.shortLE(32)) // blockAlign
    }
}
