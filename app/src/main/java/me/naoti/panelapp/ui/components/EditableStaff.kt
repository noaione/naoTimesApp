package me.naoti.panelapp.ui.components

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch
import me.naoti.panelapp.network.models.AssignmentKeyValueProject
import me.naoti.panelapp.network.models.ProjectAdjustStaffModel
import me.naoti.panelapp.network.models.ProjectUpdateContentStaff
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.theme.*
import me.naoti.panelapp.utils.getLogger

@Composable
fun EditableStaff(role: StatusRole, staff: AssignmentKeyValueProject, projectId: String, appCtx: AppState) {
    var editableState by remember { mutableStateOf(false) }
    var submittingState by remember { mutableStateOf(false) }
    val mutableIdEdit = remember {
        mutableStateOf(TextFieldValue(staff.id ?: ""))
    }
    var stateEnabled by remember {
        mutableStateOf(true)
    }
    val infiniteTransition = rememberInfiniteTransition()
    val alphaAnimation by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = .5f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        )
    )
    var oldState by remember { mutableStateOf(staff.id ?: "") }
    var mutableName by remember { mutableStateOf(staff.name ?: "Unknown") }
    val colors = getStatusColor(role)
    val ROUND = RoundedCornerShape(4.dp)
    val log = getLogger("EditableStaff[${role.name}]")
    val isDark = appCtx.isDarkMode()

    Column {
        Text(
            text = role.getFull().uppercase(),
            style = TextStyle(
                fontWeight = FontWeight.Light,
                fontSize = 10.sp,
                letterSpacing = .5.sp,
                color = if (isDark) Gray100 else Gray900
            ),
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 0.dp,
            )
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .height(40.dp)
                .clip(ROUND),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!editableState) {
                Icon(
                    Icons.Filled.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            bottom = 4.dp,
                            start = 8.dp,
                            end = 2.dp,
                        )
                        .clickable {
                            editableState = true
                        }
                        .clip(ROUND),
                    tint = if (isDark) Gray200 else Gray800
                )
                Column(
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            bottom = 4.dp,
                            start = 2.dp,
                            end = 10.dp,
                        )
                        .border(2.dp, colors.border)
                        .background(colors.bg)
                        .fillMaxWidth()
                        .clip(ROUND),
                ) {
                    Text(
                        text = mutableName,
                        style = TextStyle(
                            color = colors.text,
                        ),
                        modifier = Modifier
                            .padding(
                                vertical = 4.dp,
                                horizontal = 8.dp,
                            )
                            .clip(ROUND)
                            .wrapContentWidth(Alignment.Start)
                    )
                }

            } else {
                Icon(
                    Icons.Filled.Done,
                    contentDescription = "Done",
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            bottom = 4.dp,
                            start = 8.dp,
                            end = 2.dp,
                        )
                        .clickable {
                            if (stateEnabled) {
                                if (mutableIdEdit.value.text == oldState) {
                                    editableState = false
                                } else {
                                    // change something
                                    log.i("Changing to ${mutableIdEdit.value}")
                                    stateEnabled = false
                                    submittingState = true
                                    appCtx.coroutineScope.launch {
                                        val apiRes = appCtx.apiState.updateProjectStaff(
                                            ProjectAdjustStaffModel(
                                                ProjectUpdateContentStaff(
                                                    role.name,
                                                    projectId,
                                                    mutableIdEdit.value.text,
                                                )
                                            )
                                        )
                                        when (apiRes) {
                                            is NetworkResponse.Success -> {
                                                if (apiRes.body.success) {
                                                    mutableName = apiRes.body.name ?: "Unknown"
                                                    oldState = apiRes.body.id ?: ""
                                                    mutableIdEdit.value = TextFieldValue(
                                                        apiRes.body.id ?: ""
                                                    )
                                                } else {
                                                    mutableIdEdit.value = TextFieldValue(
                                                        oldState
                                                    )
                                                }
                                            }
                                            is NetworkResponse.NetworkError -> {
                                                Toast.makeText(
                                                    appCtx.contextState,
                                                    "Failed to update role ${role.getFull()}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                apiRes.error.let { log.e(it.stackTraceToString()) }
                                                mutableIdEdit.value = TextFieldValue(
                                                    oldState
                                                )
                                            }
                                            is NetworkResponse.Error -> {
                                                Toast.makeText(
                                                    appCtx.contextState,
                                                    "An unknown error has occurred!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                apiRes.error?.let { log.e(it.stackTraceToString()) }
                                                mutableIdEdit.value = TextFieldValue(
                                                    oldState
                                                )
                                            }
                                        }
                                        submittingState = false
                                        stateEnabled = true
                                        editableState = false
                                    }
                                }
                            }
                        }
                        .clip(ROUND),
                    tint = (if (isDark) Gray200 else Gray800).copy(
                        alpha = if (!stateEnabled) .75f else 1f
                    )
                )
                BasicTextField(
                    value = mutableIdEdit.value,
                    enabled = stateEnabled,
                    onValueChange = {
                        // dont edit if state is disabled
                        if (stateEnabled) {
                            mutableIdEdit.value = it
                        }
                    },
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            bottom = 4.dp,
                            start = 2.dp,
                            end = 10.dp,
                        )
                        .background(
                            (if (isDark) Gray600 else Gray200).copy(
                                alpha = if (submittingState) alphaAnimation else 1f
                            ),
                            MaterialTheme.shapes.small,
                        )
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(ROUND),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colors.primary),
                    textStyle = LocalTextStyle.current.copy(
                        color = (if (isDark) Gray200 else Gray800).copy(
                            alpha = if (submittingState) alphaAnimation else 1f
                        ),
                        fontSize = 12.sp,
                    ),
                    decorationBox = { innerTextField ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                horizontal = 6.dp,
                                vertical = 4.dp,
                            ),
                        ) {
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}
