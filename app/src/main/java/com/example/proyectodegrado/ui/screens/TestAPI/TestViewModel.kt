package com.example.proyectodegrado.ui.screens.TestAPI

import com.example.proyectodegrado.data.api.UserService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.api.RetrofitClient
import com.example.proyectodegrado.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val userService: UserService = RetrofitClient.createService(UserService::class.java)
    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users

    fun fetchUsers() {
        viewModelScope.launch {
            try {
                val userList = userService.getAllUsers()
                _users.value = userList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}