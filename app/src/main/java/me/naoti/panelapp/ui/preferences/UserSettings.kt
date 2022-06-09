/**
 * Original code by stefanosansone
 * https://github.com/stefanosansone/compose-theme-switcher
 * https://github.com/stefanosansone/compose-theme-switcher/blob/main/app/src/main/java/com/salsbyte/composetheme/preferences/UserSettings.kt
 */

package me.naoti.panelapp.ui.preferences

import kotlinx.coroutines.flow.StateFlow

enum class DarkModeOverride(val mode: Int) {
    FollowSystem(-1) {
        override fun toName(): String = "Follow System"
    },
    LightMode(0) {
        override fun toName(): String = "Off"
    },
    DarkMode(1){
        override fun toName(): String = "On"
    };

    abstract fun toName(): String

    companion object {
        fun fromInt(value: Int): DarkModeOverride? {
            return values().firstOrNull { value == it.mode }
        }
    }
}

interface UserSettings {
    val themeStream: StateFlow<DarkModeOverride>
    var theme: DarkModeOverride
}