package com.example.proyectodegrado.ui.screens.session

import androidx.lifecycle.ViewModel
import com.example.proyectodegrado.di.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SessionUiState(
    val isAdmin: Boolean = false
)

class SessionViewModel(prefs: AppPreferences) : ViewModel() {
    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.value = SessionUiState(isAdmin = prefs.getIsAdmin())
    }
}