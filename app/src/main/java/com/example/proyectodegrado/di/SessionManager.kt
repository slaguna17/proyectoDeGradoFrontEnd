package com.example.proyectodegrado.di

import com.example.proyectodegrado.data.model.MenuItemDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SessionState(
    val userId: Int? = null,
    val storeId: Int? = null,
    val isAdmin: Boolean = false,
    val menu: List<MenuItemDTO> = emptyList(),
    val authToken: String? = null
)

class SessionManager(private val preferences: AppPreferences) {

    private val _sessionState = MutableStateFlow(SessionState())
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    fun initialize() {
        val userId = preferences.getUserId()?.toIntOrNull()
        val storeId = preferences.getStoreId()?.toIntOrNull()
        val token = preferences.getAuthToken()

        if (userId != null && storeId != null && token != null) {
            val isAdmin = preferences.getIsAdmin()
            val savedMenu = preferences.getMenu()
            _sessionState.value = SessionState(
                userId = userId,
                storeId = storeId,
                isAdmin = isAdmin,
                menu = savedMenu,
                authToken = token
            )
        }
    }

    fun saveCurrentSession(
        userId: Int,
        storeId: Int,
        isAdmin: Boolean,
        userName: String?,
        menu: List<MenuItemDTO>,
        token: String
    ) {
        _sessionState.value = SessionState(userId, storeId, isAdmin, menu, authToken = token)
        preferences.saveUserId(userId.toString())
        preferences.saveStoreId(storeId.toString())
        preferences.saveIsAdmin(isAdmin)
        preferences.saveUserName(userName)
        preferences.saveAuthToken(token)
        preferences.saveMenu(menu)
    }

    fun setTemporarySession(
        userId: Int,
        storeId: Int,
        isAdmin: Boolean,
        menu: List<MenuItemDTO>
    ) {
        _sessionState.value = SessionState(userId, storeId, isAdmin, menu)
    }

    fun clearCurrentSession() {
        preferences.clearSession()
        _sessionState.value = SessionState()
    }

    fun getAuthToken(): String? = _sessionState.value.authToken

    fun isLoggedIn(): Boolean = _sessionState.value.authToken != null

    fun getCurrentStoreId(): Int = _sessionState.value.storeId ?: 1
    fun getCurrentUserId(): Int = _sessionState.value.userId ?: 1
    fun getCurrentMenu(): List<MenuItemDTO> = _sessionState.value.menu

    fun updateMenu(newMenu: List<MenuItemDTO>) {
        val s = _sessionState.value
        _sessionState.value = s.copy(menu = newMenu)
        preferences.saveMenu(newMenu)
    }
}
