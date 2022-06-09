package me.naoti.panelapp.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import me.naoti.panelapp.network.ErrorCode
//import me.naoti.panelapp.network.adapter.IntToErrorCode

@JsonClass(generateAdapter = true)
data class AuthModel(
    val id: String? = null,
    val privilege: String? = null,
    val name: String? = null,
    val loggedIn: Boolean,
    val error: String,
    val code: ErrorCode,
)

@JsonClass(generateAdapter = true)
data class UserInfoModel(
    val id: String?,
    val privilege: String?,
    val name: String?,
    val loggedIn: Boolean,
    @Json(name = "serverowner")
    val admins: List<String>?,
    @Json(name = "announce_channel")
    val announceChannel: String?,
) {
    fun rebuild(
        id: String? = null,
        privilege: String? = null,
        name: String? = null,
        loggedIn: Boolean? = null,
        admins: List<String>? = null,
        announceChannel: String? = null,
    ): UserInfoModel {
        val actId = id ?: this.id
        val actPrivilege = privilege ?: this.privilege
        val actName = name ?: this.name
        val actLogIn = if (loggedIn is Boolean) loggedIn else this.loggedIn
        val actAdmins = admins ?: this.admins
        val actChannel = announceChannel ?: this.announceChannel
        return UserInfoModel(
            id = actId,
            privilege = actPrivilege,
            name = actName,
            loggedIn = actLogIn,
            admins = actAdmins,
            announceChannel = actChannel
        )
    }
}

data class LoginModel(
    val server: String,
    val password: String,
)

data class RegisterModel(
    val server: String,
    val admin: String,
)