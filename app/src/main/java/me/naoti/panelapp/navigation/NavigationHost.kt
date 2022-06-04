package me.naoti.panelapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.screens.*
import me.naoti.panelapp.utils.getLogger

@Composable
fun NavigationHost(appState: AppState, paddingValues: PaddingValues) {
    val log = getLogger("NavigationMain")
    log.i("Creating navigation host")
    NavHost(
        navController = appState.navAppController,
        startDestination = NavigationItem.Dashboard.route,
        Modifier.padding(paddingValues)
    ) {
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
