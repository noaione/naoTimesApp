package me.naoti.panelapp.network.models

import androidx.annotation.Nullable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AssignmentKeyValueProject(
    val id: String? = null,
    val name: String? = null,
)

val DefaultAssignment = AssignmentKeyValueProject()

@JsonClass(generateAdapter = true)
data class AssignmentProject(
    @Json(name = "TL")
    val translator: AssignmentKeyValueProject = DefaultAssignment,
    @Json(name = "TLC")
    val translateChecker: AssignmentKeyValueProject = DefaultAssignment,
    @Json(name = "ED")
    val editor: AssignmentKeyValueProject = DefaultAssignment,
    @Json(name = "ENC")
    val encoder: AssignmentKeyValueProject = DefaultAssignment,
    @Json(name = "TM")
    val timer: AssignmentKeyValueProject = DefaultAssignment,
    @Json(name = "TS")
    val typesetter: AssignmentKeyValueProject = DefaultAssignment,
    @Json(name = "QC")
    val qualityChecker: AssignmentKeyValueProject = DefaultAssignment,
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
) {
    fun isSame(other: StatusTickProject): Boolean {
        return (
            translated == other.translated &&
            translateChecked == other.translateChecked &&
            edited == other.edited &&
            encoded == other.encoded &&
            timed == other.timed &&
            typeset == other.typeset &&
            qualityChecked == other.qualityChecked
        )
    }
}

@JsonClass(generateAdapter = true)
data class StatusProject(
    val airtime: Long,
    val episode: Int,
    @Json(name = "is_done")
    val isDone: Boolean,
    var progress: StatusTickProject,
)

data class Project(
    val id: String,
    val title: String,
    val poster: String,
    @Json(name = "start_time")
    val startTime: Long,
    var status: StatusProject,
    val assignments: AssignmentProject,
)

data class ProjectLatestModel(
    val code: Int,
    val data: List<Project>
)
