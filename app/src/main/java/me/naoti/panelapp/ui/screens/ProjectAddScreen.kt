package me.naoti.panelapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.theme.Gray200
import me.naoti.panelapp.ui.theme.Gray800
import me.naoti.panelapp.ui.theme.Gray900
import me.naoti.panelapp.ui.theme.White
import me.naoti.panelapp.utils.getLogger

@Composable
fun ProjectAddScreen(appState: AppState) {
    val log = getLogger("ProjectAddView")

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
            TopAppBar(
                backgroundColor = if (appState.isDarkMode()) Gray900 else Gray200,
                contentColor = if (appState.isDarkMode()) White else Gray800
            ) {
                Icon(
                    Icons.Filled.ArrowBack, contentDescription = "Go back",
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable {
                            // go back
                            appState.navController.popBackStack()
                        }
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text("Add New Project")
            }
        },
    ) { paddingVal ->
        Column(
            modifier = Modifier.padding(paddingVal)
        ) {
            Text(text = "Test")
        }
    }
}
