package at.matschiner.meetminutes.ui.screens.record

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import at.matschiner.meetminutes.recording.RecordingService
import at.matschiner.meetminutes.recording.RecordingState
import at.matschiner.meetminutes.recording.RecordingStateHolder
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class RecordViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    stateHolder: RecordingStateHolder,
) : ViewModel() {

    val recordingState: StateFlow<RecordingState> = stateHolder.state

    var dateText by mutableStateOf(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
        private set
    var art by mutableStateOf("Meeting")
        private set
    var thema by mutableStateOf("")
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
        RecordingService.start(context, date, art.trim(), thema.trim())
    }

    fun pause() = RecordingService.sendAction(context, RecordingService.ACTION_PAUSE)
    fun resume() = RecordingService.sendAction(context, RecordingService.ACTION_RESUME)
    fun stop() = RecordingService.sendAction(context, RecordingService.ACTION_STOP)
}
