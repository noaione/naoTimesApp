package me.naoti.panelapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import kotlinx.coroutines.launch
import me.naoti.panelapp.state.rememberAppContextState

@Composable
fun SnackbarBase(text: String, color: Color = Color.Unspecified, onClosed: (() -> Unit)? = null) {
    Scaffold {
        val appCtx = rememberAppContextState()
        val snackState = SnackbarHostState()

        SnackbarView(snackState, color)
        LaunchedEffect(key1 = true) {
            appCtx.coroutineScope.launch {
                snackState.showSnackbar(
                    message = text,
                    actionLabel = "CLOSE",
                    duration = SnackbarDuration.Short,
                )
                if (onClosed != null) {
                    onClosed()
                }
            }
        }
    }
}

@Composable
internal fun SnackbarView(snackbarHostState: SnackbarHostState, color: Color = Color.Unspecified) {
    val appCtx = rememberAppContextState()
    val isDark = appCtx.isDarkMode()
    val targetColor = if (color == Color.Unspecified) (if (isDark) Color.White else Color.DarkGray) else color
    val bgColor = if (isDark) Color.DarkGray else Color.LightGray
    val textColor = if (isDark) Color.White else Color.DarkGray
    
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (snackRef) = createRefs()
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.
                constrainAs(snackRef) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            snackbar = {
                Snackbar(
                    backgroundColor = bgColor,
                    action = {
                        ClickableText(
                            text = AnnotatedString(snackbarHostState.currentSnackbarData?.actionLabel ?: "CLOSE"),
                            onClick = {
                                snackbarHostState.currentSnackbarData?.dismiss()
                            },
                            style = TextStyle(
                                color = targetColor,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                ) {
                    Text(text = snackbarHostState.currentSnackbarData?.message ?: "", color = textColor)
                }
            }
        )
    }
}