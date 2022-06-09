/**
 * Original code by stefanosansone
 * https://github.com/stefanosansone/compose-theme-switcher
 * https://github.com/stefanosansone/compose-theme-switcher/blob/main/app/src/main/java/com/salsbyte/composetheme/preferences/UserSettingsImpl.kt
 */
package me.naoti.panelapp.ui.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.naoti.panelapp.R
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class UserSettingsImpl(
    context: Context
) : UserSettings {

    override val themeStream: MutableStateFlow<DarkModeOverride>
    override var theme: DarkModeOverride by AppThemePreferenceDelegate(
        "naotimes_dark_mode_override", DarkModeOverride.FollowSystem
    )
    override val refreshStream: MutableStateFlow<Boolean>
    override var refresh: Boolean by RefreshStateDelegate(
        "naotimes_force_refresh_view", false
    )

    private val preferences: SharedPreferences =
        context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    init {
        themeStream = MutableStateFlow(theme)
        refreshStream = MutableStateFlow(refresh)
    }

    inner class AppThemePreferenceDelegate(
        private val name: String,
        private val default: DarkModeOverride,
    ) : ReadWriteProperty<Any?, DarkModeOverride> {

        override fun getValue(thisRef: Any?, property: KProperty<*>): DarkModeOverride =
            DarkModeOverride.fromInt(preferences.getInt(name, default.mode)) ?: default

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: DarkModeOverride) {
            themeStream.value = value
            preferences.edit {
                putInt(name, value.mode)
            }
        }
    }


    inner class RefreshStateDelegate(
        private val name: String,
        private val default: Boolean,
    ) : ReadWriteProperty<Any?, Boolean> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
            return preferences.getBoolean(name, default)
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
            refreshStream.value = value
            preferences.edit {
                putBoolean(name, value)
            }
        }
    }

}