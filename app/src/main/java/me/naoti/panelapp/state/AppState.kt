package me.naoti.panelapp.state

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import me.naoti.panelapp.R
import me.naoti.panelapp.builder.getMoshi
import me.naoti.panelapp.network.ApiRoutes
import me.naoti.panelapp.network.models.UserInfoModel
import io.github.reactivecircus.cache4k.Cache
import me.naoti.panelapp.network.CookieSenderInterceptor
import me.naoti.panelapp.network.models.ProjectInfoModel
import kotlin.time.Duration.Companion.minutes

enum class DarkModeOverride(val mode: Int) {
    FollowSystem(-1),
    LightMode(0),
    DarkMode(1);

    companion object {
        fun fromInt(value: Int): DarkModeOverride? {
            return values().firstOrNull { value == it.mode }
        }
    }
}

open class AppContextState (
    val contextState: Context,
    val coroutineScope: CoroutineScope,
    val navController: NavHostController,
) {
    private var isAppBar = false
    private val projectCache = Cache.Builder()
        .expireAfterWrite(3.minutes)
        .build<String, ProjectInfoModel>()

    var shouldShowAppbar: Boolean
        get() = isAppBar
        set(shouldShow) {
            isAppBar = shouldShow
        }

    suspend fun getProjectCache(projectId: String, loader: suspend () -> ProjectInfoModel): ProjectInfoModel {
        return projectCache.get(projectId, loader)
    }

    fun setProjectCache(project: ProjectInfoModel) {
        projectCache.put(project.id, project)
    }

    fun evictCacheProject(projectId: String) {
        try {
            projectCache.invalidate(projectId)
        } catch (e: Throwable) { /* Ignore */ }
    }

    fun isDarkMode(): Boolean {
        val darkPrefs = contextState
            .getSharedPreferences(
                contextState.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
            .getInt(DARK_MODE_CONTEXT, 0)

        val darkOverride = DarkModeOverride.fromInt(darkPrefs) ?: DarkModeOverride.FollowSystem

        val systemDark = when (
            contextState.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        ) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
        if (darkOverride == DarkModeOverride.LightMode) return false
        return systemDark || darkOverride == DarkModeOverride.DarkMode
    }

    fun getDarkMode(): DarkModeOverride {
        val darkPrefs = contextState
            .getSharedPreferences(
                contextState.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
            .getInt(DARK_MODE_CONTEXT, 0)

        return DarkModeOverride.fromInt(darkPrefs) ?: DarkModeOverride.FollowSystem
    }

    fun setDarkMode(darkMode: DarkModeOverride) {
        contextState
            .getSharedPreferences(
                contextState.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
            .edit()
            .putInt(DARK_MODE_CONTEXT, darkMode.mode)
            .apply()
    }

    companion object {
        const val DARK_MODE_CONTEXT = "naotimes_dark_mode_override"
    }
}

class AppState (
    contextState: Context, coroutineScope: CoroutineScope, navController: NavHostController,
    var navAppController: NavHostController, val apiState: ApiRoutes,
) : AppContextState(contextState, coroutineScope, navController) {
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

    fun clearUserCookie() {
        contextState
            .getSharedPreferences(
                contextState.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
            .edit()
            .putStringSet(CookieSenderInterceptor.COOKIE_KEY, HashSet<String>())
            .apply()
    }

    companion object {
        const val USER_CONTEXT = "naotimes_current_user"
    }
}

@Composable
fun rememberAppState(
    contextState: Context = LocalContext.current.applicationContext,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
    navAppController: NavHostController = rememberNavController(),
    apiState: ApiRoutes = rememberApiState(),
) = remember(contextState, coroutineScope, navController, navAppController, apiState) {
    AppState(contextState, coroutineScope, navController, navAppController, apiState)
}

@Composable
fun rememberAppContextState(
    contextState: Context = LocalContext.current.applicationContext,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController(),
) = remember(contextState, coroutineScope, navController) {
    AppContextState(contextState, coroutineScope, navController)
}