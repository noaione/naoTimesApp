package me.naoti.panelapp.ui.components

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun FailurePopup(text: String, onClosed: (() -> Unit)? = null) {
    SnackbarBase(text, Color.Red, onClosed)
}

@Preview(showBackground = true)
@Composable
fun FailurePopupPreview() {
    FailurePopup(text = "This is a test!")
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FailurePopupPreviewDark() {
    FailurePopup(text = "This is a test!")
}