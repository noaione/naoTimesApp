package me.naoti.panelapp.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class ProjectAddAnimeModel(
    val id: String,
    val name: String,
    val episode: Int,
)

data class ProjectAddRoleModel(
    val id: String,
    val role: String,
)

data class ProjectAddModel(
    val server: String,
    val anime: ProjectAddAnimeModel,
    val roles: List<ProjectAddRoleModel>,
)

data class ProjectRemoveModel(
    val animeId: String,
) {
    companion object {
        fun fromProject(project: Project): ProjectRemoveModel {
            return ProjectRemoveModel(animeId = project.id)
        }

        fun fromProject(project: ProjectInfoModel): ProjectRemoveModel {
            return ProjectRemoveModel(animeId = project.id)
        }
    }
}

@JsonClass(generateAdapter = true)
data class ProjectEpisodeChangeAdjustModel(
    val episodes: List<Int>,
    val animeId: String,
)

@JsonClass(generateAdapter = true)
data class ProjectEpisodeRemoveModel(
    val changes: ProjectEpisodeChangeAdjustModel,
    val event: String = "remove",
) {
    companion object {
        fun create(animeId: String, episodes: List<Int>): ProjectEpisodeRemoveModel {
            return ProjectEpisodeRemoveModel(
                changes = ProjectEpisodeChangeAdjustModel(episodes, animeId)
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class ProjectEpisodeAddModel(
    val changes: ProjectEpisodeChangeAdjustModel,
    val event: String = "add",
) {
    companion object {
        fun create(animeId: String, episodes: List<Int>): ProjectEpisodeAddModel {
            return ProjectEpisodeAddModel(
                changes = ProjectEpisodeChangeAdjustModel(episodes, animeId)
            )
        }
    }
}

@JsonClass(generateAdapter = true)
data class ProjectEpisodeRemovedResponse(
    val episode: Int,
    val index: Int,
)

@JsonClass(generateAdapter = true)
data class ProjectUpdateContentStaff(
    val role: String,
    @Json(name = "anime_id")
    val projectId: String,
    @Json(name = "user_id")
    val userId: String? = null,
)

@JsonClass(generateAdapter = true)
data class ProjectUpdateContentStatusRoleTick(
    val role: String,
    @Json(name = "tick")
    val isDone: Boolean,
)

@JsonClass(generateAdapter = true)
data class ProjectUpdateContentStatus(
    val episode: Int,
    @Json(name = "anime_id")
    val projectId: String,
    val roles: List<ProjectUpdateContentStatusRoleTick>
)

@JsonClass(generateAdapter = true)
data class ProjectAdjustStaffModel(
    val changes: ProjectUpdateContentStaff,
    val event: String = "staff",
)

@JsonClass(generateAdapter = true)
data class ProjectAdjustStatusModel(
    val changes: ProjectUpdateContentStatus,
    val event: String = "status",
)

@JsonClass(generateAdapter = true)
data class ProjectAdjustReleaseModel(
    val episode: Int,
    @Json(name = "anime_id")
    val projectId: String,
    @Json(name = "is_done")
    val isDone: Boolean = false,
)

// Response Model
@JsonClass(generateAdapter = true)
data class ProjectAdjustStatusResponseInner(
    val progress: StatusTickProject,
)

@JsonClass(generateAdapter = true)
data class ProjectAdjustStatusResponse(
    val success: Boolean,
    val results: ProjectAdjustStatusResponseInner? = null,
)

@JsonClass(generateAdapter = true)
data class ProjectAdjustStaffResponse(
    val success: Boolean,
    val id: String? = null,
    val name: String? = null,
)