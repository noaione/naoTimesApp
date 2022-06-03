package me.naoti.panelapp.ui.components

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import me.naoti.panelapp.state.rememberAppContextState

@Composable
fun SuccessPopup(text: String, onClosed: (() -> Unit)? = null) {
    val appCtx = rememberAppContextState()
    var color = Color(34, 177, 76)
    if (appCtx.isDarkMode()) {
        color = Color(41, 214, 92)
    }
    SnackbarBase(text = text, color = color, onClosed = onClosed)
}

@Preview(showBackground = true)
@Composable
fun SuccessPopupPreview() {
    SuccessPopup(text = "This is a test!")
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SuccessPopupPreviewDark() {
    SuccessPopup(text = "This is a test!")
}