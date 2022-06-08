package me.naoti.panelapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import me.naoti.panelapp.builder.CoilImage
import me.naoti.panelapp.network.models.ProjectListModel
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.ui.theme.*
import me.naoti.panelapp.utils.getLogger

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectCard(project: ProjectListModel, appCtx: AppState) {
    val log = getLogger("DashboardProjectCardView")
    val isDone = project.isDone
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp,
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
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
                ),
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 6.dp,
                ),
                onClick = {
                    // navigate to project view
                    log.i("Navigating to resource project...")
                    val navRoute = ScreenItem.ProjectScreen.route.replace("{projectId}", project.id)
                    appCtx.navController.navigate(navRoute)
                }
            )
            Spacer(modifier = Modifier.height(2.dp))
            // text-gray-700 dark:text-gray-300 text-base mb-0.5
            Text(
                if (isDone) "Finished" else "Not finished",
                modifier = Modifier.padding(
                    bottom = 2.dp,
                    start = 10.dp,
                    end = 10.dp,
                ),
                style = TextStyle(
                    color = if (isDone) Green600 else Red600,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            FlowRow(
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 4.dp,
                )
            ) {
                val assignment = project.assignments
                AssignmentBox(type = StatusRole.TL, userName = assignment.translator.name, 2.dp, 4.dp)
                AssignmentBox(type = StatusRole.TLC, userName = assignment.translateChecker.name, 2.dp, 4.dp)
                AssignmentBox(type = StatusRole.ENC, userName = assignment.encoder.name, 2.dp, 4.dp)
                AssignmentBox(type = StatusRole.ED, userName = assignment.editor.name, 2.dp, 4.dp)
                AssignmentBox(type = StatusRole.TM, userName = assignment.timer.name, 2.dp, 4.dp)
                AssignmentBox(type = StatusRole.TS, userName = assignment.typesetter.name, 2.dp, 4.dp)
                AssignmentBox(type = StatusRole.QC, userName = assignment.qualityChecker.name, 2.dp, 4.dp)
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}