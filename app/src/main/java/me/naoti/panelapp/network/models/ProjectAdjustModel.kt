package me.naoti.panelapp.network.models

data class ProjectAddAnimeModel(
    val id: String,
    val name: String,
    val episode: Number,
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
)

data class ProjectEpisodeChangeAdjustModel(
    val episodes: List<Number>,
    val animeId: String,
)

data class ProjectEpisodeAdjustModel(
    val event: String,
    val changes: ProjectEpisodeChangeAdjustModel
)

data class ProjectAdjustModel(
    val event: String
)
