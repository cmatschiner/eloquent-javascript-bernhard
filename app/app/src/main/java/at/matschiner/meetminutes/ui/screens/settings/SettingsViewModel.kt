package at.matschiner.meetminutes.ui.screens.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import at.matschiner.meetminutes.data.SettingsRepository
import at.matschiner.meetminutes.transcription.WhisperModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settings: SettingsRepository,
) : ViewModel() {

    var apiKey by mutableStateOf(settings.anthropicApiKey)
        private set
    var analysisModel by mutableStateOf(settings.analysisModel)
        private set
    var whisperModel by mutableStateOf(settings.whisperModel)
        private set
    var language by mutableStateOf(settings.language)
        private set

    val whisperModels: List<WhisperModel> = WhisperModel.entries

    fun onApiKeyChange(value: String) {
        apiKey = value
        settings.anthropicApiKey = value
    }

    fun onAnalysisModelChange(value: String) {
        analysisModel = value
        settings.analysisModel = value
    }

    fun onWhisperModelChange(model: WhisperModel) {
        whisperModel = model
        settings.whisperModel = model
    }

    fun onLanguageChange(value: String) {
        language = value
        settings.language = value
    }
}
