package me.naoti.panelapp.network.models

import androidx.annotation.Nullable
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

data class DateModel(
    val year: String,
)

data class TitleModel(
    @Nullable
    val romaji: String,
    @Nullable
    val native: String,
    @Nullable
    val english: String,
)

@JsonClass(generateAdapter = true)
data class CoverImageModel(
    val medium: String?,
    val large: String?,
    val extraLarge: String?,
)

data class AnimeMatchModel(
    val id: Number,
    @Nullable
    val idMal: Number,
    val format: String,
    val season: String,
    val seasonYear: Number,
    val startDate: DateModel,
    val title: TitleModel,
    @Json(name = "titlematch")
    val titleMatch: String,
    @Json(name = "titlematchen")
    val titleMatchEnglish: String,
    @Json(name = "titlematchother")
    val titleMatchOther: String,
)

data class AnimeFindModel(
    val results: List<AnimeMatchModel>
)