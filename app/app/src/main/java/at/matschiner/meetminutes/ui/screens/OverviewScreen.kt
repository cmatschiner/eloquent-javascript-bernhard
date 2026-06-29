package at.matschiner.meetminutes.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun OverviewScreen(onOpenMeeting: (String) -> Unit) {
    PlaceholderScreen(
        title = "Kalenderübersicht",
        subtitle = "Hier entsteht in Sprint 4 die Kalender-/Listenansicht " +
            "der Besprechungen (anzeigen, aufrufen, Protokoll editieren, löschen).",
    )
}
