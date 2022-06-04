package me.naoti.panelapp.state

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import me.naoti.panelapp.R
import me.naoti.panelapp.builder.getMoshi
import me.naoti.panelapp.network.ApiRoutes
import me.naoti.panelapp.network.ApiService
import me.naoti.panelapp.network.models.UserInfoModel

open class AppContextState (
    val scaffoldState: ScaffoldState,
    val contextState: Context,
    val coroutineScope: CoroutineScope,
    val navController: NavHostController,
) {
    private var isAppBar = false

    var shouldShowAppbar: Boolean
        get() = isAppBar
        set(shouldShow: Boolean) {
            isAppBar = shouldShow
        }

    fun isDarkMode(): Boolean {
        val forceDark = contextState
            .getSharedPreferences(
                contextState.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
            .getBoolean(DARK_MODE_CONTEXT, false)

        val systemDark = when (
            contextState.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        ) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
        return systemDark || forceDark
    }

    fun setDarkMode(forceDark: Boolean = false) {
        contextState
            .getSharedPreferences(
                contextState.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
            .edit()
            .putBoolean(DARK_MODE_CONTEXT, forceDark)
            .apply()
    }

    companion object {
        const val DARK_MODE_CONTEXT = "naotimes_force_dark_mode"
    }
}

class AppState (
    scaffoldState: ScaffoldState, contextState: Context,
    coroutineScope: CoroutineScope, navController: NavHostController,
    var navAppController: NavHostController, val apiState: ApiRoutes,
) : AppContextState(scaffoldState, contextState, coroutineScope, navController) {
    fun getCurrentUser(): UserInfoModel? {
        val userRaw = contextState
            .getSharedPreferences(
                contextState.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
            .getString(USER_CONTEXT, null) ?: return null


        val moshi = getMoshi()
        val jsonAdapter = moshi.adapter(UserInfoModel::class.java)
        return jsonAdapter.fromJson(userRaw)
    }

    fun setCurrentUser(userInfo: UserInfoModel?) {
        if (userInfo == null) {
            contextState.getSharedPreferences(
                    contextState.getString(R.string.app_name),
                    Context.MODE_PRIVATE
                )
                .edit()
                .remove(USER_CONTEXT)
                .apply()
            return
        }
        val moshi = getMoshi()
        val jsonAdapter = moshi.adapter(UserInfoModel::class.java)

        contextState
            .getSharedPreferences(
                contextState.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
            .edit()
            .putString(USER_CONTEXT, jsonAdapter.toJson(userInfo))
            .apply()
    }

    companion object {
        const val USER_CONTEXT = "naotimes_current_user"
    }
}

@Composable
fun rememberAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    contextState: Context = LocalContext.current.applicationContext,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
    navAppController: NavHostController = rememberNavController(),
    apiState: ApiRoutes = rememberApiState(),
) = remember(scaffoldState, contextState, coroutineScope, navController, navAppController, apiState) {
    AppState(scaffoldState, contextState, coroutineScope, navController, navAppController, apiState)
}

@Composable
fun rememberAppContextState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    contextState: Context = LocalContext.current.applicationContext,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
) = remember(scaffoldState, contextState, coroutineScope, navController) {
    AppContextState(scaffoldState, contextState, coroutineScope, navController)
}