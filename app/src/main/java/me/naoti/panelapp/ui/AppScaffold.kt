package me.naoti.panelapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import me.naoti.panelapp.navigation.NavigationHost
import me.naoti.panelapp.navigation.NavigationItem
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.components.BottomNavigationBar
import me.naoti.panelapp.ui.components.TopBar
import me.naoti.panelapp.ui.preferences.UserSettings

@Composable
fun ProjectAddButton(navController: NavController) {
    FloatingActionButton(
        onClick = {
            // move to actual project add page
            navController.navigate(ScreenItem.ProjectAddScreen.route)
        },
        modifier = Modifier
            .padding(6.dp)
            .clip(RoundedCornerShape(6.dp))
            .testTag("FABAddProject"),
        contentColor = MaterialTheme.colorScheme.onSecondary,
        containerColor = MaterialTheme.colorScheme.secondary
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add Project")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(appState: AppState, userSettings: UserSettings) {
    var yourFavoriteFab by rememberSaveable {
        mutableStateOf(false)
    }
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        floatingActionButton = {
            if (yourFavoriteFab) {
                ProjectAddButton(appState.navController)
            }
        }
    ) { padVal ->
        NavigationHost(appState, navController, padVal, userSettings) { route ->
            yourFavoriteFab = route != NavigationItem.Settings.route
        }
    }
}