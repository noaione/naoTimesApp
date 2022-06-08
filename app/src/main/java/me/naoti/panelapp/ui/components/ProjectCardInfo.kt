package me.naoti.panelapp.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.naoti.panelapp.builder.CoilImage
import me.naoti.panelapp.network.models.ProjectInfoModel
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.theme.*
import me.naoti.panelapp.utils.getLogger

@Composable
fun DeleteButton(projectId: String) {
    val log = getLogger("DeleteButton[$projectId]")
    Button(
        onClick = {
            log.i("Deleting project...")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(5.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = Red600,
            contentColor = White
        )
    ) {
        Icon(
            Icons.Outlined.Delete,
            contentDescription = "Delete",
            modifier = Modifier
                .padding(
                    horizontal = 4.dp,
                    vertical = 2.dp,
                )
                .align(Alignment.CenterVertically),
            tint = White,
        )
        Text(
            text = "Delete Project",
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.CenterVertically),
            color = White,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DeleteButtonPreview() {
    DeleteButton(projectId = "123")
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DeleteButtonDarkPreview() {
    DeleteButton(projectId = "123")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectCardInfo(project: ProjectInfoModel, appState: AppState) {
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
                url = project.poster.url,
                contentDescription = "Poster",
                context = appState.contextState,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .padding(10.dp)
                    .clip(RoundedCornerShape(5.dp))
            )
            
            Spacer(modifier = Modifier.height(2.dp))

            Text(
                project.title,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(
                    horizontal = 10.dp,
                    vertical = 6.dp,
                ),
            )
            Spacer(modifier = Modifier.height(6.dp))

            EditableStaff(
                role = StatusRole.TL,
                staff = project.assignments.translator,
                projectId = project.id,
                appCtx = appState
            )
            Spacer(modifier = Modifier.height(1.dp))
            EditableStaff(
                role = StatusRole.TLC,
                staff = project.assignments.translateChecker,
                projectId = project.id,
                appCtx = appState
            )
            Spacer(modifier = Modifier.height(1.dp))
            EditableStaff(
                role = StatusRole.ENC,
                staff = project.assignments.encoder,
                projectId = project.id,
                appCtx = appState
            )
            Spacer(modifier = Modifier.height(1.dp))
            EditableStaff(
                role = StatusRole.ED,
                staff = project.assignments.editor,
                projectId = project.id,
                appCtx = appState
            )
            Spacer(modifier = Modifier.height(1.dp))
            EditableStaff(
                role = StatusRole.TM,
                staff = project.assignments.timer,
                projectId = project.id,
                appCtx = appState
            )
            Spacer(modifier = Modifier.height(1.dp))
            EditableStaff(
                role = StatusRole.TS,
                staff = project.assignments.typesetter,
                projectId = project.id,
                appCtx = appState
            )
            Spacer(modifier = Modifier.height(1.dp))
            EditableStaff(
                role = StatusRole.QC,
                staff = project.assignments.qualityChecker,
                projectId = project.id,
                appCtx = appState
            )

            Spacer(modifier = Modifier.height(6.dp))

            DeleteButton(project.id)
        }
    }
}