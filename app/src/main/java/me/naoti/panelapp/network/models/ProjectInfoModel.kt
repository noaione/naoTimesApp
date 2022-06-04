package me.naoti.panelapp.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

//{
//    "id": "105914",
//    "mal_id": 38759,
//    "title": "Sewayaki Kitsune no Senko-san",
//    "role_id": "836865120058605568",
//    "start_time": 1554854400,
//    "assignments": {
//    "TL": {
//    "id": "466469077444067372",
//    "name": "N4O"
//},
//    "TLC": {
//    "id": "579096424344715274",
//    "name": "N-"
//},
//    "ENC": {
//    "id": null,
//    "name": null
//},
//    "ED": {
//    "id": "123",
//    "name": null
//},
//    "TM": {
//    "id": null,
//    "name": null
//},
//    "TS": {
//    "id": null,
//    "name": null
//},
//    "QC": {
//    "id": null,
//    "name": null
//}
//},
//    "status": [
//    {
//        "episode": 1,
//        "is_done": true,
//        "progress": {
//        "TL": false,
//        "TLC": true,
//        "ENC": true,
//        "ED": false,
//        "TM": false,
//        "TS": false,
//        "QC": false
//    },
//        "airtime": 1554903000,
//        "delay_reason": null
//    },
//    {
//        "episode": 2,
//        "is_done": false,
//        "progress": {
//        "TL": false,
//        "TLC": false,
//        "ENC": false,
//        "ED": false,
//        "TM": false,
//        "TS": false,
//        "QC": false
//    },
//        "airtime": 1555507800,
//        "delay_reason": "test delay teks aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
//    },
//    {
//        "episode": 3,
//        "is_done": false,
//        "progress": {
//        "TL": false,
//        "TLC": false,
//        "ENC": false,
//        "ED": false,
//        "TM": false,
//        "TS": false,
//        "QC": false
//    },
//        "airtime": 1556112600,
//        "delay_reason": null
//    },
//    {
//        "episode": 4,
//        "is_done": false,
//        "progress": {
//        "TL": false,
//        "TLC": false,
//        "ENC": false,
//        "ED": false,
//        "TM": false,
//        "TS": false,
//        "QC": false
//    },
//        "airtime": 1556717400,
//        "delay_reason": null
//    },
//    {
//        "episode": 5,
//        "is_done": false,
//        "progress": {
//        "TL": false,
//        "TLC": false,
//        "ENC": false,
//        "ED": false,
//        "TM": false,
//        "TS": false,
//        "QC": false
//    },
//        "airtime": 1557322200,
//        "delay_reason": null
//    },
//    {
//        "episode": 6,
//        "is_done": false,
//        "progress": {
//        "TL": false,
//        "TLC": false,
//        "ENC": false,
//        "ED": false,
//        "TM": false,
//        "TS": false,
//        "QC": false
//    },
//        "airtime": 1557927000,
//        "delay_reason": null
//    },
//    {
//        "episode": 7,
//        "is_done": false,
//        "progress": {
//        "TL": false,
//        "TLC": false,
//        "ENC": false,
//        "ED": false,
//        "TM": false,
//        "TS": false,
//        "QC": false
//    },
//        "airtime": 1558531800,
//        "delay_reason": null
//    },
//    {
//        "episode": 8,
//        "is_done": false,
//        "progress": {
//        "TL": false,
//        "TLC": false,
//        "ENC": false,
//        "ED": false,
//        "TM": false,
//        "TS": false,
//        "QC": false
//    },
//        "airtime": 1559136600,
//        "delay_reason": null
//    },
//    {
//        "episode": 9,
//        "is_done": false,
//        "progress": {
//        "TL": false,
//        "TLC": false,
//        "ENC": false,
//        "ED": false,
//        "TM": false,
//        "TS": false,
//        "QC": false
//    },
//        "airtime": 1559741400,
//        "delay_reason": null
//    },
//    {
//        "episode": 10,
//        "is_done": false,
//        "progress": {
//        "TL": false,
//        "TLC": false,
//        "ENC": false,
//        "ED": false,
//        "TM": false,
//        "TS": false,
//        "QC": false
//    },
//        "airtime": 1560346200,
//        "delay_reason": null
//    },
//    {
//        "episode": 11,
//        "is_done": false,
//        "progress": {
//        "TL": false,
//        "TLC": false,
//        "ENC": false,
//        "ED": false,
//        "TM": false,
//        "TS": false,
//        "QC": false
//    },
//        "airtime": 1560951000,
//        "delay_reason": null
//    },
//    {
//        "episode": 12,
//        "is_done": false,
//        "progress": {
//        "TL": false,
//        "TLC": false,
//        "ENC": false,
//        "ED": false,
//        "TM": false,
//        "TS": false,
//        "QC": false
//    },
//        "airtime": 1561555800,
//        "delay_reason": null
//    }
//    ],
//    "poster_data": {
//    "url": "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx105914-VXKB0ZA2aVZF.png",
//    "color": 14983491
//},
//    "fsdb_data": null,
//    "aliases": [
//    "konkon",
//    "foxwaifu",
//    "pepega"
//    ],
//    "kolaborasi": [],
//    "last_update": 1650905434
//}

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
    val statuses: List<StatusProject>,
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
    listOf(),
    ProjectPosterInfoModel("InvalidUrl"),
    listOf(),
    -1
)