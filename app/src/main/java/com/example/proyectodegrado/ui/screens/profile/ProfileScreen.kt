package com.example.proyectodegrado.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.proyectodegrado.ui.components.RefreshableContainer

@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    val accountPainter = rememberVectorPainter(Icons.Rounded.AccountCircle)

    LaunchedEffect(Unit) { viewModel.loadMe() }

    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) {
        uri: Uri? -> viewModel.onPickAvatar(uri)
    }

    Scaffold { innerpadding ->
        RefreshableContainer(
            refreshing = ui.loading,
            onRefresh = { viewModel.loadMe() },
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ---------- Avatar ----------
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(bottom = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.size(140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            val avatarModel: Any? = ui.avatarPreview ?: ui.avatarUrl

                            if (avatarModel != null) {
                                AsyncImage(
                                    model = avatarModel,
                                    contentDescription = "Avatar",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.matchParentSize(),
                                    placeholder = accountPainter,
                                    error = accountPainter,
                                    fallback = accountPainter
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Rounded.AccountCircle,
                                    contentDescription = "Avatar",
                                    modifier = Modifier
                                        .matchParentSize()
                                        .padding(8.dp)
                                )
                            }
                        }

                        SmallFloatingActionButton(
                            onClick = { pickImage.launch("image/*") },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 6.dp, y = 6.dp),
                            containerColor = MaterialTheme.colorScheme.surface,
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Cambiar foto")
                        }
                    }
                }

                if (ui.error != null) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = ui.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.height(20.dp))

                // ---------- Campos ----------
                OutlinedTextField(
                    value = ui.fullName,
                    onValueChange = viewModel::onFullNameChange,
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = ui.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Correo electrónico") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    value = ui.phone,
                    onValueChange = viewModel::onPhoneChange,
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(Modifier.height(22.dp))

                // ---------- Guardar ----------
                Button(
                    onClick = { viewModel.save() },
                    enabled = ui.hasChanges && !ui.loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (ui.loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Guardar")
                    }
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}
