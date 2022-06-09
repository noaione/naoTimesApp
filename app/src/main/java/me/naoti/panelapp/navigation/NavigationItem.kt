package me.naoti.panelapp.navigation

import me.naoti.panelapp.R

sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Dashboard : NavigationItem("dashboard", R.drawable.ic_icons_dashboard, "Dashboard")
    object Projects : NavigationItem("projects", R.drawable.ic_icons_projects, "Projects")
    object Settings : NavigationItem("settings", R.drawable.ic_icons_settings, "Settings")
}