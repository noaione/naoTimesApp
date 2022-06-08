package me.naoti.panelapp.ui.screens

import android.app.appsearch.SearchResult
import android.content.Context
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.haroldadmin.cnradapter.NetworkResponse
import kotlinx.coroutines.CoroutineScope
import me.naoti.panelapp.R
import me.naoti.panelapp.network.models.AnimeMatchModel
import me.naoti.panelapp.state.AppState
import me.naoti.panelapp.ui.components.NetworkSearch
import me.naoti.panelapp.ui.components.SearchDebouncer
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
                listOf<AnimeSearchResult>()
            }
        }
        return actualRes
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AnilistSearchBar(appState: AppState, onItemSelect: ((AnimeMatchModel) -> Unit)? = null) {
    NetworkSearch<AnimeSearchResult>(
        items = listOf(),
        itemContent = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(text = it.content.asResult(), modifier = Modifier.padding(4.dp).wrapContentWidth(Alignment.Start))
            }
        },
        searchDebouncer = AnilistDebouncerManager(appState.contextState, appState.coroutineScope),
        onItemSelected = { item ->
            onItemSelect?.let {
                it(item.content)
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectAddScreen(appState: AppState) {
    val log = getLogger("ProjectAddViewScreen")
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
            modifier = Modifier.padding(
                top = paddingVal.calculateTopPadding(),
                bottom = paddingVal.calculateBottomPadding(),
                start = 10.dp,
                end = 10.dp,
            )
        ) {
            // image
            // Search box 1
            AnilistSearchBar(appState = appState, onItemSelect = {
                log.d("AnilistSearch=$it")
            })
        }
    }
}
