package me.naoti.panelapp.state

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import me.naoti.panelapp.network.ApiService

@Composable
fun rememberApiState(
    context: Context = LocalContext.current.applicationContext,
) = remember(context) {
    ApiService.getService(context)
}
