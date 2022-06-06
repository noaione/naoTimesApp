package me.naoti.panelapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch
import me.naoti.panelapp.R
import me.naoti.panelapp.network.models.DefaultEmptyProject
import me.naoti.panelapp.network.models.ProjectInfoModel
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.components.EpisodeCard
import me.naoti.panelapp.ui.components.ProjectCardInfo
import me.naoti.panelapp.ui.theme.*
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

@Composable
fun ProjectScreen(appState: AppState, projectId: String?) {
    val log = getLogger("ProjectInfoView")
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

    LaunchedEffect(key1 = true) {
        appState.coroutineScope.launch {
            // request to API for project information
            projectInfo = getProjectInformation(projectId, appState)
        }
    }

    val systemUiController = rememberSystemUiController()
    val isDarkMode = appState.isDarkMode()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = if (isDarkMode) Gray900 else Gray200,
            darkIcons = !isDarkMode
        )
    }

    Scaffold(
        scaffoldState = appState.scaffoldState,
        topBar = {
            TopAppBar(
                backgroundColor = if (appState.isDarkMode()) Gray900 else Gray200,
                contentColor = if (appState.isDarkMode()) White else Gray800
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painterResource(R.drawable.ic_icons_chevron_left),
                            contentDescription = "Go back",
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable {
                                    // go back
                                    appState.navController.popBackStack()
                                }
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(text = if (projectInfo == null) "..." else projectInfo!!.title)
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add New Episode",
                            modifier = Modifier
                                .padding(4.dp)
                                .clickable {
                                    log.i("Showing alert dialog modal for adding new episode")
                                }
                                .clip(CircleShape)
                                .wrapContentWidth(Alignment.End),
                        )
                    }
                }
            }
        },
    ) { paddingVal ->
        SwipeRefresh(
            state = swipeState,
            onRefresh = {
                appState.coroutineScope.launch {
                    log.i("Fetching new project information")
                    val newProject = getProjectInformation(projectId, appState, forceRefresh = true)
                    if (newProject != null) {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                    )
                    .verticalScroll(rememberScrollState())
                    .placeholder(
                        visible = projectInfo == null,
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = if (appState.isDarkMode()) Gray600 else Gray200
                        ),
                        color = if (appState.isDarkMode()) Gray700 else Gray100
                    )
            ) {
                projectInfo?.let { project ->
                    Spacer(modifier = Modifier.height(4.dp))
                    ProjectCardInfo(project, appState)

                    Spacer(modifier = Modifier.height(10.dp))
                    project.statuses.forEach { status ->
                        EpisodeCard(
                            projectId = project.id,
                            status = status,
                            appState = appState,
                            onStateEdited = { stat ->
                                project.statuses.forEachIndexed { index, statusProject ->
                                    statusProject.takeIf { it.episode == stat.episode }?.let {
                                        project.statuses[index] = it.copy(
                                            progress = stat.progress
                                        )
                                    }
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