package me.naoti.panelapp.ui

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

sealed class ScreenItem(val route: String) {
    object LoginScreen : ScreenItem("login_screen")
    object RegisterScreen : ScreenItem("register_screen")
    object SplashScreen : ScreenItem("splash_screen")

    object AppScaffold : ScreenItem("app_scaffolding")
    object ProjectScreen : ScreenItem("app_project_screen/{projectId}?source={source}")
    object ProjectAddScreen : ScreenItem("app_project_add_screen")
}

fun NavOptionsBuilder.popUpToTop(controller: NavController) {
    popUpTo(controller.currentBackStackEntry?.destination?.route ?: return) {
        inclusive = true
    }
}