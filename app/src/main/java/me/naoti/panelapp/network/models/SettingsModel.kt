package me.naoti.panelapp.network.models

import androidx.annotation.Nullable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class SettingsAdjustAdmin(
    val adminIds: List<String>
)

@JsonClass(generateAdapter = true)
data class SettingsAdjustAnnouncer(
    @Json(name = "channelid")
    val channelId: String,
    val toRemove: Boolean?
)

data class SettingsAdjustName(
    @Json(name = "newname")
    val newName: String
)

data class SettingsAdjustPassword(
    @Json(name = "old")
    val oldPassword: String,
    @Json(name = "new")
    val newPassword: String
)

data class ChannelFindResult(
    val id: String,
    val name: String
)

data class SettingsChannelFindModel(
    val results: List<ChannelFindResult>,
    val success: Boolean
)

data class SettingsModel(
    val id: String,
    @Json(name = "serverowner")
    val admins: List<String>,
    @Nullable
    @Json(name = "announce_channel")
    val announceChannel: String,
)