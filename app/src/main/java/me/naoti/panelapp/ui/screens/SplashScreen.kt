package me.naoti.panelapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import me.naoti.panelapp.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.ui.components.naoTimesText
import me.naoti.panelapp.utils.getLogger

@Composable
fun SplashScreen(appState: AppState) {
    val log = getLogger("SplashScreenActivity")
    var expanded by remember {
        mutableStateOf(false)
    }
    
    LaunchedEffect(key1 = true) {
        // check authentication
        appState.coroutineScope.launch {
            delay(500L)
            expanded = true
            delay(1000L)
            if (appState.getCurrentUser() != null) {
                log.i("There is existing user in shared prefs, using it...")
                appState.navController.navigate(ScreenItem.AppScaffold.route) {
                    popUpTo(ScreenItem.SplashScreen.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            } else {
                when (val userInfo = appState.apiState.getUser()) {
                    is NetworkResponse.Success -> {
                        if (userInfo.body.loggedIn) {
                            log.i("Navigating to: ${ScreenItem.AppScaffold.route}")
                            appState.setCurrentUser(userInfo.body)
                            appState.navController.navigate(ScreenItem.AppScaffold.route) {
                                popUpTo(ScreenItem.SplashScreen.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        } else {
                            appState.setCurrentUser(null)
                            log.i("Navigating to: ${ScreenItem.LoginScreen.route}")
                            appState.navController.navigate(ScreenItem.LoginScreen.route) {
                                popUpTo(ScreenItem.SplashScreen.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }
                    is NetworkResponse.Error -> {
                        appState.setCurrentUser(null)
                        log.i("Navigation to ${ScreenItem.LoginScreen.route}")
                        appState.navController.navigate(ScreenItem.LoginScreen.route) {
                            popUpTo(ScreenItem.SplashScreen.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(17, 24, 39, 1))
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.naotimes_logo),
            contentDescription = "Logo",
            modifier = Modifier
                .height(120.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(50))
        )
        AnimatedVisibility(visible = expanded) {
            Text(
                naoTimesText(),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(
                    vertical = 10.dp,
                )
            )
        }
    }
}