package me.naoti.panelapp.ui

import android.annotation.SuppressLint
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import me.naoti.panelapp.navigation.NavigationHost
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.components.BottomNavigationBar
import me.naoti.panelapp.ui.components.TopBar

@Composable
fun AppScaffold(appState: AppState) {
    Scaffold(
        scaffoldState = appState.scaffoldState,
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomNavigationBar(appState.navAppController)
        },
    ) { padVal ->
        NavigationHost(appState, padVal)
    }
}