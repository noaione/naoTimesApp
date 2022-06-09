package me.naoti.panelapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.ui.popUpToTop
import me.naoti.panelapp.ui.theme.darker
import me.naoti.panelapp.utils.getLogger

@Composable
fun LogoutButton(appState: AppState, modifier: Modifier = Modifier, dryRun: Boolean = false) {
    val log = getLogger("LogoutButton")
    Button(
        modifier = modifier.testTag("LogOutButton"),
        onClick = {
            log.i("Logging out...")
            appState.coroutineScope.launch {
                // ignore everything
                if (!dryRun) {
                    log.i("Setting current user to null")
                    appState.setCurrentUser(null)
                    log.i("Sending logout request to API...")
                    appState.apiState.logoutUser()
                    // clear navigation back stack
                    log.i("Clearing user cookies...")
                    appState.clearUserCookie()
                }
                // enter login screen
                log.i("Navigating back to login screen")
                appState.navController.navigate(ScreenItem.LoginScreen.route) {
                    popUpToTop(appState.navController)
                }
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            disabledContainerColor = MaterialTheme.colorScheme.errorContainer.darker(0.2f),
            disabledContentColor = MaterialTheme.colorScheme.onErrorContainer.darker(0.2f)
        )
    ) {
        Text(text = "Logout${if (dryRun) " (Dry)" else " "}")
    }
}

@Composable
fun SettingsScreen(appState: AppState) {
//    val log = getLogger("SettingsScreen")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 10.dp
            )
    ) {
        LogoutButton(appState, modifier = Modifier.padding(4.dp))
        LogoutButton(appState, modifier = Modifier.padding(4.dp), dryRun = true)
    }
}
