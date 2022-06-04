package me.naoti.panelapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import me.naoti.panelapp.navigation.NavigationHost
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.components.BottomNavigationBar
import me.naoti.panelapp.ui.components.TopBar
import me.naoti.panelapp.utils.getLogger

@Composable
fun ProjectAddButton(navController: NavController) {
    val log = getLogger("ProjectAddButton")
    FloatingActionButton(
        onClick = {
            // move to actual project add page
            log.i("Moving to project add view!")
            navController.navigate(ScreenItem.ProjectAddScreen.route)
        },
        modifier = Modifier.padding(6.dp),
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add Project")
    }
}

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
        floatingActionButton = {
            ProjectAddButton(appState.navController)
        }
    ) { padVal ->
        NavigationHost(appState, padVal)
    }
}