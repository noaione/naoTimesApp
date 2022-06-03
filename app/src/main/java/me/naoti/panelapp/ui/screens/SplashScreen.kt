package me.naoti.panelapp.ui.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import me.naoti.panelapp.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch
import me.naoti.panelapp.state.rememberAppState
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.utils.getLogger

@Composable
fun SplashScreen(navController: NavController) {
    val appState = rememberAppState()
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }
    val log = getLogger("SplashScreenActivity")
    
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                }
            )
        )

        // check authentication
        appState.coroutineScope.launch {
            if (appState.getCurrentUser() != null) {
                log.i("There is existing user in shared prefs, using it...")
                navController.navigate(ScreenItem.AppScaffold.route) {
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                }
            } else {
                when (val userInfo = appState.apiState.getUser()) {
                    is NetworkResponse.Success -> {
                        if (userInfo.body.loggedIn) {
                            log.i("Navigating to: ${ScreenItem.AppScaffold.route}")
                            appState.setCurrentUser(userInfo.body)
                            navController.navigate(ScreenItem.AppScaffold.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        } else {
                            appState.setCurrentUser(null)
                            log.i("Navigating to: ${ScreenItem.LoginScreen.route}")
                            navController.navigate(ScreenItem.LoginScreen.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                    is NetworkResponse.Error -> {
                        appState.setCurrentUser(null)
                        log.i("Navigation to ${ScreenItem.LoginScreen.route}")
                        navController.navigate(ScreenItem.LoginScreen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                }
            }
        }
    }
    
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color(17, 24, 39, 1))
        .wrapContentSize(Alignment.Center)) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Logo",
                modifier = Modifier.scale(scale.value))
    }
}