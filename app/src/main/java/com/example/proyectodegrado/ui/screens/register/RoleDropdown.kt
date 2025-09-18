package com.example.proyectodegrado.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.proyectodegrado.data.model.Role

/**
 * Dropdown de roles con Material3.
 *
 * @param roles            Lista de roles a mostrar (id + name).
 * @param selectedRoleId   Id del rol actualmente seleccionado (nullable).
 * @param onRoleSelected   Callback con el id seleccionado (nullable).
 * @param modifier         Modificador opcional.
 * @param label            Texto de la etiqueta del TextField.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleDropdown(
    roles: List<Role>,
    selectedRoleId: Int?,
    onRoleSelected: (Int?) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Rol"
) {
    var expanded by remember { mutableStateOf(false) }

    // Texto visible en el TextField según id seleccionado
    val selectedText = remember(roles, selectedRoleId) {
        roles.firstOrNull { it.id == selectedRoleId }?.name ?: ""
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = { /* readOnly */ },
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            roles.forEach { role ->
                DropdownMenuItem(
                    text = { Text(role.name) },
                    onClick = {
                        expanded = false
                        onRoleSelected(role.id)
                    }
                )
            }

            // Opción para "limpiar" selección (opcional)
            if (roles.isNotEmpty()) {
                DropdownMenuItem(
                    text = { Text("— Sin rol —") },
                    onClick = {
                        expanded = false
                        onRoleSelected(null)
                    }
                )
            }
        }
    }
}
