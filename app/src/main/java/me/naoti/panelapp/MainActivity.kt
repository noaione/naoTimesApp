package me.naoti.panelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.state.rememberAppState
import me.naoti.panelapp.ui.AppScaffold
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.ui.screens.*
import me.naoti.panelapp.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val navMainController = rememberNavController()
            val appState = rememberAppState(navController = navController, navAppController = navMainController)
            NaoTimesView(appState)
        }
    }
}

@Composable
fun NaoTimesView(appState: AppState) {
    NaoTimesTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            color = MaterialTheme.colors.background
        ) {
            NavHost(navController = appState.navController, startDestination = ScreenItem.SplashScreen.route) {
                composable(ScreenItem.SplashScreen.route) {
                    SplashScreen(appState)
                }
                composable(ScreenItem.LoginScreen.route) {
                    LoginScreen(appState.navController)
                }
                composable(ScreenItem.RegisterScreen.route) {
                    RegisterScreen(appState.navController)
                }
                composable(ScreenItem.AppScaffold.route) {
                    AppScaffold(appState)
                }
                composable(ScreenItem.ProjectScreen.route) { stack ->
                    ProjectScreen(appState, stack.arguments?.getString("projectId"))
                }
                composable(ScreenItem.ProjectAddScreen.route) {
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