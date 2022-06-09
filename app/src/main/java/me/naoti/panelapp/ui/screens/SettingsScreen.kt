package me.naoti.panelapp.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.preferences.DarkModeOverride
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.ui.popUpToTop
import me.naoti.panelapp.ui.preferences.UserSettings
import me.naoti.panelapp.ui.theme.NaoTimesTheme
import me.naoti.panelapp.ui.theme.darker
import me.naoti.panelapp.utils.getLogger

@Composable
fun LogoutButton(appState: AppState, modifier: Modifier = Modifier, dryRun: Boolean = false) {
    val log = getLogger("LogoutButton")
    Button(
        modifier = modifier
            .testTag("LogOutButton")
            .padding(
                horizontal = 20.dp,
                vertical = 5.dp
            )
            .fillMaxWidth(),
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
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            disabledContainerColor = MaterialTheme.colorScheme.error.darker(0.2f),
            disabledContentColor = MaterialTheme.colorScheme.onError.darker(0.2f)
        )
    ) {
        Text(text = "Logout${if (dryRun) " (Dry)" else " "}")
    }
}

@Composable
fun BoxedSettings(
    onClick: (() -> Unit)? = null,
    marginTop: Dp = 10.dp,
    marginBottom: Dp = 10.dp,
    content: @Composable () -> Unit,
) {
    var baseModifier = Modifier.fillMaxWidth()
    if (onClick != null) {
        baseModifier = baseModifier.clickable {
            onClick()
        }
    }
    Box(
        modifier = baseModifier
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = marginTop,
                    top = marginBottom,
                )
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkModeToggle(
    current: DarkModeOverride,
    onChangeMode: ((DarkModeOverride) -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    var currentState by remember { mutableStateOf(current) }
    val radioOptions = listOf(
        DarkModeOverride.FollowSystem,
        DarkModeOverride.LightMode,
        DarkModeOverride.DarkMode
    )
    BoxedSettings(
        onClick = {
            showDialog = true
        }
    ) {
        Text(text = "Dark Mode", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.sp)
        Text(text = currentState.toName(), fontSize = 13.sp, letterSpacing = 0.sp)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            dismissButton = {
                Text(text = "Close")
            },
            confirmButton = {},
            title = {
                Text(text = "Dark Mode")
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                ) {
                    radioOptions.forEach { opts ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .selectable(
                                    selected = opts == currentState,
                                    onClick = {
                                        currentState = opts
                                        if (onChangeMode != null) {
                                            onChangeMode(opts)
                                        }
                                        showDialog = false
                                    }
                                )
                                .clip(RoundedCornerShape(50)),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = opts == currentState,
                                onClick = null,
                                modifier = Modifier.padding(
                                    start = 4.dp,
                                )
                            )
                            Text(
                                text = opts.toName(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 16.dp, end = 4.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DarkModeTogglePreview() {
    NaoTimesTheme {
        DarkModeToggle(current = DarkModeOverride.FollowSystem)
    }
}

@Composable
fun TextHead(text: String) {
    Text(
        text,
        style = TextStyle(
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 0.sp,
        ),
        modifier = Modifier.padding(
            start = 20.dp,
            end = 20.dp,
            top = 10.dp,
            bottom = 5.dp
        )
    )
}

@Composable
fun SettingsScreen(appState: AppState, userSettings: UserSettings) {
    val userInfo = appState.getCurrentUser()!!
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        TextHead(text = "Theme")
        DarkModeToggle(appState.getDarkMode()) { newMode ->
            appState.setDarkMode(newMode)
            userSettings.theme = newMode
        }
        Spacer(modifier = Modifier.height(4.dp))
        TextHead(text = "User")
        BoxedSettings(
            onClick = {
                val clipboard = appState.contextState.getSystemService(ClipboardManager::class.java)
                val clip = ClipData.newPlainText("user id", userInfo.id!!)
                clipboard.setPrimaryClip(clip)
                Toast
                    .makeText(appState.contextState, "Copied!", Toast.LENGTH_SHORT)
                    .show()
            }
        ) {
            Text(text = "ID", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.sp)
            Text(text = userInfo.id!!, fontSize = 13.sp, letterSpacing = 0.sp)
        }
        BoxedSettings {
            Text(text = "Name", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.sp)
            Text(text = userInfo.name ?: "Not Set", fontSize = 13.sp, letterSpacing = 0.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextHead(text = "Servers")
        BoxedSettings {
            Text(text = "Announce Channel", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.sp)
            Text(text = userInfo.announceChannel ?: "Not Set", fontSize = 13.sp, letterSpacing = 0.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
        LogoutButton(appState, modifier = Modifier.padding(4.dp))
    }
}
