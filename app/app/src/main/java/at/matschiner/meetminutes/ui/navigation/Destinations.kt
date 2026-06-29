package at.matschiner.meetminutes.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/** Top-Level-Ziele der App (Bottom-Navigation). */
enum class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    Record("record", "Aufnahme", Icons.Filled.Mic),
    Overview("overview", "Übersicht", Icons.Filled.CalendarMonth),
    Settings("settings", "Einstellungen", Icons.Filled.Settings),
}

/** Nicht-Top-Level-Routen. */
object Routes {
    const val DETAIL = "detail/{meetingId}"
    fun detail(meetingId: String) = "detail/$meetingId"
}
