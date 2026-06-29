package at.matschiner.meetminutes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import at.matschiner.meetminutes.ui.navigation.MeetMinutesApp
import at.matschiner.meetminutes.ui.theme.MeetMinutesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeetMinutesTheme {
                MeetMinutesApp()
            }
        }
    }
}
