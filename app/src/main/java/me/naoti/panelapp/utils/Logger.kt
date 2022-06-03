package me.naoti.panelapp.utils

import android.util.Log

class Logger(
    private val Tag: String,
) {
    private fun serializeToString(data: Any): String {
        return data.toString()
    }

    fun info(msg: Any) {
        Log.i(Tag, serializeToString(msg))
    }

    fun warn(msg: Any) {
        Log.w(Tag, serializeToString(msg))
    }

    fun debug(msg: Any) {
        Log.d(Tag, serializeToString(msg))
    }

    fun error(msg: Any) {
        Log.e(Tag, serializeToString(msg))
    }

    fun verbose(msg: Any) {
        Log.v(Tag, serializeToString(msg))
    }

    fun wtf(msg: Any) {
        Log.wtf(Tag, serializeToString(msg))
    }

    fun i(msg: Any) = info(msg)
    fun w(msg: Any) = warn(msg)
    fun d(msg: Any) = debug(msg)
    fun e(msg: Any) = error(msg)
    fun v(msg: Any) = verbose(msg)

    companion object {
        fun get(Tag: String): Logger {
            var actualTag = Tag
            if (!Tag.contains(".")) {
                actualTag = "naoTimes.$actualTag"
            }
            return Logger(Tag)
        }
    }
}

fun getLogger(tag: String = "Main"): Logger {
    return Logger.get(tag)
}