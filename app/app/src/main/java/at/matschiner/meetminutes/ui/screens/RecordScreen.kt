package at.matschiner.meetminutes.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import at.matschiner.meetminutes.recording.RecordingStatus
import at.matschiner.meetminutes.ui.screens.record.RecordViewModel
import at.matschiner.meetminutes.ui.screens.record.TranscriptionUiState

@Composable
fun RecordScreen(viewModel: RecordViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val state by viewModel.recordingState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        if (result[Manifest.permission.RECORD_AUDIO] == true) {
            viewModel.startRecording()
        }
    }

    fun requestStart() {
        val needed = buildList {
            add(Manifest.permission.RECORD_AUDIO)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        val recordGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO,
        ) == PackageManager.PERMISSION_GRANTED
        if (recordGranted) viewModel.startRecording() else permissionLauncher.launch(needed.toTypedArray())
    }

    val isIdle = state.status == RecordingStatus.IDLE
    val isRecording = state.status == RecordingStatus.RECORDING
    val isPaused = state.status == RecordingStatus.PAUSED
    val isSaving = state.status == RecordingStatus.SAVING

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Neue Aufnahme", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = viewModel.dateText,
            onValueChange = viewModel::onDateChange,
            label = { Text("Datum (JJJJ-MM-TT)") },
            singleLine = true,
            enabled = isIdle,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = viewModel.art,
            onValueChange = viewModel::onArtChange,
            label = { Text("Art (z. B. Meeting, Telefonat)") },
            singleLine = true,
            enabled = isIdle,
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = viewModel.thema,
            onValueChange = viewModel::onThemaChange,
            label = { Text("Thema (z. B. Team-Meeting)") },
            singleLine = true,
            enabled = isIdle,
            modifier = Modifier.fillMaxWidth(),
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = formatDuration(state.elapsedMs),
                    style = MaterialTheme.typography.displaySmall,
                )
                Text(
                    text = when (state.status) {
                        RecordingStatus.IDLE -> "Bereit"
                        RecordingStatus.RECORDING -> "Aufnahme läuft …"
                        RecordingStatus.PAUSED -> "Pausiert"
                        RecordingStatus.SAVING -> "Speichern …"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
                LinearProgressIndicator(
                    progress = { if (isRecording) state.amplitude else 0f },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        when {
            isIdle || isSaving -> {
                Button(
                    onClick = ::requestStart,
                    enabled = isIdle && viewModel.isMetadataValid,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.Filled.Mic, contentDescription = null)
                    Text("  Aufnahme starten")
                }
                if (isIdle && !viewModel.isMetadataValid) {
                    Text(
                        "Bitte gültiges Datum, Art und Thema angeben.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            isRecording || isPaused -> {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { if (isPaused) viewModel.resume() else viewModel.pause() },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause,
                            contentDescription = null,
                        )
                        Text(if (isPaused) "  Fortsetzen" else "  Pause")
                    }
                    Button(
                        onClick = viewModel::stop,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(Icons.Filled.Stop, contentDescription = null)
                        Text("  Stopp")
                    }
                }
            }
        }

        state.lastSavedFileName?.let { saved ->
            if (isIdle) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Zuletzt gespeichert", style = MaterialTheme.typography.titleSmall)
                        Text(saved, style = MaterialTheme.typography.bodySmall)

                        when (val t = viewModel.transcription) {
                            is TranscriptionUiState.Idle -> {
                                Button(
                                    onClick = viewModel::transcribeLastRecording,
                                    modifier = Modifier.fillMaxWidth(),
                                ) { Text("Transkribieren") }
                            }
                            is TranscriptionUiState.Running -> {
                                // < 0.6: Modell-Download; danach: echte Whisper-Inferenz (70..100 %).
                                val label = if (t.progress < 0.6f) {
                                    "Modell wird geladen"
                                } else {
                                    "Transkribiere"
                                }
                                Text(
                                    "$label … ${(t.progress * 100).toInt()} %",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                LinearProgressIndicator(
                                    progress = { t.progress },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            is TranscriptionUiState.Done -> {
                                Text(
                                    "Transkript gespeichert:\n${t.transcriptFileName}",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            is TranscriptionUiState.Error -> {
                                Text(
                                    t.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                )
                                OutlinedButton(
                                    onClick = viewModel::transcribeLastRecording,
                                    modifier = Modifier.fillMaxWidth(),
                                ) { Text("Erneut versuchen") }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
