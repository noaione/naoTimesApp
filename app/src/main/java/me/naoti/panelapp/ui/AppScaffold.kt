package me.naoti.panelapp.ui

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import me.naoti.panelapp.navigation.NavigationHost
import me.naoti.panelapp.state.rememberAppState
import me.naoti.panelapp.ui.components.BottomNavigationBar
import me.naoti.panelapp.ui.components.TopBar

@Composable
fun AppScaffold(navController: NavController) {
    val appState = rememberAppState(navController = navController as NavHostController)
    Scaffold(
        scaffoldState = appState.scaffoldState,
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
    ) {
        NavigationHost(appState)
    }
}