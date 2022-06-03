package me.naoti.panelapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch
import me.naoti.panelapp.network.models.ProjectInfoModel
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.components.ProjectCardInfo
import me.naoti.panelapp.ui.theme.Gray100
import me.naoti.panelapp.ui.theme.Gray200
import me.naoti.panelapp.ui.theme.Gray600
import me.naoti.panelapp.ui.theme.Gray700
import me.naoti.panelapp.utils.getLogger

suspend fun getProjectInformation(projectId: String, appState: AppState): ProjectInfoModel? {
    val log = getLogger("ProjectInfoFetch")
    var projectInfo: ProjectInfoModel? = null
    when (val state = appState.apiState.getProject(projectId)) {
        is NetworkResponse.Success -> {
            projectInfo = state.body.data!!
            log.i("Success fetching ongoing projects...")
            log.d(state.body.data!!)
        }
        is NetworkResponse.Error -> {
            state.error?.let { log.e(it.stackTraceToString()) }
            Toast.makeText(
                appState.contextState, "Failed to get project info!", Toast.LENGTH_SHORT
            ).show()
        }
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

    var projectInfo by rememberSaveable { mutableStateOf<ProjectInfoModel?>(null) }
    var isInit by rememberSaveable { mutableStateOf(false) }
    val swipeState = rememberSwipeRefreshState(false)

    LaunchedEffect(key1 = true) {
        appState.coroutineScope.launch {
            // request to API for project information
            projectInfo = getProjectInformation(projectId, appState)
            isInit = true
        }
    }

    Scaffold(
        scaffoldState = appState.scaffoldState,
        topBar = {
            TopAppBar {
                Icon(
                    Icons.Filled.ArrowBack, contentDescription = "Go back",
                    modifier = Modifier.padding(4.dp).clickable {
                        // go back
                        appState.navController.popBackStack()
                    }
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(text = if (projectInfo == null) "..." else projectInfo!!.title)
            }
        },
    ) { scafPad ->
        SwipeRefresh(
            state = swipeState,
            onRefresh = {
                appState.coroutineScope.launch {
                    val newProject = getProjectInformation(projectId, appState)
                    if (newProject != null) {
                        projectInfo = newProject
                    }
                }
            },
            Modifier.padding(
                top = scafPad.calculateTopPadding(),
                bottom = scafPad.calculateBottomPadding()
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
                projectInfo?.let { project -> ProjectCardInfo(project, appState) }
            }
        }
    }
}