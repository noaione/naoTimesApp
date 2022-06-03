package me.naoti.panelapp.builder

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import me.naoti.panelapp.network.adapter.ErrorCodeAdapter
import me.naoti.panelapp.network.adapter.IntToErrorCodeAdapter

fun getMoshi(): Moshi {
    return Moshi.Builder()
        .add(ErrorCodeAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()
}