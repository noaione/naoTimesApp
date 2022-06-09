package me.naoti.panelapp.utils

fun mapBoolean(text: Any?): Boolean {
    if (text == null) {
        return false
    }
    if (text is Boolean) {
        return text
    }
    return when (text.toString().lowercase()) {
        "y" -> true
        "enable" -> true
        "true" -> true
        "yes" -> true
        "1" -> true
        "on" -> true
        else -> false
    }
}