package me.naoti.panelapp.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProjectPosterInfoModel(
    val url: String,
    val color: Int? = null,
)

@JsonClass(generateAdapter = true)
data class ProjectInfoModel(
    val id: String,
    @Json(name = "mal_id")
    val malId: String?,
    val title: String,
    @Json(name = "role_id")
    val roleId: String?,
    @Json(name = "start_time")
    val startTime: Int?,
    val assignments: AssignmentProject,
    @Json(name = "status")
    var statuses: MutableList<StatusProject>,
    @Json(name = "poster_data")
    val poster: ProjectPosterInfoModel,
    val aliases: List<String>,
    @Json(name = "last_update")
    val lastUpdate: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ProjectInfoModel
        return other.id == id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (malId?.hashCode() ?: 0)
        result = 31 * result + title.hashCode()
        result = 31 * result + (roleId?.hashCode() ?: 0)
        result = 31 * result + (startTime ?: 0)
        result = 31 * result + assignments.hashCode()
        result = 31 * result + statuses.hashCode()
        result = 31 * result + poster.hashCode()
        result = 31 * result + aliases.hashCode()
        result = 31 * result + lastUpdate
        return result
    }
}

val DefaultEmptyProject = ProjectInfoModel(
    "-123",
    null,
    "DEFAULT_NOT_ACTIVE_PROJECT",
    null,
    null,
    AssignmentProject(),
    mutableListOf(),
    ProjectPosterInfoModel("InvalidUrl"),
    listOf(),
    -1
)