package me.naoti.panelapp.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch
import me.naoti.panelapp.R
import me.naoti.panelapp.network.ErrorCode
import me.naoti.panelapp.network.models.DefaultEmptyProject
import me.naoti.panelapp.network.models.ProjectEpisodeAddModel
import me.naoti.panelapp.network.models.ProjectInfoModel
import me.naoti.panelapp.network.models.StatusProject
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.components.EpisodeCard
import me.naoti.panelapp.ui.components.ProjectCardInfo
import me.naoti.panelapp.ui.theme.darker
import me.naoti.panelapp.utils.getLogger

suspend fun getProjectInformation(projectId: String, appState: AppState, forceRefresh: Boolean = false): ProjectInfoModel? {
    val log = getLogger("ProjectInfoFetch[$projectId]")
    var projectInfo: ProjectInfoModel? = null
    if (forceRefresh) {
        appState.evictCacheProject(projectId)
    }

    val result = appState.getProjectCache(projectId) {
        log.i("Cache empty, fetching to database")
        when (val state = appState.apiState.getProject(projectId)) {
            is NetworkResponse.Success -> {
                log.i("Success fetching ongoing projects...")
                log.d(state.body.data!!)
                state.body.data ?: DefaultEmptyProject
            }
            is NetworkResponse.Error -> {
                state.error?.let { log.e(it.stackTraceToString()) }
                Toast.makeText(
                    appState.contextState, "Failed to get project info!", Toast.LENGTH_SHORT
                ).show()
                DefaultEmptyProject
            }
        }
    }
    if (result == DefaultEmptyProject) {
        log.e("Got default empty, evicting cache and returning null!")
        appState.evictCacheProject(projectId)
    } else {
        projectInfo = result
    }
    return projectInfo
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(appState: AppState, projectId: String?, clickSource: String) {
    val log = getLogger("ProjectInfoView")
    log.i("Got from $clickSource")
    if (projectId == null) {
        log.info("Project url is null or missing, using temp view")
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Unknown Project!", style = TextStyle(fontSize = 28.sp))
        }
        return
    }

    var projectInfo by remember { mutableStateOf<ProjectInfoModel?>(null) }
    val swipeState = rememberSwipeRefreshState(false)
    val mutableStatuses = remember { mutableStateListOf<StatusProject>() }
    var loadingState by remember { mutableStateOf(false) }
    var episodeAddDialog by remember {
        mutableStateOf(false)
    }
    var isSubmittingEp by remember { mutableStateOf(false) }
    var isValidEpisodeAdd by remember {
        mutableStateOf(false)
    }
    var episodeAddCount by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue("0"))
    }

    LaunchedEffect(key1 = true) {
        appState.coroutineScope.launch {
            // request to API for project information
            loadingState = true
            val projectData = getProjectInformation(projectId, appState)
            projectData?.statuses?.forEach { status ->
                mutableStatuses.add(status)
            }
            projectInfo = projectData
            loadingState = false
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = if (projectInfo == null) "..." else projectInfo!!.title,
                        style = TextStyle(
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.sp,
                            fontSize = 18.sp
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        appState.navController.popBackStack()
                    }) {
                        Icon(painterResource(id = R.drawable.ic_icons_chevron_left), contentDescription = "Go Back")
                    }
                }
            )
        },
        floatingActionButton = {
            val isEnabled = projectInfo != null
            val contentCol = MaterialTheme.colorScheme.onSecondary
            val containerCol = MaterialTheme.colorScheme.secondary
            ExtendedFloatingActionButton(
                onClick = {
                    if (isEnabled) {
                        log.i("Showing episode button")
                        episodeAddDialog = true
                    }
                },
                modifier = Modifier
                    .padding(6.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .testTag("FABProjectAddEpisodes"),
                contentColor = if (isEnabled) contentCol else contentCol.darker(.4f),
                containerColor = if (isEnabled) containerCol else containerCol.darker(.4f)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add New Episode",
                )
                Text(text = "Add Episodes")
            }
        },
    ) { paddingVal ->
        SwipeRefresh(
            state = swipeState,
            onRefresh = {
                appState.coroutineScope.launch {
                    log.i("Fetching new project information")
                    loadingState = true
                    val newProject = getProjectInformation(projectId, appState, forceRefresh = true)
                    loadingState = false
                    if (newProject != null) {
                        // replace or add
                        newProject.statuses.forEach { statusProject ->
                            val epIndex = mutableStatuses.indexOfFirst { innerStat ->
                                innerStat.episode == statusProject.episode
                            }
                            if (epIndex != -1 && mutableStatuses[epIndex].notSame(statusProject)) {
                                mutableStatuses[epIndex] = statusProject
                            } else {
                                mutableStatuses.add(statusProject)
                            }
                        }
                        projectInfo = newProject
                    } else {
                        Toast.makeText(appState.contextState, "Failed to update!", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            Modifier.padding(
                top = paddingVal.calculateTopPadding(),
                bottom = paddingVal.calculateBottomPadding()
            )
        ) {
            if (loadingState) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .zIndex(99f)
                )
            } else {
                Spacer(modifier = Modifier
                    .height(4.dp)
                    .zIndex(99f))
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                    )
                    .verticalScroll(rememberScrollState())
//                    .placeholder(
//                        visible = projectInfo == null,
//                        highlight = PlaceholderHighlight.shimmer(
//                            highlightColor = MaterialTheme.colorScheme.onSecondary.lighter(.2f)
//                        ),
//                        color = MaterialTheme.colorScheme.onSecondary
//                    )
            ) {
                projectInfo?.let { project ->
                    Spacer(modifier = Modifier.height(4.dp))
                    ProjectCardInfo(project, appState)

                    Spacer(modifier = Modifier.height(10.dp))
                    mutableStatuses.forEach { status ->
                        EpisodeCard(
                            projectId = project.id,
                            status = status,
                            appState = appState,
                            onStateEdited = { stat ->
                                mutableStatuses.forEachIndexed { index, statusProject ->
                                    statusProject.takeIf { it.episode == stat.episode }?.let {
                                        mutableStatuses[index] = it.copy(
                                            progress = stat.progress
                                        )
                                    }
                                }
                            },
                            onRemove = { deleteStatus ->
                                log.i("Searching episode from status set")
                                val itemIdx = mutableStatuses.indexOfFirst { statusProject ->
                                    deleteStatus.episode == statusProject.episode
                                }
                                if (itemIdx != -1) {
                                    try {
                                        log.i("Found, removing episode from actual project episode set")
                                        mutableStatuses.removeAt(itemIdx)
                                    } catch (e: IndexOutOfBoundsException) {/* ignore */}
                                } else {
                                    log.w("not found, huh what?")
                                }
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
        
        if (episodeAddDialog) {
            AlertDialog(
                onDismissRequest = {
                    if (!isSubmittingEp) {
                        episodeAddDialog = false
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            // submit
                            if (isValidEpisodeAdd && projectInfo != null) {
                                isSubmittingEp = true
                                appState.coroutineScope.launch {
                                    val epAddCount = episodeAddCount.text.toIntOrNull() ?: 0
                                    val lastEpisode = mutableStatuses.last().episode
                                    val newEpisodes = mutableListOf<Int>()
                                    repeat(epAddCount) { idx ->
                                        newEpisodes.add((lastEpisode + (idx + 1)))
                                    }
                                    when (val result = appState.apiState.addProjectEpisode(
                                        ProjectEpisodeAddModel.create(projectInfo!!.id, newEpisodes)
                                    )) {
                                        is NetworkResponse.Success -> {
                                            val body = result.body
                                            if (body.data != null) {
                                                body.data.forEach { statusProject ->
                                                    val epIndex = mutableStatuses.indexOfFirst { innerStat ->
                                                        innerStat.episode == statusProject.episode
                                                    }
                                                    if (epIndex != -1 && mutableStatuses[epIndex].notSame(statusProject)) {
                                                        mutableStatuses[epIndex] = statusProject
                                                    } else {
                                                        mutableStatuses.add(statusProject)
                                                    }
                                                }
                                            } else {
                                                val errMsg = body.code?.asText() ?: ErrorCode.UnknownError.asText()
                                                Toast.makeText(
                                                    appState.contextState,
                                                    errMsg,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        is NetworkResponse.Error -> {
                                            result.error?.let { log.e(it.stackTraceToString()) }
                                            var errMsg = result.error.toString()
                                            if (result.body != null) {
                                                errMsg = result.body!!.code?.asText() ?: ErrorCode.UnknownError.asText()
                                            }
                                            Toast.makeText(
                                                appState.contextState,
                                                errMsg,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    isSubmittingEp = false
                                    episodeAddDialog = false
                                }
                            }
                        },
                        enabled = !isSubmittingEp && isValidEpisodeAdd,
                    ) {
                        Text(text = "Add")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            if (!isSubmittingEp) {
                                episodeAddDialog = false
                            }
                        },
                        enabled = !isSubmittingEp,
                    ) {
                        Text(text = "Cancel")
                    }
                },
                title = {
                    Text(text = "Add Episodes", fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp)
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                    ) {
                        Text(text = "Please enter the amount of episode you want to add!", textAlign = TextAlign.Center)
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                                .fillMaxWidth()
                                .testTag("ProjectModifyEpisodeAddCount"),
                            shape = RoundedCornerShape(6.dp),
                            value = episodeAddCount,
                            onValueChange = {
                                if (it.text.isEmpty()) {
                                    episodeAddCount = it
                                    isValidEpisodeAdd = false
                                    return@OutlinedTextField
                                }
                                val asNumber = it.text.toIntOrNull()
                                if (asNumber != null) {
                                    episodeAddCount = it
                                    isValidEpisodeAdd = asNumber > 0
                                } else {
                                    isValidEpisodeAdd = false
                                }
                            },
                            label = { Text("Enter amount") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            singleLine = true,
                            enabled = !isSubmittingEp,
                        )
                        
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append("Last Episode: ")
                                }
                                append(mutableStatuses.last().episode.toString())
                            },
                            modifier = Modifier.padding(start = 4.dp, end = 4.dp, top = 2.dp),
                            fontSize = 12.sp,
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append("Total New Episode: ")
                                }
                                val epCount = episodeAddCount.text.toIntOrNull() ?: 0
                                append(epCount.toString())
                            },
                            modifier = Modifier.padding(horizontal = 4.dp),
                            fontSize = 12.sp,
                        )
                        Text(
                            text = buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                    append("New Episodes: ")
                                }
                                val lastEpisode = mutableStatuses.last().episode
                                val newEpisodes = mutableListOf<String>()
                                val epAddCount = episodeAddCount.text.toIntOrNull() ?: 0
                                if (epAddCount > 0) {
                                    repeat(epAddCount) { idx ->
                                        newEpisodes.add((lastEpisode + (idx + 1)).toString())
                                    }
                                    append(newEpisodes.joinToString(", "))
                                } else {
                                    append("None added")
                                }
                            },
                            modifier = Modifier.padding(horizontal = 4.dp).wrapContentWidth(Alignment.Start),
                            fontSize = 12.sp,
                        )
                    }
                }
            )
        }
    }
}