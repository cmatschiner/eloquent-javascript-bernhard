package at.matschiner.meetminutes.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import at.matschiner.meetminutes.ui.screens.settings.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    var showKey by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Einstellungen", style = MaterialTheme.typography.headlineSmall)

        Text("Inhaltsanalyse (Claude API)", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = viewModel.apiKey,
            onValueChange = viewModel::onApiKeyChange,
            label = { Text("Anthropic API-Key") },
            singleLine = true,
            visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showKey = !showKey }) {
                    Icon(
                        if (showKey) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (showKey) "Verbergen" else "Anzeigen",
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            "Den Key erhältst du unter console.anthropic.com. Er wird nur lokal gespeichert.",
            style = MaterialTheme.typography.bodySmall,
        )
        OutlinedTextField(
            value = viewModel.analysisModel,
            onValueChange = viewModel::onAnalysisModelChange,
            label = { Text("Claude-Modell") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Text("Transkription (On-Device Whisper)", style = MaterialTheme.typography.titleMedium)
        Text("Modell", style = MaterialTheme.typography.bodyMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            viewModel.whisperModels.forEach { model ->
                FilterChip(
                    selected = viewModel.whisperModel == model,
                    onClick = { viewModel.onWhisperModelChange(model) },
                    label = { Text(model.key) },
                )
            }
        }
        Text(
            "tiny = schnell/ungenauer · base = Kompromiss · small = genauer/langsamer. " +
                "Nach dem Wechsel wird das neue Modell beim nächsten Transkribieren geladen.",
            style = MaterialTheme.typography.bodySmall,
        )

        OutlinedTextField(
            value = viewModel.language,
            onValueChange = viewModel::onLanguageChange,
            label = { Text("Sprache (z. B. de)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
