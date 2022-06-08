package me.naoti.panelapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
        modifier = Modifier
            .padding(6.dp)
            .clip(RoundedCornerShape(6.dp)),
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add Project")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(appState: AppState) {
    Scaffold(
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