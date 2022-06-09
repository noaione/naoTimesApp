package me.naoti.panelapp.ui.components

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.flowlayout.FlowRow
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch
import me.naoti.panelapp.builder.TimeString
import me.naoti.panelapp.network.ErrorCode
import me.naoti.panelapp.network.models.*
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.state.rememberAppContextState
import me.naoti.panelapp.ui.theme.*
import me.naoti.panelapp.utils.getLogger

data class EpisodeChecked(
    var tl: Boolean = false,
    var tlc: Boolean = false,
    var enc: Boolean = false,
    var ed: Boolean = false,
    var tm: Boolean = false,
    var ts: Boolean = false,
    var qc: Boolean = false,
) {
    companion object {
        fun fromStatus(status: StatusTickProject): EpisodeChecked {
            val current = EpisodeChecked()
            if (status.translated) current.tl = true
            if (status.translateChecked) current.tlc = true
            if (status.encoded) current.enc = true
            if (status.edited) current.ed = true
            if (status.timed) current.tm = true
            if (status.typeset) current.ts = true
            if (status.qualityChecked) current.qc = true
            return current
        }
    }

    fun toStatus(): StatusTickProject {
        return StatusTickProject(
            translated = tl,
            translateChecked = tlc,
            encoded = enc,
            edited = ed,
            timed = tm,
            typeset = ts,
            qualityChecked = qc,
        )
    }
}

fun anyProgress(progress: StatusTickProject): Boolean {
    return progress.translated || progress.translateChecked || progress.encoded || progress.edited || progress.timed || progress.typeset || progress.qualityChecked
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckboxStatus(
    role: StatusRole,
    state: Boolean,
    onChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val statusCol = getStatusColor(role).checkbox
    val stateCheck = remember { mutableStateOf(state) }
    val isDark = rememberAppContextState().isDarkMode()
    val disabledColor = statusCol.copy(alpha = .75f)
    val uncheckedColor = if (isDark) White else Gray200
    val disabledUncheked = uncheckedColor.copy(alpha = .75f)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = stateCheck.value,
            onCheckedChange = {
                if (enabled) {
                    stateCheck.value = it
                    onChanged(it)
                }
            },
            enabled = enabled,
            colors = CheckboxDefaults.colors(
                checkedColor = statusCol,
                uncheckedColor = uncheckedColor,
                checkmarkColor = Color.White,
                disabledCheckedColor = disabledColor,
                disabledUncheckedColor = disabledUncheked,
            ),
            modifier = Modifier
                .padding(
                    top = 3.dp,
                    bottom = 3.dp,
                    end = 6.dp
                )
                .size(24.dp)
        )
//        Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = role.getShort(),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun EpisodeCardView(
    status: StatusProject,
    dialogEdit: Boolean,
    checkedState: EpisodeChecked,
    isSubmitting: Boolean
) {
    val log = getLogger("EpisodeCardViewActual")
    val allDone = waitForRelease(checkedState.toStatus())
    val hasProgress = anyProgress(checkedState.toStatus())

    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp),
        ) {
            Text("📺", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("Released?", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(2.dp))
            if (status.isDone) {
                Icon(Icons.Filled.Done, contentDescription = "Done", tint = Green600)
            } else {
                Icon(Icons.Filled.Close, contentDescription = "Not Done", tint = Red600)
            }
        }
        Row(
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(horizontal = 4.dp),
        ) {
            Text("⌚", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("Airing at:", fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(TimeString.fromUnix(status.airtime).toString())
        }

        if (!dialogEdit) {
            if (!allDone) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp),
                ) {
                    Text("⏰", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("On Process", fontWeight = FontWeight.SemiBold)
                }
                FlowRow(
                    modifier = Modifier.padding(
                        horizontal = 4.dp,
                        vertical = 4.dp,
                    )
                ) {
                    if (!checkedState.tl) {
                        StatusBox(type = StatusRole.TL, 2.dp, 4.dp)
                    }
                    if (!checkedState.tlc) {
                        StatusBox(type = StatusRole.TLC, 2.dp, 4.dp)
                    }
                    if (!checkedState.enc) {
                        StatusBox(type = StatusRole.ENC, 2.dp, 4.dp)
                    }
                    if (!checkedState.ed) {
                        StatusBox(type = StatusRole.ED, 2.dp, 4.dp)
                    }
                    if (!checkedState.tm) {
                        StatusBox(type = StatusRole.TM, 2.dp, 4.dp)
                    }
                    if (!checkedState.ts) {
                        StatusBox(type = StatusRole.TS, 2.dp, 4.dp)
                    }
                    if (!checkedState.qc) {
                        StatusBox(type = StatusRole.QC, 2.dp, 4.dp)
                    }
                }
            }
            if (hasProgress) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp),
                ) {
                    Text("✔️", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Finished", fontWeight = FontWeight.SemiBold)
                }
                FlowRow(
                    modifier = Modifier.padding(
                        horizontal = 4.dp,
                        vertical = 4.dp,
                    )
                ) {
                    if (checkedState.tl) {
                        StatusBox(type = StatusRole.TL, 2.dp, 4.dp)
                    }
                    if (checkedState.tlc) {
                        StatusBox(type = StatusRole.TLC, 2.dp, 4.dp)
                    }
                    if (checkedState.enc) {
                        StatusBox(type = StatusRole.ENC, 2.dp, 4.dp)
                    }
                    if (checkedState.ed) {
                        StatusBox(type = StatusRole.ED, 2.dp, 4.dp)
                    }
                    if (checkedState.tm) {
                        StatusBox(type = StatusRole.TM, 2.dp, 4.dp)
                    }
                    if (checkedState.ts) {
                        StatusBox(type = StatusRole.TS, 2.dp, 4.dp)
                    }
                    if (checkedState.qc) {
                        StatusBox(type = StatusRole.QC, 2.dp, 4.dp)
                    }
                }
            }
        } else {
            val globMod = Modifier.padding(horizontal = 2.dp, vertical = 2.dp)
            CheckboxStatus(
                StatusRole.TL,
                state = checkedState.tl,
                onChanged = {
                    checkedState.tl = it
                    log.i("Changed TL: ${checkedState.toString()}")
                },
                modifier = globMod,
                enabled = !isSubmitting,
            )
            CheckboxStatus(
                StatusRole.TLC,
                state = checkedState.tlc,
                onChanged = {
                    checkedState.tlc = it
                    log.i("Changed TLC: ${checkedState.toString()}")
                },
                modifier = globMod,
                enabled = !isSubmitting,
            )
            CheckboxStatus(
                StatusRole.ENC,
                state = checkedState.enc,
                onChanged = {
                    checkedState.enc = it
                    log.i("Changed ENC: ${checkedState.toString()}")
                },
                modifier = globMod,
                enabled = !isSubmitting,
            )
            CheckboxStatus(
                StatusRole.ED,
                state = checkedState.ed,
                onChanged = {
                    checkedState.ed = it
                    log.i("Changed ED: ${checkedState.toString()}")
                },
                modifier = globMod,
                enabled = !isSubmitting,
            )
            CheckboxStatus(
                StatusRole.TM,
                state = checkedState.tm,
                onChanged = {
                    checkedState.tm = it
                    log.i("Changed TM: ${checkedState.toString()}")
                },
                modifier = globMod,
                enabled = !isSubmitting,
            )
            CheckboxStatus(
                StatusRole.TS,
                state = checkedState.ts,
                onChanged = {
                    checkedState.ts = it
                    log.i("Changed TS: ${checkedState.toString()}")
                },
                modifier = globMod,
                enabled = !isSubmitting,
            )
            CheckboxStatus(
                StatusRole.QC,
                state = checkedState.qc,
                onChanged = {
                    checkedState.qc = it
                    log.i("Changed QC: ${checkedState.toString()}")
                },
                modifier = globMod,
                enabled = !isSubmitting,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeCard(
    projectId: String,
    status: StatusProject,
    appState: AppState,
    onStateEdited: (StatusProject) -> Unit,
    onRemove: (StatusProject) -> Unit
) {
    val log = getLogger("EpisodeCard")
    var isSubmitting by remember {
        mutableStateOf(false)
    }
    var dialogDelete by remember {
        mutableStateOf(false)
    }
    var dialogEdit by remember {
        mutableStateOf(false)
    }
    var checkedState by remember {
        mutableStateOf(EpisodeChecked.fromStatus(status.progress))
    }
    var isReleased by remember {
        mutableStateOf(status.isDone)
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
            if (isSubmitting) {
                LinearProgressIndicator(modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth())
            } else {
                Spacer(modifier = Modifier.height(2.dp))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Episode ${status.episode}",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                    ),
                    modifier = Modifier.padding(
                        horizontal = 4.dp,
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    IconButton(
                        onClick = {
                            if (!isSubmitting) {
                                if (!dialogEdit) {
                                    log.i("Moving to edit state")
                                    dialogEdit = true
                                } else {
                                    // submit first pogU
                                    if (!status.progress.isSame(checkedState.toStatus())) {
                                        appState.coroutineScope.launch {
                                            isSubmitting = true
                                            log.i("Trying to submit new roles information...")
                                            val roleUpdate = mutableListOf<ProjectUpdateContentStatusRoleTick>()
                                            roleUpdate.add(
                                                ProjectUpdateContentStatusRoleTick(
                                                    role = "TL",
                                                    isDone = checkedState.tl
                                                )
                                            )
                                            roleUpdate.add(
                                                ProjectUpdateContentStatusRoleTick(
                                                    role = "TLC",
                                                    isDone = checkedState.tlc
                                                )
                                            )
                                            roleUpdate.add(
                                                ProjectUpdateContentStatusRoleTick(
                                                    role = "ENC",
                                                    isDone = checkedState.enc
                                                )
                                            )
                                            roleUpdate.add(
                                                ProjectUpdateContentStatusRoleTick(
                                                    role = "ED",
                                                    isDone = checkedState.ed
                                                )
                                            )
                                            roleUpdate.add(
                                                ProjectUpdateContentStatusRoleTick(
                                                    role = "TM",
                                                    isDone = checkedState.tm
                                                )
                                            )
                                            roleUpdate.add(
                                                ProjectUpdateContentStatusRoleTick(
                                                    role = "TS",
                                                    isDone = checkedState.ts
                                                )
                                            )
                                            roleUpdate.add(
                                                ProjectUpdateContentStatusRoleTick(
                                                    role = "QC",
                                                    isDone = checkedState.qc
                                                )
                                            )
                                            val result = appState.apiState.updateProjectStatus(
                                                ProjectAdjustStatusModel(
                                                    changes = ProjectUpdateContentStatus(
                                                        episode = status.episode,
                                                        projectId = projectId,
                                                        roles = roleUpdate,
                                                    )
                                                )
                                            )
                                            when (result) {
                                                is NetworkResponse.Success -> {
                                                    if (result.body.results != null) {

                                                        status.progress = result.body.results!!.progress
                                                        checkedState =
                                                            EpisodeChecked.fromStatus(result.body.results!!.progress)
                                                        log.i("new checked state: $checkedState")
                                                        onStateEdited(status)
                                                    } else {
                                                        log.e("Got 200 code, but results data is empty!")
                                                        Toast.makeText(
                                                            appState.contextState,
                                                            "Failed to update status!",
                                                            Toast.LENGTH_SHORT,
                                                        ).show()
                                                    }
                                                }
                                                is NetworkResponse.Error -> {
                                                    result.error?.let { log.e(it.stackTraceToString()) }
                                                    Toast.makeText(
                                                        appState.contextState,
                                                        "Failed to update status!",
                                                        Toast.LENGTH_SHORT,
                                                    ).show()
                                                }
                                            }
                                            isSubmitting = false
                                            dialogEdit = false
                                        }
                                    } else {
                                        isSubmitting = false
                                        dialogEdit = false
                                    }
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        modifier = Modifier.size(24.dp),
                    ) {
                        Icon(
                            imageVector = if (dialogEdit) Icons.Filled.Done else Icons.Filled.Edit,
                            contentDescription = "Edit",
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = {
                            if (!isSubmitting) {
                                log.i("Showing alert dialog...")
                                dialogDelete = true
                            }
                        },
                        modifier = Modifier.size(24.dp),
                        enabled = !isSubmitting
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
            EpisodeCardView(status, dialogEdit, checkedState, isSubmitting = isSubmitting)
            Spacer(modifier = Modifier.height(2.dp))
            Button(
                enabled = !isSubmitting,
                onClick = {
                    // release
                    val toggleModel = ProjectAdjustReleaseModel(
                        projectId = projectId,
                        episode = status.episode,
                        isDone = !isReleased,
                    )
                    isSubmitting = true
                    appState.coroutineScope.launch {
                        when (val result = appState.apiState.updateReleaseStatus(toggleModel)) {
                            is NetworkResponse.Success -> {
                                if (result.body.success) {
                                    isReleased = !isReleased
                                    status.isDone = isReleased
                                } else {
                                    val body = result.body
                                    val theText = when (body.code) {
                                        ErrorCode.ProjectEpisodeNotFound -> {
                                            body.code.asText(status.episode.toString())
                                        }
                                        ErrorCode.ProjectNotFound -> {
                                            body.code.asText(projectId)
                                        }
                                        else -> {
                                            if (body.code != null) {
                                                body.code.asText()
                                            } else {
                                                ErrorCode.UnknownError.asText()
                                            }
                                        }
                                    }
                                    Toast.makeText(
                                        appState.contextState,
                                        theText,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            is NetworkResponse.Error -> {
                                val body = result.body
                                var theText = result.error.toString()
                                if (body != null) {
                                    theText = when (body.code) {
                                        ErrorCode.ProjectEpisodeNotFound -> {
                                            body.code.asText(status.episode.toString())
                                        }
                                        ErrorCode.ProjectNotFound -> {
                                            body.code.asText(projectId)
                                        }
                                        else -> {
                                            if (body.code != null) {
                                                body.code.asText()
                                            } else {
                                                ErrorCode.UnknownError.asText()
                                            }
                                        }
                                    }
                                }
                                Toast.makeText(
                                    appState.contextState,
                                    theText,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        isSubmitting = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(10.dp))
                    .padding(vertical = 4.dp, horizontal = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isReleased) Red600 else Green600,
                    contentColor = White,
                    disabledContainerColor = (if (isReleased) Red600 else Green600).darker(.3f),
                    disabledContentColor = White.darker(.3f)
                )
            ) {
                Text(text = if (isReleased) "Undo Release" else "Release")
            }
        }
    }

    if (dialogDelete) {
        DeleteDialog(
            extraText = "This will remove episode ${status.episode}",
            onDismiss = {
                if (!isSubmitting) {
                    dialogDelete = false
                }
            },
            onConfirm = {
                isSubmitting = true
                appState.coroutineScope.launch {
                    log.i("Removing epsideo from database...")
                    val removeThis = ProjectEpisodeRemoveModel.create(projectId, listOf(status.episode))
                    when (val result = appState.apiState.removeProjectEpisode(removeThis)) {
                        is NetworkResponse.Success -> {
                            if (result.body.success == true) {
                                log.i("Success, sending callback")
                                dialogDelete = false
                                isSubmitting = false
                                onRemove(status)
                            } else {
                                val errMsg = result.body.code?.asText() ?: ErrorCode.UnknownError.asText()
                                Toast.makeText(
                                    appState.contextState,
                                    errMsg,
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialogDelete = false
                                isSubmitting = false
                            }
                        }
                        is NetworkResponse.Error -> {
                            result.error?.let { log.e(it.stackTraceToString()) }
                            val body = result.body
                            var errMsg = result.error.toString()
                            if (body != null) {
                                errMsg = body.code?.asText() ?: ErrorCode.UnknownError.asText()
                            }
                            Toast.makeText(
                                appState.contextState,
                                errMsg,
                                Toast.LENGTH_SHORT
                            ).show()
                            dialogDelete = false
                            isSubmitting = false
                        }
                    }
                }
            },
            enabled = !isSubmitting
        )
    }
}
