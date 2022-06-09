package me.naoti.panelapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.preferences.UserSettings
import me.naoti.panelapp.ui.screens.*
import me.naoti.panelapp.utils.getLogger
import me.naoti.panelapp.utils.mapBoolean

@Composable
fun NavigationHost(appState: AppState, paddingValues: PaddingValues, userSettings: UserSettings) {
    val log = getLogger("NavigationMain")
    log.i("Creating navigation host")
    NavHost(
        navController = appState.navAppController,
        startDestination = NavigationItem.Dashboard.route,
        Modifier.padding(paddingValues)
    ) {
        // Main screen
        composable(
            NavigationItem.Dashboard.route,
            arguments = listOf(navArgument("refresh") {defaultValue = "false"})
        ) { backStack ->
            val forceRefresh = mapBoolean(backStack.arguments?.getString("refresh"))
            log.i("Entering route: ${NavigationItem.Dashboard.route} [refresh=$forceRefresh]")
            DashboardScreen(appState, forceRefresh)
        }
        composable(
            NavigationItem.Projects.route,
            arguments = listOf(navArgument("refresh") {defaultValue = "false"})
        ) { backStack ->
            val forceRefresh = mapBoolean(backStack.arguments?.getString("refresh"))
            log.i("Entering route: ${NavigationItem.Projects.route} [refresh=$forceRefresh]")
            ProjectsScreen(appState)
        }
        composable(NavigationItem.Settings.route) {
            log.i("Entering route: ${NavigationItem.Settings.route}")
            SettingsScreen(appState, userSettings)
        }
    }
}
