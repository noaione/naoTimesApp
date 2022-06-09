package me.naoti.panelapp.ui.components

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.naoti.panelapp.builder.CoilImage
import me.naoti.panelapp.navigation.NavigationItem
import me.naoti.panelapp.network.ErrorCode
import me.naoti.panelapp.network.models.ProjectInfoModel
import me.naoti.panelapp.network.models.ProjectRemoveModel
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.popUpToTop
import me.naoti.panelapp.ui.theme.*
import me.naoti.panelapp.utils.getLogger
import me.naoti.panelapp.utils.pickWords

@Composable
fun DeleteButton(
    onClick: (() -> Unit)? = null
) {
    Button(
        onClick = {
            if (onClick != null) {
                onClick()
            }
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
    DeleteButton()
}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DeleteButtonDarkPreview() {
    DeleteButton()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectCardInfo(project: ProjectInfoModel, appState: AppState, sourcePage: String) {
    var openDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    val log = getLogger("ProjectCardInfo")
    var verificationWord by remember {
        mutableStateOf(pickWords().joinToString("-"))
    }
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

            DeleteButton {
                verificationWord = pickWords().joinToString("-")
                openDialog = true
            }
        }
    }

    if (openDialog) {
        DeleteDialog(
            enabled = !isDeleting,
            onConfirm = {
                isDeleting = true
                appState.coroutineScope.launch {
                    when (val result = appState.apiState.removeProject(ProjectRemoveModel.fromProject(project))) {
                        is NetworkResponse.Success -> {
                            if (result.body.success) {
                                log.i("Success in deleting project, sending back to dashboard project")
                                if (sourcePage == "projects") {
                                    appState.navAppController.navigate(
                                        NavigationItem.Projects.route.replace("{refresh}", "true")
                                    ) {
                                        // remove all stack
                                        popUpToTop(appState.navAppController)
                                    }
                                } else {
                                    appState.navAppController.navigate(
                                        NavigationItem.Dashboard.route.replace("{refresh}", "true")
                                    ) {
                                        // remove all stack
                                        popUpToTop(appState.navAppController)
                                    }
                                }
                            } else {
                                val errMsg = when (val code = result.body.code) {
                                    ErrorCode.ProjectNotFound -> code.asText(project.title)
                                    else -> code?.asText() ?: ErrorCode.UnknownError.asText()
                                }
                                Toast.makeText(
                                    appState.contextState,
                                    errMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is NetworkResponse.Error -> {
                            // error handle
                            val body = result.body
                            var errMsg = result.error.toString()
                            if (body != null) {
                                errMsg = when (val code = body.code) {
                                    ErrorCode.ProjectNotFound -> code.asText(project.title)
                                    else -> code?.asText() ?: ErrorCode.UnknownError.asText()
                                }
                            }
                            Toast.makeText(
                                appState.contextState,
                                errMsg,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    isDeleting = false
                    openDialog = false
                }
            },
            onDismiss = {
                if (!isDeleting) {
                    openDialog = false
                }
            },
            verificationWord = verificationWord
        )
    }
}