package com.example.proyectodegrado.ui.screens.register

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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectodegrado.R
import java.text.SimpleDateFormat
import java.util.*
import com.example.proyectodegrado.di.AppPreferences
import com.example.proyectodegrado.di.DependencyProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    navController: NavController
) {
    val ui by viewModel.ui.collectAsState()
    val roles by viewModel.roles.collectAsState()
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }
    val authState by viewModel.authState.observeAsState()

    LaunchedEffect(authState) {
        authState?.let { result ->
            if (result.isSuccess) {
                val resp = result.getOrNull()!!
                val user = resp.user
                val userId = user.id

                // Guarda datos mínimos como en LoginScreen
                prefs.saveUserId(userId.toString())
                prefs.saveUserName(user.username?.ifBlank { user.fullName })

                val storeId = prefs.getStoreId()?.toIntOrNull() ?: 1
                DependencyProvider.setCurrentSession(userId = userId, storeId = storeId)

                navController.navigate("home") {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    val logo = painterResource(R.drawable.logonobackground)

    // ---- Image Picker ----
    val pickImage = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        viewModel.onPickAvatar(uri)
        if (uri != null) viewModel.uploadAvatarIfNeeded()
    }

    // ---- Date Picker (Material3) ----
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFmt = remember { SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH) }
    val birthText = if (ui.dateOfBirth.isBlank()) "" else ui.dateOfBirth

    LaunchedEffect(Unit) { viewModel.loadRoles() }

    // ---- UI ----
    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 70.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(painter = logo, contentDescription = "Main Logo", modifier = Modifier.size(200.dp))
        Text(text = "¡Registrate!", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        if (ui.uploading) {
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = ui.fullName,
            onValueChange = viewModel::onFullName,
            label = { Text("Nombre completo") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = ui.username,
            onValueChange = viewModel::onUsername,
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = ui.email,
            onValueChange = viewModel::onEmail,
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = ui.phone,
            onValueChange = viewModel::onPhone,
            label = { Text("Teléfono") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        // Fecha de nacimiento (abre calendario)
        OutlinedTextField(
            value = birthText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Fecha de nacimiento") },
            placeholder = { Text("dd/MM/yyyy") },
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
        Spacer(Modifier.height(10.dp))

        // ---- DatePickerDialog ----
        if (showDatePicker) {
            val today = remember { Date().time }
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = today)
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.onDatePicked(datePickerState.selectedDateMillis)
                        showDatePicker = false
                    }) { Text("Aceptar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        RoleDropdown(
            roles = roles,
            selectedRoleId = ui.roleId,
            onRoleSelected = viewModel::onRoleId,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = ui.password,
            onValueChange = viewModel::onPassword,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = ui.confirmPassword,
            onValueChange = viewModel::onConfirmPassword,
            label = { Text("Repetir contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (ui.error != null) {
            Spacer(Modifier.height(8.dp))
            Text(ui.error!!, color = MaterialTheme.colorScheme.error)
        }
        if (ui.successMsg != null) {
            Spacer(Modifier.height(8.dp))
            Text(ui.successMsg!!, color = MaterialTheme.colorScheme.primary)
        }

        // Avatar (optional)
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            if (ui.avatarPreview == null) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(40.dp)
                )
            } else {
                AsyncImage(
                    model = ui.avatarPreview,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            SmallFloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(6.dp, 6.dp),
                onClick = { pickImage.launch("image/*") },
            ) { Icon(Icons.Filled.Edit, contentDescription = null) }
        }

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = {
                if (ui.uploading) return@Button
                viewModel.register()
            },
            enabled = !ui.loading && !ui.uploading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (ui.loading) {
                CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
            } else Text("Crear cuenta")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Text(text = "¿Tienes cuenta? ")
            Text(
                text = "¡Ingresa ahora!",
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    navController.navigate("login")
                }
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
