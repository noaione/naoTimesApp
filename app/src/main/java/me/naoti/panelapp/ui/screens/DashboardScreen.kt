package me.naoti.panelapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.launch
import me.naoti.panelapp.network.models.Project
import me.naoti.panelapp.network.models.StatsKeyValueModel
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.components.DashboardProjectCard
import me.naoti.panelapp.ui.theme.*
import me.naoti.panelapp.utils.getLogger

data class APIResult(
    var ongoingProjects: List<Project> = listOf(),
    var dashboardInfo: List<StatsKeyValueModel>? = null
)

suspend fun getAPIData(appState: AppState): APIResult {
    val log = getLogger("DashboardAPIFetch")
    val apiResult = APIResult()
    when (val ongoingState = appState.apiState.getLatestOngoingProjects()) {
        is NetworkResponse.Success -> {
            apiResult.ongoingProjects = ongoingState.body.data!!
            log.i("Success fetching ongoing projects...")
            log.d(ongoingState.body.data!!)
        }
        is NetworkResponse.Error -> {
            ongoingState.error?.let { log.e(it.stackTraceToString()) }
            Toast.makeText(
                appState.contextState, "Failed to get ongoing projects!", Toast.LENGTH_SHORT
            ).show()
        }
    }
    when (val dashboardState = appState.apiState.getServerStats()) {
        is NetworkResponse.Success -> {
            apiResult.dashboardInfo = dashboardState.body.data!!
            log.i("Success fetching server stats...")
            log.d(dashboardState.body.data!!)
        }
        is NetworkResponse.Error -> {
            dashboardState.error?.let { log.e(it.stackTraceToString()) }
            Toast.makeText(
                appState.contextState, "Failed to get server stats!", Toast.LENGTH_SHORT
            ).show()
        }
    }
    return apiResult
}

@Composable
fun DashboardScreen(appState: AppState, paddingValues: PaddingValues? = null) {
    val log = getLogger("DashboardScreenVIew")
    var ongoingProject by rememberSaveable { mutableStateOf<List<Project>>(listOf()) }
    val swipeState = rememberSwipeRefreshState(false)
    var isInitialized by rememberSaveable { mutableStateOf(false) }
    var ongoingCount by rememberSaveable { mutableStateOf(-1) }
    var finishedCount by rememberSaveable { mutableStateOf(-1) }

    LaunchedEffect(key1 = true) {
        if (!isInitialized) {
            appState.coroutineScope.launch {
                log.i("Initializing data...")
                val apiRes = getAPIData(appState)
                ongoingProject = apiRes.ongoingProjects
                apiRes.dashboardInfo?.let { it ->
                    it.forEach { v ->
                        if (v.key == "ongoing") {
                            ongoingCount = v.data
                        } else if (v.key == "done") {
                            finishedCount = v.data
                        }
                    }
                }
                log.i("Data initialized!")
                isInitialized = true
            }
        }
    }

    SwipeRefresh(
        state = swipeState,
        onRefresh = {
            appState.coroutineScope.launch {
                log.i("Launching scope to refresh state...")
                val rememberOldOne = ongoingCount
                val rememberOldTwo = finishedCount
                ongoingCount = -1
                finishedCount = -1
                val apiRes = getAPIData(appState)
                ongoingProject = apiRes.ongoingProjects
                if (apiRes.dashboardInfo != null) {
                    apiRes.dashboardInfo!!.forEach { v ->
                        if (v.key == "ongoing") {
                            ongoingCount = v.data
                        } else if (v.key == "done") {
                            finishedCount = v.data
                        }
                    }
                }
                if (ongoingCount == -1) {
                    ongoingCount = rememberOldOne
                }
                if (finishedCount == -1) {
                    finishedCount = rememberOldTwo
                }
                log.i("State refreshed!")
            }
        }
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 0.dp,
            )
            .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "Statistics",
                style = TextStyle(
                    fontWeight = FontWeight.Light,
                    fontSize = 18.sp,
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Stats box
            Row {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .background(Yellow500)
                        .clip(CircleShape),
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Ongoing",
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(CircleShape),
                        tint = White
                    )
                }
                Column(
                    modifier = Modifier.padding(
                        horizontal = 4.dp,
                        vertical = 10.dp
                    )
                ) {
                    Text(
                        text = ongoingCount.toString(),
                        color = if (appState.isDarkMode()) Gray200 else Gray900,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                        ),
                        modifier = Modifier.placeholder(
                            visible = ongoingCount == -1,
                            highlight = PlaceholderHighlight.shimmer(
                                highlightColor = if (appState.isDarkMode()) Gray600 else Gray200
                            ),
                            color = if (appState.isDarkMode()) Gray700 else Gray100
                        )
                    )
                    Text(
                        "Ongoing",
                        color = Gray400
                    )
                }
            }

            Row {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .background(Green500)
                        .clip(RoundedCornerShape(50))
                ) {
                    Icon(
                        Icons.Filled.Done,
                        contentDescription = "Finished",
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(RoundedCornerShape(50)),
                        tint = White
                    )
                }
                Column(
                    modifier = Modifier.padding(
                        horizontal = 4.dp,
                        vertical = 10.dp
                    )
                ) {
                    Text(
                        text = finishedCount.toString(),
                        color = if (appState.isDarkMode()) Gray200 else Gray900,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp,
                        ),
                        modifier = Modifier.placeholder(
                            visible = finishedCount == -1,
                            highlight = PlaceholderHighlight.shimmer(
                                highlightColor = if (appState.isDarkMode()) Gray600 else Gray200
                            ),
                            color = if (appState.isDarkMode()) Gray700 else Gray100
                        )
                    )
                    Text(
                        "Finished",
                        color = Gray400
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // projects
            Text(
                "Ongoing Projects",
                style = TextStyle(
                    fontWeight = FontWeight.Light,
                    fontSize = 18.sp,
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                Modifier.padding(
                    bottom = 8.dp
                )
            ) {
                ongoingProject.forEach { project ->
                    DashboardProjectCard(project = project, appCtx = appState)
                }
            }
        }
    }
}
