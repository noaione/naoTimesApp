package me.naoti.panelapp.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.preferences.UserSettings
import me.naoti.panelapp.ui.screens.DashboardScreen
import me.naoti.panelapp.ui.screens.ProjectsScreen
import me.naoti.panelapp.ui.screens.SettingsScreen
import me.naoti.panelapp.utils.getLogger

@Composable
fun NavigationHost(
    appState: AppState,
    navController: NavController,
    paddingValues: PaddingValues,
    userSettings: UserSettings,
    onRouteChange: (String) -> Unit,
) {
    val log = getLogger("NavigationMain")
    log.i("Creating navigation host")
    NavHost(
        navController = navController as NavHostController,
        startDestination = NavigationItem.Dashboard.route,
        Modifier.padding(paddingValues)
    ) {
        // Main screen
        composable(
            NavigationItem.Dashboard.route,
            arguments = listOf(navArgument("refresh") {defaultValue = "false"})
        ) {
            log.i("Entering route: ${NavigationItem.Dashboard.route}")
            onRouteChange(NavigationItem.Dashboard.route)
            DashboardScreen(appState, userSettings)
        }
        composable(
            NavigationItem.Projects.route,
            arguments = listOf(navArgument("refresh") {defaultValue = "false"})
        ) {
            log.i("Entering route: ${NavigationItem.Projects.route}")
            onRouteChange(NavigationItem.Projects.route)
            ProjectsScreen(appState, userSettings)
        }
        composable(NavigationItem.Settings.route) {
            log.i("Entering route: ${NavigationItem.Settings.route}")
            onRouteChange(NavigationItem.Settings.route)
            SettingsScreen(appState, userSettings)
        }
    }
}
