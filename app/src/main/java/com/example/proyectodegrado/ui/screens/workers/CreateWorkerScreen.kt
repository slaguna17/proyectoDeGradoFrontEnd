package com.example.proyectodegrado.ui.screens.workers

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectodegrado.R
import com.example.proyectodegrado.data.model.RegisterWorkerRequest
import com.example.proyectodegrado.ui.components.ScheduleDropdown
import com.example.proyectodegrado.ui.components.StoreDropdown
import com.example.proyectodegrado.ui.screens.register.RoleDropdown
import com.example.proyectodegrado.ui.screens.register.RegisterViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWorkerScreen(
    navController: NavController,
    workersViewModel: WorkersViewModel,
    registerViewModel: RegisterViewModel // reutilizamos el VM de Register para UI/validaciones/roles/avatar
) {
    // ---- Estados del VM de registro (UI unificada con RegisterScreen) ----
    val ui by registerViewModel.ui.collectAsStateWithLifecycle()
    val roles by registerViewModel.roles.collectAsStateWithLifecycle()

    // ---- Catálogos de empleados (tiendas/horarios) ----
    val stores by workersViewModel.stores.collectAsStateWithLifecycle()
    val schedules by workersViewModel.schedules.collectAsStateWithLifecycle()

    var selectedStoreId by remember { mutableStateOf<Int?>(null) }
    var selectedScheduleId by remember { mutableStateOf<Int?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Carga inicial: roles + catálogos de empleados
    LaunchedEffect(Unit) {
        registerViewModel.loadRoles()
        workersViewModel.refreshAll()
    }

    // ---- Image Picker (como en RegisterScreen) ----
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        registerViewModel.onPickAvatar(uri)
        if (uri != null) registerViewModel.uploadAvatarIfNeeded()
    }

    // ---- DatePicker (Material 3) ----
    var showDatePicker by remember { mutableStateOf(false) }
    val birthText = ui.dateOfBirth

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
            modifier = Modifier.size(200.dp)
        )
        Text("Nuevo Empleado", fontSize = 24.sp)

        if (ui.uploading) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(16.dp))

        // Campos (mismos que RegisterScreen)
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
            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        // Fecha de nacimiento (abre DatePicker)
        OutlinedTextField(
            value = birthText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha de nacimiento") },
            trailingIcon = {
                TextButton(onClick = { showDatePicker = true }) { Text("Elegir") }
            },
            modifier = Modifier.fillMaxWidth()
        )
        if (showDatePicker) {
            val today = remember { Date().time }
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = today)
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

        // Roles (usando tu componente)
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

        // Selección de tienda y turno (propios de empleados)
        StoreDropdown(
            stores = stores,
            selectedStoreId = selectedStoreId,
            onStoreSelected = { selectedStoreId = it }
        )
        Spacer(Modifier.height(8.dp))
        ScheduleDropdown(
            schedules = schedules,
            selectedScheduleId = selectedScheduleId,
            onScheduleSelected = { selectedScheduleId = it }
        )
        Spacer(Modifier.height(12.dp))

        // Errores
        (ui.error ?: errorMessage)?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        // Avatar + botón fuera del círculo (como ajustamos en Profile)
        Box(
            modifier = Modifier
                .wrapContentSize()
                .padding(bottom = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.size(140.dp), contentAlignment = Alignment.Center) {
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
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        )
                    }
                }
                SmallFloatingActionButton(
                    onClick = { pickImage.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = 6.dp, y = 6.dp)
                ) { Icon(Icons.Filled.Edit, contentDescription = "Cambiar foto") }
            }
        }

        // Botón Registrar
        Button(
            onClick = {
                errorMessage = null

                // Validaciones adicionales (tienda/horario)
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

                // Construye el request que ya acepta tu repositorio de Workers
                val req = RegisterWorkerRequest(
                    username = ui.username,
                    email = ui.email,
                    password = ui.password,
                    fullName = ui.fullName,
                    phone = ui.phone,
                    storeId = selectedStoreId!!,
                    scheduleId = selectedScheduleId!!,
                    roleId = ui.roleId!!
                    // Si tu backend ya admite más campos (dateOfBirth, avatarKey), puedes extender el modelo.
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
