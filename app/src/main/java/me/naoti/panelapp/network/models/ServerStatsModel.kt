package me.naoti.panelapp.network.models

data class StatsKeyValueModel(
    val key: String,
    val data: Int,
)

data class ServerStatsModel(
    val code: Int,
    val data: List<StatsKeyValueModel>,
)
