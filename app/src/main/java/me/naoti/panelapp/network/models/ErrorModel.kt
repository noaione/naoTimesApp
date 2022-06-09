package me.naoti.panelapp.network.models

import com.squareup.moshi.JsonClass
import me.naoti.panelapp.network.ErrorCode

@JsonClass(generateAdapter = true)
data class ErrorModel(
    val success: Boolean,
    val code: ErrorCode? = null,
    val error: String? = null,
)

@JsonClass(generateAdapter = true)
data class ErrorModelWithData<T>(
    val success: Boolean? = null,
    val code: ErrorCode? = null,
    val error: String? = null,
    val data: T? = null,
)

