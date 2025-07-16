package com.example.proyectodegrado.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.proyectodegrado.data.model.Store

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDropdown(
    stores: List<Store>,
    selectedStoreId: Int?,
    onStoreSelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedName = stores.firstOrNull { it.id == selectedStoreId }?.name ?: "Todas las tiendas"
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Filtrar por tienda") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Todas las tiendas") },
                onClick = {
                    onStoreSelected(null)
                    expanded = false
                }
            )
            stores.forEach { store ->
                DropdownMenuItem(
                    text = { Text(store.name) },
                    onClick = {
                        onStoreSelected(store.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
