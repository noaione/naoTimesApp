package me.naoti.panelapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import me.naoti.panelapp.state.AppState

@Composable
fun ProjectsScreen(appState: AppState) {
    Column(modifier = Modifier
        .fillMaxSize()
        .wrapContentSize(Alignment.Center)) {
        Text(
            text = "Projects View",
            fontWeight = FontWeight.Bold,
            color = Color.Blue,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}