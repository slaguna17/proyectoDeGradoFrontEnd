package com.example.proyectodegrado.ui.screens.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class StoreOption(val id: Int, val name: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreFilterBar(
    stores: List<StoreOption>,
    selectedStoreId: Int?,
    onStoreSelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            val selectedName = stores.firstOrNull { it.id == selectedStoreId }?.name ?: "Todas"
            OutlinedTextField(
                modifier = Modifier.menuAnchor(),
                value = selectedName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tienda") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Todas") },
                    onClick = {
                        expanded = false
                        onStoreSelected(null)
                    }
                )
                stores.forEach { s ->
                    DropdownMenuItem(
                        text = { Text(s.name) },
                        onClick = {
                            expanded = false
                            onStoreSelected(s.id)
                        }
                    )
                }
            }
        }
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
}
