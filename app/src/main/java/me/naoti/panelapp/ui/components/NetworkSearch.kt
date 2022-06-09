package me.naoti.panelapp.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.naoti.panelapp.network.ApiService
import me.naoti.panelapp.utils.getLogger
import java.security.InvalidParameterException

abstract class SearchDebouncer<T>(
    val context: Context,
    val coroutineScope: CoroutineScope,
    var onSearchResult: ((List<T>, String) -> Unit)? = null,
) {
    private val debouncePeriod = 500L
    private var searchJob: Job? = null
    val apiState = ApiService.getService(context)
    val log = getLogger("AnilistDebouncer")
    var isSearching = false

    abstract suspend fun searchNet(query: String): List<T>

    fun submitSearch(query: String) {
        searchJob?.cancel()
        isSearching = false
        searchJob = coroutineScope.launch {
            log.i("Launching new search job: $query")
            isSearching = true
            delay(debouncePeriod)
            log.i("Job not cancelled, continuing...")
            onSearchResult?.let { innerCall ->
                innerCall(searchNet(query), query)
                isSearching = false
            }
        }
    }
}

@Composable
fun <T> NetworkSearch(
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    searchDebouncer: (SearchDebouncer<T>)? = null,
    onFilterResult: ((List<T>, String) -> List<T>)? = null,
    onCleared: (() -> Unit)? = null,
    enabled: Boolean = true,
    isError: Boolean = false,
    isInitializing: Boolean = false,
) {
    val log = getLogger("NetworkSearch")
    if (searchDebouncer == null && onFilterResult == null) {
        throw InvalidParameterException(
            "NetworkSearch should receive either search debouncer or onFilterResult or even both to works!"
        )
    }
    var itemsFound by remember { mutableStateOf(items) }
    log.i("Initial items: ${items.count()}")
    log.i("Items in dataset: ${itemsFound.count()}")
    var value by remember { mutableStateOf("") }
    var isFocus by remember {
        mutableStateOf(false)
    }
    log.i("Using debouncer? ${searchDebouncer != null}")
    log.i("Using filter? ${onFilterResult != null}")
    log.i("Is focusing? $isFocus")
    if (searchDebouncer != null) {
        searchDebouncer.onSearchResult = { results, query ->
            itemsFound = if (onFilterResult != null) {
                onFilterResult(results, query)
            } else {
                results
            }
            value = query
        }
    }
    val view = LocalView.current
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isFocus = it.isFocused
                },
            value = value,
            onValueChange = { query ->
                if (enabled) {
                    value = query
                    if (searchDebouncer != null) {
                        searchDebouncer.submitSearch(query)
                    } else if (onFilterResult != null) {
                        itemsFound = onFilterResult(items, query)
                    }
                }
            },
            label = { Text(text = "Search...") },
            textStyle = MaterialTheme.typography.bodyMedium,
            isError = isError,
            singleLine = true,
            trailingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val validSearching = isInitializing || (searchDebouncer?.isSearching ?: false)
                    if (validSearching) {
                        DotsFlashing(8.dp)
                    }
                    IconButton(onClick = {
                        if (enabled) {
                            value = ""
                            itemsFound = listOf()
                            if (onCleared != null) {
                                onCleared()
                            }
                        }
                    }) {
                        Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                    }
                }

            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text
            ),
            enabled = enabled
        )
        AnimatedVisibility(visible = isFocus) {
            log.i("Rendering lazy column items...")
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary)
                    .clip(RoundedCornerShape(4.dp)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(itemsFound) { item ->
                    Box(modifier = Modifier.clickable {
                        onItemSelected(item)
                        view.clearFocus()
                        value = item.toString()
                    }) {
                        itemContent(item)
                    }
                }
            }
        }
    }
}