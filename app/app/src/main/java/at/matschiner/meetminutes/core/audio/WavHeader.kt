package at.matschiner.meetminutes.core.audio

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Erzeugt einen 44-Byte-WAV/RIFF-Header für PCM-Audio (Little Endian).
 * Reine Logik – ohne Android-Abhängigkeit und damit unit-testbar.
 */
object WavHeader {

    const val SIZE_BYTES = 44

    /**
     * @param sampleRate z. B. 16000
     * @param channels 1 = Mono, 2 = Stereo
     * @param bitsPerSample z. B. 16
     * @param dataSize Anzahl der PCM-Datenbytes (ohne Header)
     */
    fun build(sampleRate: Int, channels: Int, bitsPerSample: Int, dataSize: Int): ByteArray {
        require(sampleRate > 0) { "sampleRate must be > 0" }
        require(channels > 0) { "channels must be > 0" }
        require(bitsPerSample % 8 == 0) { "bitsPerSample must be a multiple of 8" }
        require(dataSize >= 0) { "dataSize must be >= 0" }

        val byteRate = sampleRate * channels * (bitsPerSample / 8)
        val blockAlign = channels * (bitsPerSample / 8)

        return ByteBuffer.allocate(SIZE_BYTES).order(ByteOrder.LITTLE_ENDIAN).apply {
            put("RIFF".toByteArray(Charsets.US_ASCII))
            putInt(36 + dataSize)                       // ChunkSize
            put("WAVE".toByteArray(Charsets.US_ASCII))
            put("fmt ".toByteArray(Charsets.US_ASCII))
            putInt(16)                                  // Subchunk1Size (PCM)
            putShort(1)                                 // AudioFormat = PCM
            putShort(channels.toShort())
            putInt(sampleRate)
            putInt(byteRate)
            putShort(blockAlign.toShort())
            putShort(bitsPerSample.toShort())
            put("data".toByteArray(Charsets.US_ASCII))
            putInt(dataSize)                            // Subchunk2Size
        }.array()
    }
}
