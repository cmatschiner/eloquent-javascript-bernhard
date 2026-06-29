package at.matschiner.meetminutes.recording

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import at.matschiner.meetminutes.MainActivity
import at.matschiner.meetminutes.R
import at.matschiner.meetminutes.core.audio.WavHeader
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.RandomAccessFile
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.min

/**
 * Foreground-Service für die Audioaufnahme. Nimmt PCM (16 kHz, Mono, 16 bit) auf,
 * schreibt fortlaufend in eine WAV-Datei und aktualisiert den [RecordingStateHolder].
 * Der WAV-Header wird periodisch aktualisiert, damit die Datei auch bei einem
 * unerwarteten Abbruch (Absturz/Kill) abspielbar bleibt.
 */
@AndroidEntryPoint
class RecordingService : Service() {

    @Inject lateinit var stateHolder: RecordingStateHolder
    @Inject lateinit var storage: MeetingStorage

    @Volatile private var running = false
    @Volatile private var paused = false
    private var worker: Thread? = null
    private var outputFile: File? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                val date = LocalDate.ofEpochDay(
                    intent.getLongExtra(EXTRA_DATE_EPOCH_DAY, LocalDate.now().toEpochDay()),
                )
                val art = intent.getStringExtra(EXTRA_ART).orEmpty()
                val thema = intent.getStringExtra(EXTRA_THEMA).orEmpty()
                startRecording(date, art, thema)
            }
            ACTION_PAUSE -> {
                paused = true
                stateHolder.setStatus(RecordingStatus.PAUSED)
            }
            ACTION_RESUME -> {
                paused = false
                stateHolder.setStatus(RecordingStatus.RECORDING)
            }
            ACTION_STOP -> stopRecording()
        }
        return START_NOT_STICKY
    }

    private fun startRecording(date: LocalDate, art: String, thema: String) {
        if (running) return
        val file = storage.resolveWavFile(date, art, thema)
        outputFile = file

        startForegroundNotification(thema.ifBlank { art.ifBlank { "Aufnahme" } })
        stateHolder.setStatus(RecordingStatus.RECORDING)
        paused = false
        running = true

        worker = Thread { recordLoop(file) }.also { it.start() }
    }

    private fun recordLoop(file: File) {
        var recorder: AudioRecord? = null
        var raf: RandomAccessFile? = null
        var dataSize = 0
        try {
            val minBuffer = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL, ENCODING)
                .coerceAtLeast(BUFFER_FALLBACK)
            val bufferSize = min(minBuffer * 2, MAX_BUFFER)

            @Suppress("MissingPermission")
            recorder = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL,
                ENCODING,
                bufferSize,
            )

            raf = RandomAccessFile(file, "rw")
            raf.setLength(0)
            raf.write(ByteArray(WavHeader.SIZE_BYTES)) // Platzhalter, später überschrieben

            if (recorder.state != AudioRecord.STATE_INITIALIZED) {
                throw IllegalStateException("AudioRecord nicht initialisiert")
            }
            recorder.startRecording()

            val buffer = ByteArray(bufferSize)
            var lastHeaderFlush = 0
            while (running) {
                if (paused) {
                    // Mikrofon-Puffer leeren, aber nicht in die Datei schreiben.
                    recorder.read(buffer, 0, buffer.size)
                    continue
                }
                val read = recorder.read(buffer, 0, buffer.size)
                if (read <= 0) continue
                raf.write(buffer, 0, read)
                dataSize += read

                val amplitude = peakAmplitude(buffer, read)
                val elapsedMs = dataSize.toLong() * 1000L / BYTE_RATE
                stateHolder.setProgress(elapsedMs, amplitude)

                // Crash-Sicherheit: Header ~jede Sekunde mit aktueller Größe schreiben.
                if (dataSize - lastHeaderFlush >= BYTE_RATE) {
                    writeHeader(raf, dataSize)
                    lastHeaderFlush = dataSize
                }
            }
        } catch (t: Throwable) {
            // Bestmöglich finalisieren – Datei bleibt durch Header gültig.
        } finally {
            recorder?.let { rec ->
                runCatching { rec.stop() }
                runCatching { rec.release() }
            }
            raf?.let { f ->
                runCatching { writeHeader(f, dataSize) }
                runCatching { f.close() }
            }
            stateHolder.onSaved(file.name)
            stopForegroundCompat()
            stopSelf()
        }
    }

    private fun writeHeader(raf: RandomAccessFile, dataSize: Int) {
        val pointer = raf.filePointer
        raf.seek(0)
        raf.write(WavHeader.build(SAMPLE_RATE, CHANNELS, BITS_PER_SAMPLE, dataSize))
        raf.seek(pointer)
    }

    private fun peakAmplitude(buffer: ByteArray, length: Int): Float {
        var peak = 0
        var i = 0
        while (i + 1 < length) {
            val sample = (buffer[i].toInt() and 0xFF) or (buffer[i + 1].toInt() shl 8)
            val absVal = abs(sample)
            if (absVal > peak) peak = absVal
            i += 2
        }
        return (peak / 32768f).coerceIn(0f, 1f)
    }

    private fun stopRecording() {
        if (!running) {
            stopForegroundCompat()
            stopSelf()
            return
        }
        stateHolder.setStatus(RecordingStatus.SAVING)
        running = false
        worker?.join(2000)
    }

    private fun startForegroundNotification(title: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Aufnahme",
                NotificationManager.IMPORTANCE_LOW,
            ).apply { description = "Laufende Audioaufnahme" }
            manager.createNotificationChannel(channel)
        }

        val contentIntent = android.app.PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            android.app.PendingIntent.FLAG_IMMUTABLE,
        )

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("MeetMinutes nimmt auf")
            .setContentText(title)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(contentIntent)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun stopForegroundCompat() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
    }

    companion object {
        const val ACTION_START = "at.matschiner.meetminutes.action.START"
        const val ACTION_PAUSE = "at.matschiner.meetminutes.action.PAUSE"
        const val ACTION_RESUME = "at.matschiner.meetminutes.action.RESUME"
        const val ACTION_STOP = "at.matschiner.meetminutes.action.STOP"

        const val EXTRA_DATE_EPOCH_DAY = "extra_date_epoch_day"
        const val EXTRA_ART = "extra_art"
        const val EXTRA_THEMA = "extra_thema"

        private const val CHANNEL_ID = "recording"
        private const val NOTIFICATION_ID = 1001

        private const val SAMPLE_RATE = 16_000
        private const val CHANNELS = 1
        private const val BITS_PER_SAMPLE = 16
        private const val BYTE_RATE = SAMPLE_RATE * CHANNELS * (BITS_PER_SAMPLE / 8)
        private const val CHANNEL = AudioFormat.CHANNEL_IN_MONO
        private const val ENCODING = AudioFormat.ENCODING_PCM_16BIT
        private const val BUFFER_FALLBACK = 4096
        private const val MAX_BUFFER = 65_536

        fun start(context: Context, date: LocalDate, art: String, thema: String) {
            val intent = Intent(context, RecordingService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_DATE_EPOCH_DAY, date.toEpochDay())
                putExtra(EXTRA_ART, art)
                putExtra(EXTRA_THEMA, thema)
            }
            androidx.core.content.ContextCompat.startForegroundService(context, intent)
        }

        fun sendAction(context: Context, action: String) {
            val intent = Intent(context, RecordingService::class.java).apply { this.action = action }
            context.startService(intent)
        }
    }
}
