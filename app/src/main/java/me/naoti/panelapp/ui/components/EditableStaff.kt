package me.naoti.panelapp.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
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
import androidx.compose.ui.text.input.KeyboardType
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
    val mutableIdEdit = remember {
        mutableStateOf(TextFieldValue(staff.id ?: ""))
    }
    var stateEnabled by remember {
        mutableStateOf(true)
    }
    var oldState by remember { mutableStateOf(staff.id ?: "") }
    var mutableName by remember { mutableStateOf(staff.name ?: "Unknown") }
    val colors = getStatusColor(role)
    val Round = RoundedCornerShape(4.dp)
    val log = getLogger("EditableStaff[${role.name}]")

    Column {
        Text(
            text = role.getFull().uppercase(),
            style = TextStyle(
                fontWeight = FontWeight.Light,
                fontSize = 10.sp,
                letterSpacing = .5.sp,
                color = MaterialTheme.colorScheme.secondary.darker(.1f)
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
                .clip(Round),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!editableState) {
                IconButton(
                    onClick = {
                        editableState = true
                    },
                    modifier = Modifier.padding(
                        top = 4.dp,
                        bottom = 4.dp,
                        start = 8.dp,
                        end = 8.dp,
                    ).size(24.dp)
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit",
                    )
                }
                Column(
                    modifier = Modifier
                        .padding(
                            top = 4.dp,
                            bottom = 4.dp,
                            start = 2.dp,
                            end = 2.dp,
                        )
                        .border(2.dp, colors.border)
                        .background(colors.bg)
                        .fillMaxWidth()
                        .clip(Round),
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
                            .clip(Round)
                            .wrapContentWidth(Alignment.Start)
                    )
                }

            } else {
                IconButton(
                    modifier = Modifier.padding(
                        top = 4.dp,
                        bottom = 4.dp,
                        start = 8.dp,
                        end = 8.dp,
                    ).size(24.dp),
                    enabled = stateEnabled,
                    onClick = {
                        if (stateEnabled) {
                            if (mutableIdEdit.value.text == oldState) {
                                editableState = false
                            } else {
                                // change something
                                log.i("Changing to ${mutableIdEdit.value}")
                                stateEnabled = false
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
                                            Toast
                                                .makeText(
                                                    appCtx.contextState,
                                                    "Failed to update role ${role.getFull()}",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                            apiRes.error.let { log.e(it.stackTraceToString()) }
                                            mutableIdEdit.value = TextFieldValue(
                                                oldState
                                            )
                                        }
                                        is NetworkResponse.Error -> {
                                            Toast
                                                .makeText(
                                                    appCtx.contextState,
                                                    "An unknown error has occurred!",
                                                    Toast.LENGTH_SHORT
                                                )
                                                .show()
                                            apiRes.error?.let { log.e(it.stackTraceToString()) }
                                            mutableIdEdit.value = TextFieldValue(
                                                oldState
                                            )
                                        }
                                    }
                                    stateEnabled = true
                                    editableState = false
                                }
                            }
                        }
                    }
                ) {
                    Icon(
                        Icons.Filled.Done,
                        contentDescription = "Done",
                    )
                }

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
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.shapes.small,
                        )
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clip(Round),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    textStyle = LocalTextStyle.current.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
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
