package me.naoti.panelapp.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.naoti.panelapp.R
import me.naoti.panelapp.builder.CoilImage
import me.naoti.panelapp.network.ErrorCode
import me.naoti.panelapp.network.models.AnimeMatchModel
import me.naoti.panelapp.network.models.ProjectAddAnimeModel
import me.naoti.panelapp.network.models.ProjectAddModel
import me.naoti.panelapp.network.models.ProjectAddRoleModel
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.ScreenItem
import me.naoti.panelapp.ui.components.NetworkSearch
import me.naoti.panelapp.ui.components.SearchDebouncer
import me.naoti.panelapp.ui.components.StatusRole
import me.naoti.panelapp.ui.popUpToTop
import me.naoti.panelapp.ui.preferences.UserSettings
import me.naoti.panelapp.ui.theme.Green500
import me.naoti.panelapp.ui.theme.darker
import me.naoti.panelapp.utils.getLogger

data class AnimeSearchResult(
    val content: AnimeMatchModel,
) {
    override fun toString(): String {
        return content.asResult()
    }
}

internal class AnilistDebouncerManager(
    context: Context,
    coroutineScope: CoroutineScope,
) : SearchDebouncer<AnimeSearchResult>(context, coroutineScope) {
    override suspend fun searchNet(query: String): List<AnimeSearchResult> {
        val actualRes = when (val result = apiState.findAnime(query)) {
            is NetworkResponse.Success -> {
                val resulting = mutableListOf<AnimeSearchResult>()
                result.body.results.forEach { aniMatch ->
                    resulting.add(AnimeSearchResult(aniMatch))
                }
                log.i("Sending results over to callback!")
                resulting.toList()
            }
            is NetworkResponse.Error -> {
                log.e("An error occurred while trying to find something...")
                result.error?.let { log.e(it.stackTraceToString()) }
                listOf()
            }
        }
        return actualRes
    }
}

@Composable
fun AnilistSearchBar(
    appState: AppState,
    onItemSelect: ((AnimeMatchModel) -> Unit)? = null,
    onCleared: (() -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
) {
    NetworkSearch(
        items = listOf(),
        itemContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(text = it.content.asResult(), modifier = Modifier
                    .padding(4.dp)
                    .wrapContentWidth(Alignment.Start))
            }
        },
        searchDebouncer = AnilistDebouncerManager(appState.contextState, appState.coroutineScope),
        onItemSelected = { item ->
            onItemSelect?.let {
                it(item.content)
            }
        },
        onCleared = onCleared,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        isError = isError
    )
}

private fun validateEverything(project: AnimeMatchModel?, episodeCount: String): Pair<String?, Pair<Boolean, Boolean>> {
    if (project == null) {
        return Pair("Please select an Anime first!", Pair(true, false))
    }
    val episode = project.episodes ?: 0
    if (episode < 1) {
        if (episodeCount.isEmpty()) {
            return Pair("Episode count is needed, please fill it!", Pair(false, true))
        }
        try {
            val epAsNum = episodeCount.toInt()
            if (epAsNum < 1) {
                return Pair("Episode count is needed, please fill it!", Pair(false, true))
            }
        } catch (e: NumberFormatException) {
            return Pair("Episode is needed, and it's not a valid number!", Pair(false, true))
        }
    }
    return Pair(null, Pair(false, false))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectAddScreen(appState: AppState, userSettings: UserSettings) {
    val log = getLogger("ProjectAddViewScreen")
    var selectedAnime by remember { mutableStateOf<AnimeMatchModel?>(null) }

    var translatorId by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var translatorCheckId by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var encoderId by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var editorId by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var timerId by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var typesetterId by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var qualityCheckId by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var overrideEpisodeCount by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var isSubmitting by rememberSaveable { mutableStateOf(false) }
    var validateSearchBox by remember { mutableStateOf(false) }
    var validateEpisodeBox by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var lastKnownError by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        appState.navController.popBackStack()
                    }) {
                        Icon(painterResource(id = R.drawable.ic_icons_chevron_left), contentDescription = "Go Back")
                    }
                },
                title = {
                    Text(
                        text = "Add New Project",
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.sp,
                            fontSize = 18.sp
                        )
                    )
                }
            )
        },
    ) { paddingVal ->
        Column(
            modifier = Modifier
                .padding(
                    top = paddingVal.calculateTopPadding(),
                    bottom = paddingVal.calculateBottomPadding(),
                    start = 10.dp,
                    end = 10.dp,
                )
                .verticalScroll(rememberScrollState())
        ) {
            // image
            if (selectedAnime != null) {
                CoilImage(
                    url = selectedAnime!!.selectPoster(),
                    contentDescription = "Image Poster",
                    context = appState.contextState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .height(100.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .height(100.dp)
                        .padding(10.dp)
                        .width(60.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                )
            }
            // Search box project
            Text(
                text = "Anime",
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
            AnilistSearchBar(
                appState = appState,
                onItemSelect = {
                    log.d("AnilistSearch=$it")
                    selectedAnime = it
                    errorMessage = null
                    validateSearchBox = false
                },
                onCleared = {
                    log.i("Item cleared, removing everything...")
                    selectedAnime = null
                    overrideEpisodeCount = TextFieldValue("")
                    errorMessage = null
                    validateSearchBox = false
                },
                enabled = !isSubmitting,
                isError = validateSearchBox,
            )
            // Episode
            if (selectedAnime != null) {
                val episode = selectedAnime!!.episodes ?: 0
                if (episode < 1) {
                    OutlinedTextField(
                        value = overrideEpisodeCount,
                        onValueChange = { epVal ->
                            overrideEpisodeCount = epVal
                            if (errorMessage != null) {
                                errorMessage = null
                            }
                            if (validateEpisodeBox) validateEpisodeBox = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("EpisodeCount")
                            .padding(4.dp),
                        label = { Text(text = "Total Episodes")},
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        ),
                        enabled = !isSubmitting,
                        isError = validateEpisodeBox,
                    )
                }
            }
            Text(
                text = "Role will be created automatically, please check your server after it's done",
                color = MaterialTheme.colorScheme.error,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .fillMaxWidth(),
                letterSpacing = 0.sp,
            )

            // Staff
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Staff",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .fillMaxWidth(),
            )
            Text(
                text = "Please enter Discord ID, you can leave it empty!",
                color = MaterialTheme.colorScheme.error,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 0.dp)
                    .fillMaxWidth(),
                letterSpacing = 0.sp,
            )

            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = translatorId,
                onValueChange = {
                    translatorId = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("TranslateID")
                    .padding(4.dp),
                label = { Text(text = StatusRole.TL.getFull())},
                placeholder = { Text(text = "xxxxxxxxxxxxxxxxxx") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                enabled = !isSubmitting,
            )
            OutlinedTextField(
                value = translatorCheckId,
                onValueChange = {
                    translatorCheckId = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("TranslateCheckID")
                    .padding(4.dp),
                label = { Text(text = StatusRole.TLC.getFull())},
                placeholder = { Text(text = "xxxxxxxxxxxxxxxxxx") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                enabled = !isSubmitting,
            )
            OutlinedTextField(
                value = encoderId,
                onValueChange = {
                    encoderId = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("EncodeID")
                    .padding(4.dp),
                label = { Text(text = StatusRole.ENC.getFull())},
                placeholder = { Text(text = "xxxxxxxxxxxxxxxxxx") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                enabled = !isSubmitting,
            )
            OutlinedTextField(
                value = editorId,
                onValueChange = {
                    editorId = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("EditorID")
                    .padding(4.dp),
                label = { Text(text = StatusRole.ED.getFull())},
                placeholder = { Text(text = "xxxxxxxxxxxxxxxxxx") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                enabled = !isSubmitting,
            )
            OutlinedTextField(
                value = timerId,
                onValueChange = {
                    timerId = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("TimerID")
                    .padding(4.dp),
                label = { Text(text = StatusRole.TM.getFull())},
                placeholder = { Text(text = "xxxxxxxxxxxxxxxxxx") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                enabled = !isSubmitting,
            )
            OutlinedTextField(
                value = typesetterId,
                onValueChange = {
                    typesetterId = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("TypesetID")
                    .padding(4.dp),
                label = { Text(text = StatusRole.TS.getFull())},
                placeholder = { Text(text = "xxxxxxxxxxxxxxxxxx") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                enabled = !isSubmitting,
            )
            OutlinedTextField(
                value = qualityCheckId,
                onValueChange = {
                    qualityCheckId = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("QualityCheckID")
                    .padding(4.dp),
                label = { Text(text = StatusRole.QC.getFull())},
                placeholder = { Text(text = "xxxxxxxxxxxxxxxxxx") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                ),
                enabled = !isSubmitting,
            )

            // Add Button
            Spacer(modifier = Modifier.height(10.dp))
            AnimatedVisibility(
                visible = errorMessage != null,
            ) {
                Text(
                    text = errorMessage ?: (lastKnownError ?: "An Unknown Error Has Occurred!"),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }
            Button(
                onClick = {
                    // submit to API
                    val (valErr, valErrBool) = validateEverything(selectedAnime, overrideEpisodeCount.text)
                    val (errProject, errEpisode) = valErrBool
                    if (valErr != null) {
                        errorMessage = valErr
                        lastKnownError = valErr
                        validateEpisodeBox = errEpisode
                        validateSearchBox = errProject
                        return@Button
                    }
                    isSubmitting = true
                    validateEpisodeBox = false
                    validateSearchBox = false
                    log.i("Submitting to API...")
                    appState.coroutineScope.launch {
                        val actualEpisode = selectedAnime?.episodes ?: overrideEpisodeCount.text.toInt()
                        if (actualEpisode < 1) {
                            validateEpisodeBox = true
                            errorMessage = "Please enter a valid episode first!"
                            lastKnownError = "Please enter a valid episode first!"
                            isSubmitting = false
                            return@launch
                        }
                        val animeProj = selectedAnime?.let {
                            ProjectAddAnimeModel(
                                id = it.id.toString(),
                                name = it.getTitle(),
                                episode = actualEpisode
                            )
                        }
                        if (animeProj == null) {
                            errorMessage = "Please select an Anime first!"
                            lastKnownError = "Please select an Anime first!"
                            validateSearchBox = true
                            isSubmitting = false
                            return@launch
                        }
                        val allRoles = mutableListOf<ProjectAddRoleModel>()
                        allRoles.add(ProjectAddRoleModel(id = translatorId.text, role = StatusRole.TL.name))
                        allRoles.add(ProjectAddRoleModel(id = translatorCheckId.text, role = StatusRole.TLC.name))
                        allRoles.add(ProjectAddRoleModel(id = encoderId.text, role = StatusRole.ENC.name))
                        allRoles.add(ProjectAddRoleModel(id = editorId.text, role = StatusRole.ED.name))
                        allRoles.add(ProjectAddRoleModel(id = timerId.text, role = StatusRole.TM.name))
                        allRoles.add(ProjectAddRoleModel(id = typesetterId.text, role = StatusRole.TS.name))
                        allRoles.add(ProjectAddRoleModel(id = qualityCheckId.text, role = StatusRole.QC.name))
                        val projectModel = appState.getCurrentUser()!!.id?.let {
                            ProjectAddModel(
                                server = it,
                                anime = animeProj,
                                roles = allRoles.toList()
                            )
                        }
                        if (projectModel == null) {
                            Toast.makeText(
                                appState.contextState,
                                "User is empty, please re-login",
                                Toast.LENGTH_SHORT
                            ).show()
                            delay(1500L)
                            isSubmitting = false
                            appState.navController.navigate(ScreenItem.LoginScreen.route) {
                                popUpToTop(appState.navController)
                                launchSingleTop = true
                            }
                            return@launch
                        }
                        when (val result = appState.apiState.addProject(projectModel)) {
                            is NetworkResponse.Success -> {
                                if (result.body.success) {
                                    log.i("Success, showing toast then redirecting...")
                                    Toast.makeText(
                                        appState.contextState,
                                        "Success, redirecting...",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    delay(1500L)
                                    log.i("Navigating to resource project...")
                                    val navRoute = ScreenItem.ProjectScreen.route.replace(
                                        "{projectId}", selectedAnime!!.id.toString()
                                    )
                                    userSettings.refresh = true
                                    appState.navController.navigate(navRoute) {
                                        val navTarget = appState.navController.currentDestination
                                        if (navTarget != null) {
                                            popUpTo(navTarget.id) {
                                                inclusive = true
                                            }
                                        }
                                    }
                                } else {
                                    val body = result.body
                                    val theText = when (body.code) {
                                        ErrorCode.ProjectAlreadyRegistered -> {
                                            validateSearchBox = true
                                            body.code.asText(selectedAnime!!.getTitle())
                                        }
                                        else -> {
                                            if (body.code != null) {
                                                body.code.asText()
                                            } else {
                                                ErrorCode.UnknownError.asText()
                                            }
                                        }
                                    }
                                    log.e("Failed to add project: $theText")
                                    errorMessage = theText
                                    lastKnownError = theText
                                }
                            }
                            is NetworkResponse.Error -> {
                                val body = result.body
                                var theText = result.error.toString()
                                if (body != null) {
                                    theText = when (body.code) {
                                        ErrorCode.ProjectAlreadyRegistered -> {
                                            validateSearchBox = true
                                            body.code.asText(selectedAnime!!.getTitle())
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
                                log.e("Failed to add project: $theText")
                                errorMessage = theText
                                lastKnownError = theText
                            }
                        }
                    }
                },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green500,
                    contentColor = White,
                    disabledContentColor = White.darker(.3f),
                    disabledContainerColor = Green500.darker(.3f)
                ),
                enabled = !isSubmitting,
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                Text("Add")
            }
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}
