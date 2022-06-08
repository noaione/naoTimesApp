package me.naoti.panelapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.ui.theme.darker
import me.naoti.panelapp.utils.getLogger

@Composable
fun SettingsScreen(appState: AppState) {
    val log = getLogger("SettingsScreen")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                horizontal = 10.dp
            )
    ) {
        Button(
            onClick = {
                log.i("Logging out...")
                appState.coroutineScope.launch {
                    // ignore everything
                    log.i("Setting current user to null")
                    appState.setCurrentUser(null)
                    log.i("Sending logout request to API...")
                    appState.apiState.logoutUser()
                    // clear navigation back stack
                    log.i("Removing navigation back stack queue...")
                    try {
                        appState.navAppController.backQueue.clear()
                    } catch (e: Exception) {
                        log.e("Failed to pop up navAppController, ignoring...")
                    }
                    try {
                        appState.navController.backQueue.clear()
                    } catch (e: Exception) {
                        log.e("Failed to pop up navController, ignoring...")
                    }
                    log.i("Clearing user cookies...")
                    appState.clearUserCookie()
                    // enter login screen
                    log.i("Navigating back to login screen")
                    appState.navController.navigate(ScreenItem.LoginScreen.route)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                disabledContainerColor = MaterialTheme.colorScheme.errorContainer.darker(0.2f),
                disabledContentColor = MaterialTheme.colorScheme.onErrorContainer.darker(0.2f)
            )
        ) {
            Text(text = "Logout")
        }
    }
}
