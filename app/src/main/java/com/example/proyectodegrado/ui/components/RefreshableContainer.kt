package com.example.proyectodegrado.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RefreshableContainer(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    // 1. El estado ahora se crea así, vinculando `refreshing` y `onRefresh` directamente.
    val pullRefreshState = rememberPullRefreshState(refreshing, onRefresh)

    // 2. No se usa un `PullToRefreshBox`, sino un `Box` normal con un modificador.
    Box(
        modifier = modifier
            .pullRefresh(pullRefreshState)
    ) {
        // El contenido que le pases al contenedor va aquí.
        content()

        // 3. El indicador de carga es un componente separado que se alinea dentro del Box.
        PullRefreshIndicator(
            refreshing = refreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}