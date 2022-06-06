package me.naoti.panelapp.ui.components

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import me.naoti.panelapp.R
import me.naoti.panelapp.state.rememberAppContextState
import me.naoti.panelapp.ui.theme.Gray200
import me.naoti.panelapp.ui.theme.Gray800
import me.naoti.panelapp.ui.theme.Gray900
import me.naoti.panelapp.ui.theme.White


fun naoTimesText(): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
            append("nao")
        }
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("Times")
        }
    }
}


@Composable
fun TopBar() {
    val appCtx = rememberAppContextState()
    TopAppBar(
        title = { Text(naoTimesText(), fontSize = 18.sp) },
        backgroundColor = if (appCtx.isDarkMode()) Gray900 else Gray200,
        contentColor = if (appCtx.isDarkMode()) White else Gray800
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    TopBar()
}
