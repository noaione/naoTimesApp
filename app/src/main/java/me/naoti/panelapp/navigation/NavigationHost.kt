package me.naoti.panelapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.screens.*
import me.naoti.panelapp.utils.getLogger

@Composable
fun NavigationHost(appState: AppState) {
    val log = getLogger("NavigationMain")
    log.i("Creating navigation host")
    NavHost(navController = appState.navController, startDestination = NavigationItem.Dashboard.route) {
        // Main screen
        composable(NavigationItem.Dashboard.route) {
            log.i("Entering route: ${NavigationItem.Dashboard.route}")
            DashboardScreen(appState)
        }
        composable(NavigationItem.Projects.route) {
            log.i("Entering route: ${NavigationItem.Projects.route}")
            ProjectsScreen(appState)
        }
        composable(NavigationItem.Settings.route) {
            log.i("Entering route: ${NavigationItem.Settings.route}")
            SettingsScreen(appState)
        }
    }
}
