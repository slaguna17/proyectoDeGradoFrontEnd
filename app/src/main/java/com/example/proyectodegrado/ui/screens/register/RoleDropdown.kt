package com.example.proyectodegrado.ui.screens.register

import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.proyectodegrado.data.model.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleDropdown(
    viewModel: RegisterViewModel,
    roles: List<Role>,
    selectedRole: Role?,
    onRoleSelected: (Role) -> Unit,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedText by remember { mutableStateOf(selectedRole?.name ?: "") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            label = { Text("Rol") },
            placeholder = { Text("Selecciona un rol") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            isError = isError,
            enabled = roles.isNotEmpty()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            roles.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role.name) },
                    onClick = {
                        selectedText = role.name
                        onRoleSelected(role)
                        viewModel.setSelectedRoleId(role.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
