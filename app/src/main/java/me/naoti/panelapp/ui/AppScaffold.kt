package me.naoti.panelapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.naoti.panelapp.navigation.NavigationHost
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.components.BottomNavigationBar
import me.naoti.panelapp.ui.components.TopBar
import me.naoti.panelapp.ui.theme.Gray200
import me.naoti.panelapp.ui.theme.Gray900
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
    val systemUiController = rememberSystemUiController()
    val isDarkMode = appState.isDarkMode()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = if (isDarkMode) Gray900 else Gray200,
            darkIcons = !isDarkMode
        )
    }
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