package at.matschiner.meetminutes.recording

/** Status der laufenden Aufnahme. */
enum class RecordingStatus { IDLE, RECORDING, PAUSED, SAVING }

/**
 * Beobachtbarer Zustand der Aufnahme, den UI und Service teilen.
 *
 * @param amplitude normalisierter Pegel 0f..1f (für die Pegelanzeige)
 * @param elapsedMs aufgenommene Dauer in Millisekunden (datenbasiert)
 * @param lastSavedFileName Dateiname der zuletzt fertiggestellten Aufnahme
 */
data class RecordingState(
    val status: RecordingStatus = RecordingStatus.IDLE,
    val elapsedMs: Long = 0L,
    val amplitude: Float = 0f,
    val lastSavedFileName: String? = null,
)
