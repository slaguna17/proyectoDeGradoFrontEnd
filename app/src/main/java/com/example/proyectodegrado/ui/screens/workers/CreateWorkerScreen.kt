package com.example.proyectodegrado.ui.screens.workers

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectodegrado.R
import com.example.proyectodegrado.data.model.RegisterWorkerRequest
import com.example.proyectodegrado.ui.components.ScheduleDropdown
import com.example.proyectodegrado.ui.components.StoreDropdown
import com.example.proyectodegrado.ui.screens.register.RoleDropdown
import com.example.proyectodegrado.ui.screens.register.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkerScreen(
    navController: NavController,
    workersViewModel: WorkersViewModel,
    registerViewModel: RegisterViewModel
) {
    // ---- State ----
    val ui by registerViewModel.ui.collectAsStateWithLifecycle()
    val roles by registerViewModel.roles.collectAsStateWithLifecycle()
    val stores by workersViewModel.stores.collectAsStateWithLifecycle()
    val schedules by workersViewModel.schedules.collectAsStateWithLifecycle()

    var selectedStoreId by remember { mutableStateOf<Int?>(null) }
    var selectedScheduleId by remember { mutableStateOf<Int?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // ---- DatePicker (Material 3) ----
    var showDatePicker by remember { mutableStateOf(false) }
    val birthText = ui.dateOfBirth

    // ---- Image Picker ----
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        registerViewModel.onPickAvatar(uri)
        if (uri != null) registerViewModel.uploadAvatarIfNeeded()
    }

    LaunchedEffect(Unit) {
        registerViewModel.loadRoles()
        workersViewModel.refreshAll()
    }

    // ---- UI ----
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 70.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Image(
            painter = painterResource(id = R.drawable.logonobackground),
            contentDescription = "Logo",
            modifier = Modifier.size(150.dp).padding(top = 24.dp)
        )
        Text("Nuevo Empleado",  style = MaterialTheme.typography.headlineLarge)

        if (ui.uploading) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = ui.fullName,
            onValueChange = registerViewModel::onFullName,
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = ui.username,
            onValueChange = registerViewModel::onUsername,
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = ui.email,
            onValueChange = registerViewModel::onEmail,
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = ui.phone,
            onValueChange = registerViewModel::onPhone,
            label = { Text("Teléfono") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        // DatePicker
        OutlinedTextField(
            value = ui.dateOfBirth,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha de nacimiento") },
            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Elegir fecha")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { showDatePicker = true }
        )
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        registerViewModel.onDatePicked(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    }) { Text("Aceptar") }
                },
                dismissButton = { TextButton({ showDatePicker = false }) { Text("Cancelar") } }
            ) { DatePicker(state = datePickerState) }
        }
        Spacer(Modifier.height(10.dp))

        RoleDropdown(
            roles = roles,
            selectedRoleId = ui.roleId,
            onRoleSelected = registerViewModel::onRoleId,
            modifier = Modifier.fillMaxWidth(),
            label = "Rol del empleado"
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = ui.password,
            onValueChange = registerViewModel::onPassword,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = ui.confirmPassword,
            onValueChange = registerViewModel::onConfirmPassword,
            label = { Text("Repetir contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        StoreDropdown(
            stores = stores,
            selectedStoreId = selectedStoreId,
            onStoreSelected = { selectedStoreId = it }
        )
        Spacer(Modifier.height(10.dp))

        ScheduleDropdown(
            schedules = schedules,
            selectedScheduleId = selectedScheduleId,
            onScheduleSelected = { selectedScheduleId = it }
        )
        Spacer(Modifier.height(10.dp))

        // Errors
        (ui.error ?: errorMessage)?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

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
                    if (ui.avatarPreview != null) {
                        AsyncImage(
                            model = ui.avatarPreview,
                            contentDescription = "Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                SmallFloatingActionButton(
                    onClick = { pickImage.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 6.dp, y = 6.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    Icon(Icons.Filled.Edit, contentDescription = "Cambiar foto")
                }
            }
        }
        Spacer(Modifier.height(18.dp))

        // Submit
        Button(
            onClick = {
                errorMessage = null
                when {
                    ui.password != ui.confirmPassword -> {
                        errorMessage = "Las contraseñas no coinciden"; return@Button
                    }
                    ui.roleId == null -> {
                        errorMessage = "Selecciona un rol"; return@Button
                    }
                    selectedStoreId == null -> {
                        errorMessage = "Selecciona una tienda"; return@Button
                    }
                    selectedScheduleId == null -> {
                        errorMessage = "Selecciona un horario"; return@Button
                    }
                }

                val req = RegisterWorkerRequest(
                    username = ui.username,
                    email = ui.email,
                    password = ui.password,
                    fullName = ui.fullName,
                    phone = ui.phone,
                    storeId = selectedStoreId!!,
                    scheduleId = selectedScheduleId!!,
                    roleId = ui.roleId!!
                )

                workersViewModel.registerWorker(
                    request = req,
                    onSuccess = { navController.navigate("workers") },
                    onError = { errorMessage = it }
                )
            },
            enabled = !ui.loading && !ui.uploading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (ui.loading) {
                CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
            } else {
                Text("Registrar empleado")
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
