package me.naoti.panelapp.network

enum class ErrorCode(val actual: Int, val alias: Int? = null, val customMsg: String? = null) {
    Success(200, 2000) {
        override fun asText() = "Success"
        override fun asText(extra: String) = "Success"
    },
    BadRequest(400) {
        override fun asText() = "Bad Request to API"
        override fun asText(extra: String) = "Bad request to API"
    },
    Unauthorized(403) {
        override fun asText() = "You're Unauthorized"
        override fun asText(extra: String) = "You're Unauthorized"
    },
    NotFound(404) {
        override fun asText() = "Object not found!"
        override fun asText(extra: String) = "$extra not found!"
    },
    MethodNotAllowed(405) {
        override fun asText() = "HTTP Method is wrong!"
        override fun asText(extra: String) = "HTTP Method is wrong!"
    },
    ServerError(500, 5000) {
        override fun asText() = "An internal server error occurred!"
        override fun asText(extra: String) = "An internal server error occurred!"
    },
    NotImplemented(501) {
        override fun asText() = "HTTP Route not Implemented!"
        override fun asText(extra: String) = "$extra Route not Implemented!"
    },

    // Custom error code
    // Login error
    WrongPassword(4001) {
        override fun asText() = "Incorrect password!"
        override fun asText(extra: String) = "Incorrect password!"
    },
    UnknownServerID(4002) {
        override fun asText() = "Unable to find provided server ID!"
        override fun asText(extra: String) = "Unable to find $extra server!"
    },
    // Registration error
    ServerNotFound(4100) {
        override fun asText() = "Bot unable to find provided server ID, please invite the bot!"
        override fun asText(extra: String) = "Bot unable to find $extra server, please invite the bot!"
    },
    UserNotFound(4101) {
        override fun asText() = "Bot unable to find provided user ID!"
        override fun asText(extra: String) = "Bot unable to find user $extra"
    },
    MissingPermission(4102) {
        override fun asText() = "User missing required permission!"
        override fun asText(extra: String) = "User missing permission: $extra"
    },
    UserIsBot(4103) {
        override fun asText() = "User is bot!"
        override fun asText(extra: String) = "User is bot!"
    },
    ServerRegistered(4104) {
        override fun asText() = "Server already registered!"
        override fun asText(extra: String) = "Server $extra already registered!"
    },

    // Other
    UnknownError(5999) {
        override fun asText(): String {
            if (customMsg != null) {
                return customMsg
            }
            return "An unknown error has occurred!"
        }
        override fun asText(extra: String): String {
            if (customMsg != null) {
                return customMsg
            }
            return extra
        }
    };

    abstract fun asText(): String
    abstract fun asText(extra: String): String

    companion object {
        fun get(code: Int): ErrorCode? {
            return ErrorCode.values().firstOrNull {
                it.actual == code || it.alias == code
            }
        }
    }
}
