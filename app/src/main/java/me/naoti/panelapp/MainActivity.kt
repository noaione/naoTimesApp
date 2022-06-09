package me.naoti.panelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.state.rememberAppState
import me.naoti.panelapp.ui.AppScaffold
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.ui.preferences.DarkModeOverride
import me.naoti.panelapp.ui.preferences.UserSettings
import me.naoti.panelapp.ui.preferences.UserSettingsImpl
import me.naoti.panelapp.ui.screens.*
import me.naoti.panelapp.ui.theme.NaoTimesTheme

class MainActivity : ComponentActivity() {
    private lateinit var userSettings: UserSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            userSettings = UserSettingsImpl(this)
            val theme = userSettings.themeStream.collectAsState()
            val useDarkColors = when (theme.value) {
                DarkModeOverride.DarkMode -> true
                DarkModeOverride.LightMode -> false
                DarkModeOverride.FollowSystem -> isSystemInDarkTheme()
            }
            val navController = rememberNavController()
            val appState = rememberAppState(navController = navController)
            NaoTimesTheme(darkTheme = useDarkColors) {
                NaoTimesView(appState = appState, userSettings = userSettings)
            }
        }
    }
}

@Composable
fun NaoTimesView(appState: AppState, userSettings: UserSettings) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = appState.navController,
            startDestination = ScreenItem.SplashScreen.route
        ) {
            composable(
                ScreenItem.SplashScreen.route,
            ) {
                SplashScreen(appState)
            }
            composable(
                ScreenItem.LoginScreen.route,
            ) {
                LoginScreen(appState)
            }
            composable(
                ScreenItem.RegisterScreen.route,
            ) {
                RegisterScreen(appState)
            }
            composable(ScreenItem.AppScaffold.route) {
                AppScaffold(appState, userSettings)
            }
            composable(
                ScreenItem.ProjectScreen.route,
                arguments = listOf(
                    navArgument("source") { defaultValue = "dashboard" }
                )
            ) { stack ->
                val clickSource = stack.arguments?.getString("source") ?: "dashboard"
                ProjectScreen(
                    appState,
                    stack.arguments?.getString("projectId"),
                    clickSource.lowercase(),
                    userSettings,
                )
            }
            composable(
                ScreenItem.ProjectAddScreen.route,
            ) {
                ProjectAddScreen(appState, userSettings)
            }
        }
    }
}
