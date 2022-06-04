package me.naoti.panelapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch
import me.naoti.panelapp.network.models.ProjectListModel
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.components.ProjectCard
import me.naoti.panelapp.utils.getLogger

suspend fun getProjects(appState: AppState): List<ProjectListModel> {
    val log = getLogger("ProjectsViewFetch")
    log.i("Fetching all projects...")
    val actualRes = when (val res = appState.apiState.getProjects()) {
        is NetworkResponse.Success -> {
            log.i("Success, returning data...")
            log.d("Projects=" + res.body.data!!)
            res.body.data ?: listOf()
        }
        is NetworkResponse.Error -> {
            log.e("Failed to fetch...")
            res.error?.let { log.e(it.stackTraceToString()) }
            Toast.makeText(
                appState.contextState, "Failed to get project info!", Toast.LENGTH_SHORT
            ).show()
            listOf()
        }
    }
    return actualRes
}

@Composable
fun ProjectsScreen(appState: AppState) {
    val log = getLogger("ProjectsView")

    var projectsList by rememberSaveable { mutableStateOf<List<ProjectListModel>>(listOf()) }
    var isInit by rememberSaveable { mutableStateOf(false) }
    val swipeState = rememberSwipeRefreshState(false)
    var loadingState by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(key1 = true) {
        appState.coroutineScope.launch {
            // request to API for project information
            loadingState = true
            if (!isInit) {
                projectsList = getProjects(appState)
                isInit = true
            }
            loadingState = false
        }
    }

    if (loadingState) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )   
    } else {
        Spacer(modifier = Modifier.height(4.dp))
    }
    SwipeRefresh(
        state = swipeState,
        onRefresh = {
            appState.coroutineScope.launch {
                log.i("Reloading...")
                loadingState = true
                val result = getProjects(appState)
                if (result.isEmpty() && projectsList.isNotEmpty()) {
                    projectsList = result
                } else {
                    log.e("Failed to refresh????")
                }
                loadingState = false
            }
        }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 0.dp,
                )
                .verticalScroll(rememberScrollState())
        ) {
            if (isInit) {
                if (projectsList.isEmpty()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        "No Projects",
                        style = TextStyle(
                            fontWeight = FontWeight.Light,
                            fontSize = 18.sp,
                        )
                    )
                } else {
                    Spacer(modifier = Modifier.height(20.dp))
                    projectsList.forEach { project ->
                        ProjectCard(project = project , appCtx = appState)
                    }
                }
            }
        }
    }
}