package at.matschiner.meetminutes.ui.screens.record

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.matschiner.meetminutes.data.SettingsRepository
import at.matschiner.meetminutes.recording.MeetingStorage
import at.matschiner.meetminutes.recording.RecordingService
import at.matschiner.meetminutes.recording.RecordingState
import at.matschiner.meetminutes.recording.RecordingStateHolder
import at.matschiner.meetminutes.transcription.TranscriptMarkdownFormatter
import at.matschiner.meetminutes.transcription.TranscriptMeta
import at.matschiner.meetminutes.transcription.TranscriptionEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

/** UI-Zustand der Transkription. */
sealed interface TranscriptionUiState {
    data object Idle : TranscriptionUiState
    data class Running(val progress: Float) : TranscriptionUiState
    data class Done(val transcriptFileName: String) : TranscriptionUiState
    data class Error(val message: String) : TranscriptionUiState
}

@HiltViewModel
class RecordViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    stateHolder: RecordingStateHolder,
    private val storage: MeetingStorage,
    private val engine: TranscriptionEngine,
    private val settings: SettingsRepository,
) : ViewModel() {

    val recordingState: StateFlow<RecordingState> = stateHolder.state

    var dateText by mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
        private set
    var art by mutableStateOf("Meeting")
        private set
    var thema by mutableStateOf("")
        private set

    var transcription by mutableStateOf<TranscriptionUiState>(TranscriptionUiState.Idle)
        private set

    fun onDateChange(value: String) { dateText = value }
    fun onArtChange(value: String) { art = value }
    fun onThemaChange(value: String) { thema = value }

    /** Pflichtfelder gemäß Pflichtenheft: Datum (gültig), Art, Thema. */
    val isMetadataValid: Boolean
        get() = parseDate() != null && art.isNotBlank() && thema.isNotBlank()

    private fun parseDate(): LocalDate? =
        runCatching { LocalDate.parse(dateText.trim()) }.getOrNull()

    fun startRecording() {
        val date = parseDate() ?: return
        transcription = TranscriptionUiState.Idle
        RecordingService.start(context, date, art.trim(), thema.trim())
    }

    fun pause() = RecordingService.sendAction(context, RecordingService.ACTION_PAUSE)
    fun resume() = RecordingService.sendAction(context, RecordingService.ACTION_RESUME)
    fun stop() = RecordingService.sendAction(context, RecordingService.ACTION_STOP)

    /** Transkribiert die zuletzt gespeicherte Aufnahme und schreibt das .md-Transkript. */
    fun transcribeLastRecording() {
        val wavName = recordingState.value.lastSavedFileName ?: return
        val date = parseDate() ?: LocalDate.now()
        viewModelScope.launch {
            transcription = TranscriptionUiState.Running(0f)
            try {
                val wav = storage.fileByName(wavName)
                val transcript = withContext(Dispatchers.IO) {
                    engine.transcribe(wav, settings.language) { progress ->
                        transcription = TranscriptionUiState.Running(progress)
                    }
                }
                val markdown = TranscriptMarkdownFormatter.format(
                    TranscriptMeta(
                        date = date,
                        art = art.trim(),
                        thema = thema.trim(),
                        audioFileName = wavName,
                        durationMs = recordingState.value.elapsedMs,
                        language = settings.language,
                    ),
                    transcript,
                )
                val mdFile = withContext(Dispatchers.IO) { storage.writeTranscript(wav, markdown) }
                transcription = TranscriptionUiState.Done(mdFile.name)
            } catch (e: Exception) {
                transcription = TranscriptionUiState.Error(e.message ?: "Transkription fehlgeschlagen")
            }
        }
    }
}
