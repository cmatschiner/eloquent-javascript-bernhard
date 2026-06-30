package at.matschiner.meetminutes.transcription

import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Liest 16-bit-PCM-WAV-Dateien in einen normalisierten Float-Array ([-1,1]),
 * wie ihn whisper.cpp erwartet (16 kHz, Mono). Bei Stereo werden Kanäle gemittelt.
 */
object WavPcmReader {

    /** Wandelt 16-bit-PCM-Bytes (Little Endian) in Float [-1,1] um. */
    fun pcm16ToFloat(bytes: ByteArray, offset: Int, length: Int, channels: Int): FloatArray {
        val usableLength = length - (length % (2 * channels.coerceAtLeast(1)))
        val frames = usableLength / (2 * channels)
        val out = FloatArray(frames)
        val bb = ByteBuffer.wrap(bytes, offset, usableLength).order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until frames) {
            var sum = 0
            for (c in 0 until channels) {
                sum += bb.short.toInt()
            }
            val avg = sum / channels
            out[i] = (avg / 32768f).coerceIn(-1f, 1f)
        }
        return out
    }

    /** Liest eine WAV-Datei und liefert Mono-Float-Samples. */
    fun readMonoFloat(file: File): FloatArray {
        val data = file.readBytes()
        require(data.size > 44) { "WAV-Datei zu klein: ${file.name}" }
        require(String(data, 0, 4, Charsets.US_ASCII) == "RIFF") { "Keine RIFF-Datei" }

        var channels = 1
        var pos = 12 // nach "RIFF"+size+"WAVE"
        var dataOffset = -1
        var dataLength = 0
        while (pos + 8 <= data.size) {
            val chunkId = String(data, pos, 4, Charsets.US_ASCII)
            val chunkSize = ByteBuffer.wrap(data, pos + 4, 4).order(ByteOrder.LITTLE_ENDIAN).int
            val body = pos + 8
            when (chunkId) {
                "fmt " -> {
                    channels = ByteBuffer.wrap(data, body + 2, 2)
                        .order(ByteOrder.LITTLE_ENDIAN).short.toInt().coerceAtLeast(1)
                }
                "data" -> {
                    dataOffset = body
                    dataLength = chunkSize.coerceAtMost(data.size - body)
                }
            }
            if (dataOffset >= 0) break
            pos = body + chunkSize + (chunkSize and 1) // 2-Byte-Ausrichtung
        }
        require(dataOffset >= 0) { "Kein data-Chunk gefunden" }
        return pcm16ToFloat(data, dataOffset, dataLength, channels)
    }
}
