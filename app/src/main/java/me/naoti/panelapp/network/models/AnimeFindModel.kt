package me.naoti.panelapp.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DateModel(
    val year: String? = null,
)

@JsonClass(generateAdapter = true)
data class TitleModel(
    val romaji: String? = null,
    val native: String? = null,
    val english: String? = null,
)

@JsonClass(generateAdapter = true)
data class CoverImageModel(
    val medium: String? = null,
    val large: String? = null,
    val extraLarge: String? = null,
)

@JsonClass(generateAdapter = true)
data class AnimeMatchModel(
    val id: Int,
    val idMal: Int?,
    val format: String?,
    val season: String?,
    val seasonYear: Int?,
    val startDate: DateModel?,
    val coverImage: CoverImageModel,
    val title: TitleModel,
    @Json(name = "titlematch")
    val titleMatch: String,
    @Json(name = "titlematchen")
    val titleMatchEnglish: String,
    @Json(name = "titlematchother")
    val titleMatchOther: String,
) {
    fun getTitle(): String {
        return title.romaji ?: (title.english ?: (title.native ?: "????"))
    }

    fun asResult(): String {
        val format = format ?: "???"
        val startDate = startDate?.year ?: "Unknown Year"
        val selTitle = getTitle()

        return "$selTitle ($startDate) [$format] [$id]"
    }
}

@JsonClass(generateAdapter = true)
data class AnimeFindModel(
    val results: List<AnimeMatchModel>
)