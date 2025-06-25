package com.example.proyectodegrado.ui.components

import androidx.compose.runtime.Composable
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import androidx.compose.ui.Modifier

@Composable
fun RefreshableContainer(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val state = androidx.compose.runtime.remember { SwipeRefreshState(refreshing) }
    // Actualizar el estado cuando cambie refreshing
    state.isRefreshing = refreshing
    SwipeRefresh(
        state = state,
        onRefresh = onRefresh,
        modifier = modifier,
        content = content
    )
}
