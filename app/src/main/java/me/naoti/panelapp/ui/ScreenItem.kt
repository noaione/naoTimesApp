package me.naoti.panelapp.ui

sealed class ScreenItem(val route: String) {
    object LoginScreen : ScreenItem("login_screen")
    object RegisterScreen : ScreenItem("register_screen")
    object SplashScreen : ScreenItem("splash_screen")

    object AppScaffold : ScreenItem("app_scaffolding")
}

