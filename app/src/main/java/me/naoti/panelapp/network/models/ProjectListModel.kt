package me.naoti.panelapp.network.models

import com.squareup.moshi.Json

data class ProjectListModel(
    val id: String,
    val title: String,
    @Json(name = "is_finished")
    val isDone: Boolean,
    val poster: String,
    val assignments: AssignmentProject
)
