package me.naoti.panelapp.network.models

import androidx.annotation.Nullable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssignmentKeyValueProject(
    val id: String? = null,
    val name: String? = null,
)

data class AssignmentProject(
    @Json(name = "TL")
    val translator: AssignmentKeyValueProject,
    @Json(name = "TLC")
    val translateChecker: AssignmentKeyValueProject,
    @Json(name = "ED")
    val editor: AssignmentKeyValueProject,
    @Json(name = "ENC")
    val encoder: AssignmentKeyValueProject,
    @Json(name = "TM")
    val timer: AssignmentKeyValueProject,
    @Json(name = "TS")
    val typesetter: AssignmentKeyValueProject,
    @Json(name = "QC")
    val qualityChecker: AssignmentKeyValueProject,
)

data class StatusTickProject(
    @Json(name = "TL")
    val translated: Boolean = false,
    @Json(name = "TLC")
    val translateChecked: Boolean = false,
    @Json(name = "ED")
    val edited: Boolean = false,
    @Json(name = "ENC")
    val encoded: Boolean = false,
    @Json(name = "TM")
    val timed: Boolean = false,
    @Json(name = "TS")
    val typeset: Boolean = false,
    @Json(name = "QC")
    val qualityChecked: Boolean = false,
)

data class StatusProject(
    val airtime: Int,
    val episode: Int,
    @Json(name = "is_done")
    val isDone: Boolean,
    val progress: StatusTickProject,
)

data class Project(
    val id: String,
    val title: String,
    val poster: String,
    @Json(name = "start_time")
    val startTime: Int,
    val status: StatusProject,
    val assignments: AssignmentProject,
)

data class ProjectLatestModel(
    val code: Int,
    val data: List<Project>
)
