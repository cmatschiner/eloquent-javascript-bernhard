package at.matschiner.meetminutes.recording

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Single Source of Truth für den Aufnahme-Zustand. Wird vom [RecordingService]
 * aktualisiert und von der UI (ViewModel) beobachtet.
 */
@Singleton
class RecordingStateHolder @Inject constructor() {

    private val _state = MutableStateFlow(RecordingState())
    val state: StateFlow<RecordingState> = _state.asStateFlow()

    fun setStatus(status: RecordingStatus) = _state.update { it.copy(status = status) }

    fun setProgress(elapsedMs: Long, amplitude: Float) =
        _state.update { it.copy(elapsedMs = elapsedMs, amplitude = amplitude) }

    fun onSaved(fileName: String) = _state.update {
        it.copy(status = RecordingStatus.IDLE, amplitude = 0f, lastSavedFileName = fileName)
    }

    fun reset() = _state.update {
        RecordingState(lastSavedFileName = it.lastSavedFileName)
    }
}
