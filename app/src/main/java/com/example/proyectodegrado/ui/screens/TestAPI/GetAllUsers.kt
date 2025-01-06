package com.example.proyectodegrado.ui.screens.TestAPI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectodegrado.data.model.User


@Composable
fun GetAllUsers(){
    val viewModel : UserViewModel = viewModel()
    val users by viewModel.users.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Button(
            onClick = { viewModel.fetchUsers() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Fetch Users")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(users) { user ->
                UserRow(user)
            }
        }
    }
}
@Composable
fun UserRow(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("Username: ${user.username}")
        Text("Full Name: ${user.full_name}")
        Text("Email: ${user.email}")
        Spacer(modifier = Modifier.height(8.dp))
    }
}