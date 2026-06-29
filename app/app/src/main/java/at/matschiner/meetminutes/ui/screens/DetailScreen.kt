package at.matschiner.meetminutes.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun DetailScreen(meetingId: String, onBack: () -> Unit) {
    PlaceholderScreen(
        title = "Besprechung",
        subtitle = "Detailansicht für Protokoll, Transkript und Audio " +
            "(ID: $meetingId). Inhalte folgen ab Sprint 3/4.",
    )
}
