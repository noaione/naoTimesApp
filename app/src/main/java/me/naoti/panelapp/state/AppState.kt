package me.naoti.panelapp.state

import android.content.Context
import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.CoroutineScope
import me.naoti.panelapp.R
import me.naoti.panelapp.builder.getMoshi
import me.naoti.panelapp.network.ApiRoutes
import me.naoti.panelapp.network.models.UserInfoModel
import io.github.reactivecircus.cache4k.Cache
import me.naoti.panelapp.network.CookieSenderInterceptor
import me.naoti.panelapp.network.models.ProjectInfoModel
import kotlin.time.Duration.Companion.minutes

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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun rememberAppState(
    contextState: Context = LocalContext.current.applicationContext,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberAnimatedNavController(),
    navAppController: NavHostController = rememberAnimatedNavController(),
    apiState: ApiRoutes = rememberApiState(),
) = remember(contextState, coroutineScope, navController, navAppController, apiState) {
    AppState(contextState, coroutineScope, navController, navAppController, apiState)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun rememberAppContextState(
    contextState: Context = LocalContext.current.applicationContext,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberAnimatedNavController(),
) = remember(contextState, coroutineScope, navController) {
    AppContextState(contextState, coroutineScope, navController)
}