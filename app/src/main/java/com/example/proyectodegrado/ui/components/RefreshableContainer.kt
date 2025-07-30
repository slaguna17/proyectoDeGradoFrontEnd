package com.example.proyectodegrado.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun RefreshableContainer(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val state = rememberSwipeRefreshState(isRefreshing = refreshing)
    SwipeRefresh(
        state = state,
        onRefresh = onRefresh,
        modifier = modifier
    ) {
        content()
    }
}

