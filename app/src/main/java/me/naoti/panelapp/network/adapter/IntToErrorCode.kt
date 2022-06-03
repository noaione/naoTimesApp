package me.naoti.panelapp.network.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.ToJson
import me.naoti.panelapp.network.ErrorCode

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
internal annotation class IntToErrorCode

/**
 * Convert a specific type from API error code to a enum of custom error code
 */
class IntToErrorCodeAdapter {
    @FromJson @IntToErrorCode fun fromJson(errorCode: Int): ErrorCode {
        return ErrorCode.values().first { it.actual == errorCode || it.alias == errorCode }
    }

    @ToJson fun toJson(@IntToErrorCode errorCode: ErrorCode): Int {
        return errorCode.actual
    }
}

class ErrorCodeAdapter {
    @ToJson fun toJson(value: ErrorCode): Int {
        return value.actual
    }

    @FromJson fun fromJson(value: Int): ErrorCode {
        return ErrorCode.values().first {
            it.actual == value || it.alias == value
        }
    }
}