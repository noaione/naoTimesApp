package me.naoti.panelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.state.rememberAppState
import me.naoti.panelapp.ui.AppScaffold
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.ui.screens.*
import me.naoti.panelapp.ui.theme.*

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberAnimatedNavController()
            val navMainController = rememberAnimatedNavController()
            val appState = rememberAppState(navController = navController, navAppController = navMainController)
            NaoTimesView(appState)
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NaoTimesView(appState: AppState) {
    NaoTimesTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedNavHost(
                navController = appState.navController,
                startDestination = ScreenItem.SplashScreen.route
            ) {
                composable(
                    ScreenItem.SplashScreen.route,
                ) {
                    SplashScreen(appState)
                }
                composable(
                    ScreenItem.LoginScreen.route,
                    exitTransition = {
                        when (initialState.destination.route) {
                            ScreenItem.AppScaffold.route ->
                                slideOutOfContainer(
                                    AnimatedContentScope.SlideDirection.Up,
                                    animationSpec = tween(750)
                                )
                            ScreenItem.RegisterScreen.route -> {
                                slideOutOfContainer(
                                    AnimatedContentScope.SlideDirection.Left,
                                    animationSpec = tween(750)
                                )
                            }
                            else -> null
                        }
                    }
                ) {
                    LoginScreen(appState.navController)
                }
                composable(
                    ScreenItem.RegisterScreen.route,
                    exitTransition = {
                        when (initialState.destination.route) {
                            ScreenItem.AppScaffold.route ->
                                slideOutOfContainer(
                                    AnimatedContentScope.SlideDirection.Up,
                                    animationSpec = tween(750)
                                )
                            ScreenItem.LoginScreen.route -> {
                                slideOutOfContainer(
                                    AnimatedContentScope.SlideDirection.Right,
                                    animationSpec = tween(750)
                                )
                            }
                            else -> null
                        }
                    }
                ) {
                    RegisterScreen(appState)
                }
                composable(ScreenItem.AppScaffold.route) {
                    AppScaffold(appState)
                }
                composable(
                    ScreenItem.ProjectScreen.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Up,
                            animationSpec = tween(750)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Down,
                            animationSpec = tween(750)
                        )
                    }
                ) { stack ->
                    ProjectScreen(appState, stack.arguments?.getString("projectId"))
                }
                composable(
                    ScreenItem.ProjectAddScreen.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentScope.SlideDirection.Up,
                            animationSpec = tween(750)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentScope.SlideDirection.Down,
                            animationSpec = tween(750)
                        )
                    }
                ) {
                    ProjectAddScreen(appState)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NaoTimesViewPreview() {
    NaoTimesView(rememberAppState())
}