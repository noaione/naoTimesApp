package me.naoti.panelapp.builder

import java.text.SimpleDateFormat
import java.util.*

data class TimeString(
    val unixEpoch: Long = System.currentTimeMillis() / 1000L,
    val timeZone: TimeZone = TimeZone.getDefault()
) {
    fun toString(s: String): String {
        val sdf = SimpleDateFormat(s, Locale.getDefault())
        val netDate = Date(unixEpoch * 1000L)
        return sdf.format(netDate)
    }
    override fun toString(): String {
        return toString("E, MMM dd yyyy kk:mm:ss z")
    }

    companion object {
        fun fromUnix(unixEpoch: Long, timeZone: TimeZone): TimeString {
            return TimeString(unixEpoch, timeZone)
        }
        fun fromUnix(unixEpoch: Long): TimeString {
            return fromUnix(unixEpoch, TimeZone.getDefault())
        }

        fun fromUnixMillis(unixEpochMillis: Long, timeZone: TimeZone): TimeString {
            return fromUnix(unixEpochMillis / 1000L, timeZone)
        }
        fun fromUnixMillis(unixEpochMillis: Long): TimeString {
            return fromUnixMillis(unixEpochMillis, TimeZone.getDefault())
        }
    }
}