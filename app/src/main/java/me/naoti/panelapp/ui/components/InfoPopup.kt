package me.naoti.panelapp.ui.components

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import me.naoti.panelapp.state.rememberAppContextState

@Composable
fun InfoPopup(text: String, onClosed: (() -> Unit)? = null) {
    val appCtx = rememberAppContextState()
    var color = Color(0, 162, 232)
    if (appCtx.isDarkMode()) {
        color = Color(3, 191, 242)
    }
    SnackbarBase(text = text, color = color, onClosed = onClosed)
}

@Preview(showBackground = true)
@Composable
fun InfoPopupPreview() {
    InfoPopup(text = "This is a test!")
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun InfoPopupPreviewDark() {
    InfoPopup(text = "This is a test!")
}