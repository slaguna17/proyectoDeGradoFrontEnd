package com.example.proyectodegrado.ui.screens.workers

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
import com.example.proyectodegrado.data.model.Schedule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDropdown(
    schedules: List<Schedule>,
    selectedScheduleId: Int?,
    onScheduleSelected: (Int?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedSchedule = schedules.firstOrNull { it.id == selectedScheduleId }
    val selectedName = selectedSchedule?.let { "${it.name} (${it.startTime} - ${it.endTime})" } ?: "Selecciona turno"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Turno") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            schedules.forEach { schedule ->
                DropdownMenuItem(
                    text = { Text("${schedule.name} (${schedule.startTime} - ${schedule.endTime})") },
                    onClick = {
                        onScheduleSelected(schedule.id)
                        expanded = false
                    }
                )
            }
        }
    }
}