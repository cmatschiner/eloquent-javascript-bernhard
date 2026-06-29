package at.matschiner.meetminutes.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import at.matschiner.meetminutes.ui.screens.DetailScreen
import at.matschiner.meetminutes.ui.screens.OverviewScreen
import at.matschiner.meetminutes.ui.screens.RecordScreen
import at.matschiner.meetminutes.ui.screens.SettingsScreen

/** Wurzel-Composable: Scaffold mit Bottom-Navigation und NavHost. */
@Composable
fun MeetMinutesApp() {
    val navController = rememberNavController()
    val topLevel = TopLevelDestination.entries

    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            NavigationBar {
                topLevel.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(TopLevelDestination.Record.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.Record.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(TopLevelDestination.Record.route) { RecordScreen() }
            composable(TopLevelDestination.Overview.route) {
                OverviewScreen(onOpenMeeting = { id -> navController.navigate(Routes.detail(id)) })
            }
            composable(TopLevelDestination.Settings.route) { SettingsScreen() }
            composable(
                route = Routes.DETAIL,
                arguments = listOf(navArgument("meetingId") { type = NavType.StringType }),
            ) { entry ->
                DetailScreen(
                    meetingId = entry.arguments?.getString("meetingId").orEmpty(),
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
