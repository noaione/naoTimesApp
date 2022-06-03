package me.naoti.panelapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.flowlayout.FlowRow
import me.naoti.panelapp.builder.CoilImage
import me.naoti.panelapp.network.models.*
import me.naoti.panelapp.state.AppContextState
import me.naoti.panelapp.state.rememberAppContextState
import me.naoti.panelapp.ui.theme.Gray100
import me.naoti.panelapp.ui.theme.Gray300
import me.naoti.panelapp.ui.theme.Gray700
import me.naoti.panelapp.ui.theme.Gray900
import me.naoti.panelapp.utils.getLogger

fun waitForRelease(progress: StatusTickProject): Boolean {
    return progress.translated && progress.translateChecked && progress.encoded && progress.edited && progress.timed && progress.typeset && progress.qualityChecked
}

@Composable
fun DashboardProjectCard(project: Project, appCtx: AppContextState = rememberAppContextState()) {
    val log = getLogger("DashboardProjectCardView")
    val allDone = waitForRelease(project.status.progress)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = 5.dp
    ) {
        Column {
            CoilImage(
                url = project.poster,
                contentDescription = "Project Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentScale = ContentScale.Crop,
                context = appCtx.contextState
            )
            Spacer(modifier = Modifier.height(6.dp))
            ClickableText(
                text = AnnotatedString(project.title),
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (appCtx.isDarkMode()) Gray100 else Gray900,
                ),
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 6.dp,
                ),
                onClick = {
                    // navigate to project view
                    log.i("Navigating to resource project...")
                    appCtx.navController.navigate("app_project_screen/${project.id}")
                }
            )
            Spacer(modifier = Modifier.height(6.dp))
            var theText = "Episode ${project.status.episode}"
            if (allDone) {
                theText += " are"
            } else {
                theText += " needs"
            }
            // text-gray-700 dark:text-gray-300 text-base mb-0.5
            Text(
                theText,
                modifier = Modifier.padding(
                    bottom = 2.dp,
                    start = 10.dp,
                    end = 10.dp,
                ),
                style = TextStyle(
                    color = if (appCtx.isDarkMode()) Gray300 else Gray700
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            FlowRow(
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 4.dp,
                )
            ) {
                val stats = project.status.progress
                if (allDone) {
                    Text(
                        text = "Waiting for release...",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium
                        )
                    )
                } else {
                    if (!stats.translated) {
                        StatusBox(type = StatusRole.TL, 2.dp, 4.dp)
                    }
                    if (!stats.translateChecked) {
                        StatusBox(type = StatusRole.TLC, 2.dp, 4.dp)
                    }
                    if (!stats.encoded) {
                        StatusBox(type = StatusRole.ENC, 2.dp, 4.dp)
                    }
                    if (!stats.edited) {
                        StatusBox(type = StatusRole.ED, 2.dp, 4.dp)
                    }
                    if (!stats.timed) {
                        StatusBox(type = StatusRole.TM, 2.dp, 4.dp)
                    }
                    if (!stats.typeset) {
                        StatusBox(type = StatusRole.TS, 2.dp, 4.dp)
                    }
                    if (!stats.qualityChecked) {
                        StatusBox(type = StatusRole.QC, 2.dp, 4.dp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProjectCardPreview() {
    val sampleKeyVal = AssignmentKeyValueProject(
        "466469077444067372",
        "N4O"
    )
    val project = Project(
        "105914",
        "Sewayaki Kitsune no Senko-san",
        "https://s4.anilist.co/file/anilistcdn/media/anime/cover/medium/bx105914-VXKB0ZA2aVZF.png",
        1554854400,
        StatusProject(
            1555507800,
            2,
            false,
            StatusTickProject()
        ),
        AssignmentProject(
            sampleKeyVal,
            sampleKeyVal,
            sampleKeyVal,
            sampleKeyVal,
            sampleKeyVal,
            sampleKeyVal,
            sampleKeyVal
        )
    )
    DashboardProjectCard(project = project)
}