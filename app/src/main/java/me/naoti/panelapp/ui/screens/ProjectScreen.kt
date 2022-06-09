package me.naoti.panelapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch
import me.naoti.panelapp.R
import me.naoti.panelapp.network.models.DefaultEmptyProject
import me.naoti.panelapp.network.models.ProjectInfoModel
import me.naoti.panelapp.network.models.StatusProject
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.components.EpisodeCard
import me.naoti.panelapp.ui.components.ProjectCardInfo
import me.naoti.panelapp.utils.getLogger
import java.lang.IndexOutOfBoundsException

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
                },
                actions = {
                    IconButton(onClick = {
                        log.i("Adding new episode clicked!")
                    }) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add New Episode",
                        )
                    }
                }
            )
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
                Spacer(modifier = Modifier.height(4.dp).zIndex(99f))
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
    }
}