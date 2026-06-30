package at.matschiner.meetminutes.data

import android.content.Context
import at.matschiner.meetminutes.transcription.WhisperModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Transkriptions-Modus gemäß ADR-01: On-Device als Default, Cloud optional. */
enum class TranscriptionMode { ON_DEVICE, CLOUD }

/** Einfache, persistente App-Einstellungen (SharedPreferences). */
@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences("meetminutes_settings", Context.MODE_PRIVATE)

    var transcriptionMode: TranscriptionMode
        get() = runCatching {
            TranscriptionMode.valueOf(prefs.getString(KEY_MODE, TranscriptionMode.ON_DEVICE.name)!!)
        }.getOrDefault(TranscriptionMode.ON_DEVICE)
        set(value) = prefs.edit().putString(KEY_MODE, value.name).apply()

    var whisperModel: WhisperModel
        get() = WhisperModel.fromKey(prefs.getString(KEY_MODEL, WhisperModel.DEFAULT.key))
        set(value) = prefs.edit().putString(KEY_MODEL, value.key).apply()

    /** Sprachcode für die Transkription. */
    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "de") ?: "de"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    private companion object {
        const val KEY_MODE = "transcription_mode"
        const val KEY_MODEL = "whisper_model"
        const val KEY_LANGUAGE = "language"
    }
}
