package me.naoti.panelapp.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import me.naoti.panelapp.navigation.NavigationItem
import me.naoti.panelapp.ui.theme.NaoTimesTheme
import me.naoti.panelapp.utils.getLogger


@Composable
fun BottomNavigationBar(navController: NavController) {
    val log = getLogger("BottomNavigationBar")
    log.i("Initiating navigation bar...")
    val items = listOf(
        NavigationItem.Dashboard,
        NavigationItem.Projects,
        NavigationItem.Settings
    )
    NavigationBar {
        log.i("Preparing back stack entry...")
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        log.i("Creating navigqation item")
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.title) },
                label = { Text(text = item.title) },
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.primary,
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                ),
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // re-selecting the same item
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
    NaoTimesTheme {
        BottomNavigationBar(navController)
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun BottomNavigationBarDarkPreview() {
    val navController = rememberNavController()
    NaoTimesTheme {
        BottomNavigationBar(navController)
    }
}