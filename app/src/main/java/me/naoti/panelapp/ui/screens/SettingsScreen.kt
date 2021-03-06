package me.naoti.panelapp.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.naoti.panelapp.Constants
import me.naoti.panelapp.R
import me.naoti.panelapp.network.ApiRoutes
import me.naoti.panelapp.network.ErrorCode
import me.naoti.panelapp.network.models.*
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.ui.components.NetworkSearch
import me.naoti.panelapp.ui.popUpToTop
import me.naoti.panelapp.ui.preferences.DarkModeOverride
import me.naoti.panelapp.ui.preferences.UserSettings
import me.naoti.panelapp.ui.theme.NaoTimesTheme
import me.naoti.panelapp.ui.theme.darker
import me.naoti.panelapp.utils.getLogger
import me.naoti.panelapp.utils.hasUppercase

@Composable
fun LogoutButton(appState: AppState, modifier: Modifier = Modifier, dryRun: Boolean = false) {
    val log = getLogger("LogoutButton")
    Button(
        modifier = modifier
            .testTag("LogOutButton")
            .padding(
                horizontal = 20.dp,
                vertical = 5.dp
            )
            .fillMaxWidth(),
        onClick = {
            log.i("Logging out...")
            appState.coroutineScope.launch {
                // ignore everything
                if (!dryRun) {
                    log.i("Setting current user to null")
                    appState.setCurrentUser(null)
                    log.i("Sending logout request to API...")
                    appState.apiState.logoutUser()
                    // clear navigation back stack
                    log.i("Clearing user cookies...")
                    appState.clearUserCookie()
                }
                // enter login screen
                log.i("Navigating back to login screen")
                appState.navController.navigate(ScreenItem.LoginScreen.route) {
                    popUpToTop(appState.navController)
                }
            }
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
            disabledContainerColor = MaterialTheme.colorScheme.error.darker(0.2f),
            disabledContentColor = MaterialTheme.colorScheme.onError.darker(0.2f)
        )
    ) {
        Text(text = "Logout${if (dryRun) " (Dry)" else " "}")
    }
}

@Composable
fun BoxedSettings(
    onClick: (() -> Unit)? = null,
    marginTop: Dp = 10.dp,
    marginBottom: Dp = 10.dp,
    content: @Composable () -> Unit,
) {
    var baseModifier = Modifier.fillMaxWidth()
    if (onClick != null) {
        baseModifier = baseModifier.clickable {
            onClick()
        }
    }
    Box(
        modifier = baseModifier
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = marginTop,
                    top = marginBottom,
                )
                .fillMaxWidth()
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DarkModeToggle(
    current: DarkModeOverride,
    onChangeMode: ((DarkModeOverride) -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    var currentState by remember { mutableStateOf(current) }
    val radioOptions = listOf(
        DarkModeOverride.FollowSystem,
        DarkModeOverride.LightMode,
        DarkModeOverride.DarkMode
    )
    BoxedSettings(
        onClick = {
            showDialog = true
        }
    ) {
        Text(text = "Dark Mode", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.sp)
        Text(text = currentState.toName(), fontSize = 13.sp, letterSpacing = 0.sp)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            dismissButton = {
                Text(text = "Close")
            },
            confirmButton = {},
            title = {
                Text(text = "Dark Mode")
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup()
                ) {
                    radioOptions.forEach { opts ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .selectable(
                                    selected = opts == currentState,
                                    onClick = {
                                        currentState = opts
                                        if (onChangeMode != null) {
                                            onChangeMode(opts)
                                        }
                                        showDialog = false
                                    }
                                )
                                .clip(RoundedCornerShape(50)),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = opts == currentState,
                                onClick = null,
                                modifier = Modifier.padding(
                                    start = 4.dp,
                                )
                            )
                            Text(
                                text = opts.toName(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 16.dp, end = 4.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}

data class AnnouncerDelegatedResult(
    val content: ChannelFindResult,
) {
    override fun toString(): String {
        return content.asText()
    }
}

private fun getOrNotSet(name: String?): String {
    if (name == null) return "Not Set"
    if (name.isEmpty()) return "Not Set"
    return name
}

@Composable
fun AnnouncerSearch(
    appState: AppState,
    onItemSelect: (AnnouncerDelegatedResult) -> Unit,
    onCleared: () -> Unit,
    enabled: Boolean = true,
    isError: Boolean = false,
) {
    val log = getLogger("AnnouncerChangeSearch")
    val currentFindings = remember { mutableStateListOf<AnnouncerDelegatedResult>() }
    var isLoading by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = true) {
        appState.coroutineScope.launch {
            isLoading = true
            log.i("Searching to API...")
            when (val result = appState.apiState.getServerChannels()) {
                is NetworkResponse.Success -> {
                    if (result.body.success) {
                        log.i("Success, setting to currentFindings")
                        currentFindings.clear()
                        result.body.results.forEach { item ->
                            currentFindings.add(AnnouncerDelegatedResult(item))
                        }
                    } else {
                        log.e("Failed for some reason...?")
                        Toast.makeText(
                            appState.contextState,
                            "Unable to fetch server list!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                is NetworkResponse.Error -> {
                    var errMsg = result.error.toString()
                    if (result.body != null) {
                        errMsg = result.body?.code?.asText() ?: ErrorCode.UnknownError.asText()
                    }
                    result.error?.let { log.e(it.stackTraceToString()) }
                    Toast.makeText(
                        appState.contextState,
                        errMsg,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            isLoading = false
        }
    }
    NetworkSearch(
        items = currentFindings,
        itemContent = { item ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(
                    text = item.content.asText(),
                    modifier = Modifier
                        .padding(4.dp)
                        .wrapContentWidth(Alignment.Start)
                )
            }
        },
        isInitializing = isLoading,
        onItemSelected = onItemSelect,
        onFilterResult = { items, query ->
            if (query.isEmpty()) {
                log.w("Query empty, resetting to default items count: ${items.count()}")
                items
            } else {
                log.i("Filtering with: $query")
                items.filter { item ->
                    item.content.id.contains(query, ignoreCase = true) ||
                            item.content.name.contains(query, ignoreCase = true)
                }
            }
        },
        onCleared = onCleared,
        modifier = Modifier
            .fillMaxWidth()
            .padding(2.dp),
        enabled = enabled,
        isError = isError,
    )
}

@Composable
fun AnnouncerChangeModule(
    appState: AppState,
    user: UserInfoModel,
    onInfoUpdate: ((UserInfoModel) -> Unit)? = null
) {
    var userState by remember { mutableStateOf(user) }
    var isSubmitting by remember { mutableStateOf(false) }
    var selectedMatch by remember { mutableStateOf<ChannelFindResult?>(null) }
    val log = getLogger("AnnouncerChangeModule")

    BoxedSettings {
        Text(text = "Announcement Channel", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.sp)
        Text(text = getOrNotSet(userState.announceChannel), fontSize = 13.sp, letterSpacing = 0.sp)
        Spacer(modifier = Modifier.height(4.dp))
        AnnouncerSearch(
            appState = appState,
            onItemSelect = { item ->
                selectedMatch = item.content
            },
            onCleared = {
                selectedMatch = null
            },
            enabled = !isSubmitting
        )
        Button(
            modifier = Modifier
                .testTag("SettingsSetChannelBtn")
                .padding(
                    vertical = 2.dp
                )
                .fillMaxWidth(),
            enabled = !isSubmitting,
            onClick = {
                isSubmitting = true
                appState.coroutineScope.launch {
                    log.i("Changing channel to $selectedMatch")
                    val adjustAnnouncer = SettingsAdjustAnnouncer(
                        channelId = selectedMatch?.id ?: "",
                        toRemove = selectedMatch == null,
                    )
                    when (val result = appState.apiState.updateAnnoucer(adjustAnnouncer)) {
                        is NetworkResponse.Success -> {
                            if (result.body.success) {
                                log.i("Success, using new announcer state!")
                                val newUserState = userState.rebuild(
                                    announceChannel = selectedMatch?.id ?: ""
                                )
                                userState = newUserState
                                if (onInfoUpdate != null) {
                                    onInfoUpdate(newUserState)
                                }
                            } else {
                                val code = result.body.code ?: ErrorCode.UnknownError
                                log.e("Failed to change for some reason: ${code.asText()}")
                                Toast.makeText(
                                    appState.contextState,
                                    code.asText(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        is NetworkResponse.Error -> {
                            var errMsg = result.error.toString()
                            if (result.body != null) {
                                val code = result.body!!.code ?: ErrorCode.UnknownError
                                errMsg = code.asText()
                            }
                            log.e("Failed to change for some reason: $errMsg")
                            result.error?.let { log.e(it.stackTraceToString()) }
                            Toast.makeText(
                                appState.contextState,
                                errMsg,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    isSubmitting = false
                }
            }
        ) {
            Text(text = "Set Channel")
        }
    }
}

@Composable
fun NameChangeModule(
    context: Context,
    apiState: ApiRoutes,
    coroScope: CoroutineScope,
    user: UserInfoModel,
    onInfoUpdate: ((UserInfoModel) -> Unit)? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    var userState by remember { mutableStateOf(user) }
    var nameState by remember { mutableStateOf(TextFieldValue(user.name ?: "")) }
    var isSubmitting by remember { mutableStateOf(false) }
    val log = getLogger("SettingsNameChange")

    BoxedSettings(
        onClick = {
            showDialog = true
        }
    ) {
        Text(text = "Name", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.sp)
        Text(text = getOrNotSet(userState.name), fontSize = 13.sp, letterSpacing = 0.sp)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isSubmitting) {
                    showDialog = false
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isSubmitting = true
                        coroScope.launch {
                            log.i("Changing name to: ${nameState.text}")
                            when (val result = apiState.updateName(SettingsAdjustName(nameState.text))) {
                                is NetworkResponse.Success -> {
                                    if (result.body.success) {
                                        log.i("Success, using new name!")
                                        val newUserState = userState.rebuild(
                                            name = nameState.text.ifEmpty { null }
                                        )
                                        userState = newUserState
                                        if (onInfoUpdate != null) {
                                            onInfoUpdate(newUserState)
                                        }
                                        showDialog = false
                                    } else {
                                        val code = result.body.code ?: ErrorCode.UnknownError
                                        log.e("Failed to change for some reason: ${code.asText()}")
                                        Toast.makeText(
                                            context,
                                            code.asText(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                is NetworkResponse.Error -> {
                                    var errMsg = result.error.toString()
                                    if (result.body != null) {
                                        val code = result.body!!.code ?: ErrorCode.UnknownError
                                        errMsg = code.asText()
                                    }
                                    log.e("Failed to change for some reason: $errMsg")
                                    result.error?.let { log.e(it.stackTraceToString()) }
                                    Toast.makeText(
                                        context,
                                        errMsg,
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                            }
                            isSubmitting = false
                        }
                    },
                    enabled = !isSubmitting
                ) {
                    Text(text = "Change")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (!isSubmitting) {
                            showDialog = false
                        }
                    },
                    enabled = !isSubmitting
                ) {
                    Text(text = "Cancel")
                }
            },
            title = {
                Text(
                    text = "Name",
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Set a new name for your account!")
                    OutlinedTextField(
                        value = nameState,
                        onValueChange = {
                            nameState = it
                        },
                        label = { Text(text = "Enter Name") },
                        enabled = !isSubmitting,
                        singleLine = true,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .testTag("SettingsNameChangeInput"),
                        shape = RoundedCornerShape(6.dp),
                    )
                }
            }
        )
    }
}

enum class PasswordChangeError {
    MinimumCharacter {
        override fun asError(): String = "Minimum 8 characters"
    },
    CapitalCharacter {
        override fun asError(): String = "Must have one capital letter"
    };

    abstract fun asError(): String
}

private fun validatePassword(password: String): List<PasswordChangeError> {
    val errors = mutableListOf<PasswordChangeError>()
    if (password.length < 8) {
        errors.add(PasswordChangeError.MinimumCharacter)
    }
    if (!password.hasUppercase()) {
        errors.add(PasswordChangeError.CapitalCharacter)
    }
    return errors.toList()
}

@Composable
fun PasswordChangeModule(
    context: Context,
    apiState: ApiRoutes,
    coroScope: CoroutineScope,
    user: UserInfoModel,
    onSuccess: () -> Unit,
) {
    var showDialog by remember { mutableStateOf(false) }
    var oldPassState by remember { mutableStateOf(TextFieldValue("")) }
    var newPassState by remember { mutableStateOf(TextFieldValue("")) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showOldPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    val validationError = remember {
        mutableStateListOf(
            PasswordChangeError.MinimumCharacter,
            PasswordChangeError.CapitalCharacter,
        )
    }
    val log = getLogger("SettingsNameChange")

    BoxedSettings(
        onClick = {
            showDialog = true
        }
    ) {
        Text(text = "Password", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.sp)
        Text(text = "Set a new password", fontSize = 13.sp, letterSpacing = 0.sp)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isSubmitting) {
                    showDialog = false
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (validationError.isNotEmpty()) {
                            return@TextButton
                        }
                        isSubmitting = true
                        coroScope.launch {
                            log.i("Changing user ${user.id} password...")
                            val adjustPassword = SettingsAdjustPassword(
                                oldPassword = oldPassState.text,
                                newPassword = newPassState.text,
                            )
                            when (val result = apiState.updatePassword(adjustPassword)) {
                                is NetworkResponse.Success -> {
                                    if (result.body.success) {
                                        log.i("Success, using new name!")
                                        Toast.makeText(
                                            context,
                                            "Success, you need to login again...",
                                            Toast.LENGTH_SHORT,
                                        ).show()
                                        delay(1500L)
                                        onSuccess()
                                    } else {
                                        val code = result.body.code ?: ErrorCode.UnknownError
                                        log.e("Failed to change for some reason: ${code.asText()}")
                                        Toast.makeText(
                                            context,
                                            code.asText(),
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                is NetworkResponse.Error -> {
                                    var errMsg = result.error.toString()
                                    if (result.body != null) {
                                        val code = result.body!!.code ?: ErrorCode.UnknownError
                                        errMsg = code.asText()
                                    }
                                    log.e("Failed to change for some reason: $errMsg")
                                    result.error?.let { log.e(it.stackTraceToString()) }
                                    Toast.makeText(
                                        context,
                                        errMsg,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            isSubmitting = false
                        }
                    },
                    enabled = !isSubmitting && validationError.isEmpty()
                ) {
                    Text(text = "Change")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (!isSubmitting) {
                            showDialog = false
                        }
                    },
                    enabled = !isSubmitting
                ) {
                    Text(text = "Cancel")
                }
            },
            title = {
                Text(
                    text = "Password",
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                ) {
                    Text("Set a new password for your account!", textAlign = TextAlign.Center)
                    OutlinedTextField(
                        value = oldPassState,
                        onValueChange = {
                            oldPassState = it
                        },
                        label = { Text(text = "Old Password") },
                        enabled = !isSubmitting,
                        singleLine = true,
                        modifier = Modifier
                            .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                            .fillMaxWidth()
                            .testTag("SettingsPassChangeOldInput"),
                        shape = RoundedCornerShape(6.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (showOldPassword) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { showOldPassword = !showOldPassword }
                            ) {
                                if (showOldPassword) {
                                    Icon(painterResource(id = R.drawable.ic_icons_eye), contentDescription = "Hide Password")
                                } else {
                                    Icon(painterResource(id = R.drawable.ic_icons_eye_off), contentDescription = "Show Password")
                                }
                            }
                        }
                    )
                    OutlinedTextField(
                        value = newPassState,
                        onValueChange = {
                            newPassState = it
                            validationError.clear()
                            validatePassword(it.text).forEach { errVal ->
                                validationError.add(errVal)
                            }
                        },
                        label = { Text(text = "New Password") },
                        enabled = !isSubmitting,
                        singleLine = true,
                        modifier = Modifier
                            .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                            .testTag("SettingsPassChangeNewInput")
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(6.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (showNewPassword) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { showNewPassword = !showNewPassword }
                            ) {
                                if (showNewPassword) {
                                    Icon(painterResource(id = R.drawable.ic_icons_eye), contentDescription = "Hide Password")
                                } else {
                                    Icon(painterResource(id = R.drawable.ic_icons_eye_off), contentDescription = "Show Password")
                                }
                            }
                        }
                    )

                    if (validationError.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    validationError.forEach { errVal ->
                        Text(
                            text = errVal.asError(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 4.dp,
                                    end = 4.dp,
                                    top = 2.dp,
                                ),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DarkModeTogglePreview() {
    NaoTimesTheme {
        DarkModeToggle(current = DarkModeOverride.FollowSystem)
    }
}

@Composable
fun TextHead(text: String) {
    Text(
        text,
        style = TextStyle(
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 0.sp,
        ),
        modifier = Modifier.padding(
            start = 20.dp,
            end = 20.dp,
            top = 10.dp,
            bottom = 5.dp
        )
    )
}

@Composable
fun SettingsDebugBox(userInfo: UserInfoModel, cookieToken: String?) {
    val context = LocalContext.current
    BoxedSettings(
        onClick = {
            // for effect
            val jsonData = buildString {
                append("{\n\t")
                append("\"version\": \"${Constants.APP_VERSION}\",")
                append("\n\t")
                append("\"hasCookie\": ${cookieToken != null},")
                append("\n\t")
                append("\"user\": {\n")
                val uuid = if (userInfo.id != null) "\"${userInfo.id}\"" else "null"
                append("\t\t\"id\": $uuid\n")
                append("\t}\n")
                append("}\n")
            }
            val clipboard = context.getSystemService(ClipboardManager::class.java)
            val clip = ClipData.newPlainText("user debug data", jsonData)
            clipboard.setPrimaryClip(clip)
            Toast
                .makeText(context, "Debug Copied!", Toast.LENGTH_SHORT)
                .show()
        }
    ) {
        Text(text = "Debug Info", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.sp)
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append("Version:")
                }
                append(" ${Constants.APP_VERSION}")
            },
            fontSize = 13.sp,
            letterSpacing = 0.sp
        )
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append("Repository:")
                }
                append(" ${Constants.APP_REPO}")
            },
            fontSize = 13.sp,
            letterSpacing = 0.sp
        )
        if (cookieToken != null) {
            Text(
                text = cookieToken,
                fontSize = 11.sp,
                letterSpacing = 0.sp,
                lineHeight = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}

@Composable
fun SettingsScreen(appState: AppState, userSettings: UserSettings) {
    var userInfo by remember { mutableStateOf(appState.getCurrentUser()!!) }
    val swipeState = rememberSwipeRefreshState(false)
    var loadingState by rememberSaveable { mutableStateOf(false) }
    val log = getLogger("SettingsScreenView")

    if (loadingState) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .zIndex(99f)
        )
    } else {
        Spacer(
            modifier = Modifier
                .height(4.dp)
                .zIndex(99f)
        )
    }

    SwipeRefresh(
        state = swipeState,
        onRefresh = {
            loadingState = true
            appState.coroutineScope.launch {
                when (val result = appState.apiState.getUser()) {
                    is NetworkResponse.Success -> {
                        if (result.body.loggedIn) {
                            userInfo = result.body
                        } else {
                            Toast.makeText(
                                appState.contextState,
                                "You got logged out, please login again!",
                                Toast.LENGTH_SHORT,
                            ).show()
                            delay(1500L)
                            appState.navController.navigate(ScreenItem.LoginScreen.route) {
                                popUpToTop(appState.navController)
                            }
                        }
                    }
                    is NetworkResponse.Error -> {
                        result.error?.let { log.e(it.stackTraceToString()) }
                        Toast.makeText(
                            appState.contextState,
                            result.error.toString(),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
                loadingState = false
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TextHead(text = "Theme")
            DarkModeToggle(appState.getDarkMode()) { newMode ->
                appState.setDarkMode(newMode)
                userSettings.theme = newMode
            }
            Spacer(modifier = Modifier.height(4.dp))
            TextHead(text = "User")
            BoxedSettings(
                onClick = {
                    val clipboard = appState.contextState.getSystemService(ClipboardManager::class.java)
                    val clip = ClipData.newPlainText("user id", userInfo.id!!)
                    clipboard.setPrimaryClip(clip)
                    Toast
                        .makeText(appState.contextState, "Copied!", Toast.LENGTH_SHORT)
                        .show()
                }
            ) {
                Text(text = "ID", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = 0.sp)
                Text(text = userInfo.id!!, fontSize = 13.sp, letterSpacing = 0.sp)
            }
            NameChangeModule(
                context = appState.contextState,
                apiState = appState.apiState,
                coroScope = appState.coroutineScope,
                user = userInfo,
                onInfoUpdate = { newUser ->
                    appState.setCurrentUser(newUser)
                    userInfo = newUser
                }
            )
            PasswordChangeModule(
                context = appState.contextState,
                apiState = appState.apiState,
                coroScope = appState.coroutineScope,
                user = userInfo,
            ) {
                appState.navController.navigate(ScreenItem.LoginScreen.route) {
                    popUpToTop(appState.navController)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextHead(text = "Server")
            AnnouncerChangeModule(appState = appState, user = userInfo) { newUser ->
                log.d(newUser)
                appState.setCurrentUser(newUser)
                userInfo = newUser
            }

            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 4.dp,
                        start = 20.dp,
                        end = 20.dp,
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            LogoutButton(appState, modifier = Modifier.padding(4.dp))
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 4.dp,
                        start = 20.dp,
                        end = 20.dp,
                    )
            )
            Spacer(modifier = Modifier.height(8.dp))
            SettingsDebugBox(userInfo = userInfo, cookieToken = appState.getUserCookie())
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}
