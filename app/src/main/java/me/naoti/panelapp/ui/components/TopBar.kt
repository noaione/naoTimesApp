package me.naoti.panelapp.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import me.naoti.panelapp.state.rememberAppContextState
import me.naoti.panelapp.ui.theme.NaoTimesTheme


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
    SmallTopAppBar(
        title = { Text(naoTimesText(), fontSize = 18.sp) },
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun TopBarPreview() {
    NaoTimesTheme {
        TopBar()
    }
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun TopBarPreviewDark() {
    NaoTimesTheme {
        TopBar()
    }
}

