package me.naoti.panelapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.naoti.panelapp.builder.CoilImage
import me.naoti.panelapp.network.models.ProjectInfoModel
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.theme.*
import me.naoti.panelapp.utils.getLogger

@Composable
fun ProjectCardInfo(project: ProjectInfoModel, appState: AppState) {
    val log = getLogger("ProjectCardInfo")
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = 5.dp,
    ) {
        Column {
            CoilImage(
                url = project.poster.url,
                contentDescription = "Poster",
                context = appState.contextState,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                project.title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (appState.isDarkMode()) Gray100 else Gray900
                ),
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 6.dp,
                ),
            )
            Spacer(modifier = Modifier.height(6.dp))
            Button(
                onClick = {
                    log.i("Deleting project...")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(
                            horizontal = 4.dp,
                            vertical = 2.dp,
                        )
                        .clip(RoundedCornerShape(10.dp))
                        .background(Red600)
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.padding(
                            horizontal = 4.dp,
                            vertical = 2.dp,
                        ),
                        tint = White,
                    )
                    Text(
                        text = "Delete Project",
                        fontWeight = FontWeight.SemiBold,
                        color = White
                    )
                }
            }
        }
    }
}