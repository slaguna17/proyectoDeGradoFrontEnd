package com.example.proyectodegrado.ui.screens.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.Schedule
import com.example.proyectodegrado.data.model.ScheduleRequest


@Composable
fun ScheduleScreen(navController: NavController, viewModel: ScheduleViewModel){
    //State variables
    var schedules by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    //Dialog variables
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    //Create Schedule variables
    var newScheduleName by remember { mutableStateOf("") }
    var newScheduleLength by remember { mutableStateOf("") }
    var newScheduleStartTime by remember { mutableStateOf("") }
    var newScheduleEndTime by remember { mutableStateOf("") }

    //Edit and delete variables
    var scheduleToEdit by remember { mutableStateOf<Schedule?>(null) }
    var scheduleToDelete by remember { mutableStateOf<Schedule?>(null) }

    // Refresh function
    val refreshSchedule: () -> Unit = {
        viewModel.fetchSchedules(
            onSuccess = { schedules = viewModel.schedules.value },
            onError = { errorMessage = it }
        )
    }

    // Load schedules when initializing screeen
    LaunchedEffect(Unit) {
        refreshSchedule()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        //Dialogs
        CreateScheduleDialog(
            show = showCreateDialog,
            onDismiss = { showCreateDialog = false },
            onCreate = { name, length,start_time, end_time ->
                viewModel.createSchedule(
                    request = ScheduleRequest( name, length,start_time, end_time),
                    onSuccess = {
                        refreshSchedule()
                        newScheduleName = ""
                        newScheduleLength = ""
                        newScheduleStartTime = ""
                        newScheduleEndTime = ""
                    },
                    onError = {
                        errorMessage = it
                    }
                )
            },
            name = newScheduleName,
            onNameChange = {newScheduleName = it},
            length = newScheduleLength,
            onLengthChange = {newScheduleLength = it},
            start_time = newScheduleStartTime,
            onStartTimeChange = {newScheduleStartTime = it},
            end_time =  newScheduleEndTime,
            onEndTimeChange = {newScheduleEndTime = it},
        )
        EditScheduleDialog(
            show = showEditDialog,
            onDismiss = { showEditDialog = false },
            onEdit = {id, name, length,start_time, end_time ->
                if (scheduleToEdit != null) {
                    viewModel.updateSchedule(
                        id = id,
                        request = ScheduleRequest(name,length,start_time, end_time),
                        onSuccess = {
                            refreshSchedule()
                        },
                        onError = { errorMessage = it }
                    )

                }
            },
            schedule = scheduleToEdit
        )

        DeleteScheduleDialog(
            show = showDeleteDialog,
            onDismiss = { showDeleteDialog = false },
            onDelete = {
                if (scheduleToDelete != null) {
                    viewModel.deleteSchedule(
                        id = scheduleToDelete!!.id,
                        onSuccess = {
                            refreshSchedule()
                        },
                        onError = {
                            errorMessage = it
                        }
                    )
                }
            },
            schedule = scheduleToDelete
        )

        //Create Schedule
        Button(onClick = {
            showCreateDialog = true
        }) {
            Text("Crear Horario")
        }
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                modifier = Modifier.padding(16.dp)
            )
        } else if (schedules.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(schedules) { schedule ->
                    ScheduleItem(
                        schedule = schedule,
                        onEdit = {
                            scheduleToEdit = it
                            showEditDialog = true
                        },
                        onDelete = {
                            scheduleToDelete = it
                            showDeleteDialog = true
                        }
                    )
                }
            }
        } else {
            CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
fun ScheduleItem(
    schedule: Schedule,
    onEdit: (Schedule) -> Unit,
    onDelete: (Schedule) -> Unit
) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row (
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Column(
                Modifier.weight(1f)
            ) {
                Text(text = "ID: ${schedule.id}")
                Text(text = "Name: ${schedule.name}")
                Text(text = "Length: ${schedule.length}")
                Text(text = "Start time: ${schedule.start_time}")
                Text(text = "End time: ${schedule.end_time}")
            }
            Row {
                IconButton(onClick = { onEdit(schedule) }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar Horario")
                }
                IconButton(onClick = {onDelete(schedule)}) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar Horario")
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(2.dp))
}

@Composable
fun CreateScheduleDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    length:String,
    onLengthChange:(String) -> Unit,
    start_time: String,
    onStartTimeChange : (String) -> Unit,
    end_time:String,
    onEndTimeChange: (String) -> Unit,
    ) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("Crear Horario", style = MaterialTheme.typography.h6)
                    OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Nombre") })
                    OutlinedTextField(value = length, onValueChange = onLengthChange, label = { Text("Duracion en horas") })
                    OutlinedTextField(value = start_time, onValueChange = onStartTimeChange, label = { Text("Hora de inicio") })
                    OutlinedTextField(value = end_time, onValueChange = onEndTimeChange, label = { Text("Hora de finalizacion") })
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) {Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onCreate(name, length, start_time, end_time); onDismiss() }) {Text("Crear") }
                    }
                }
            }
        }
    }
}

@Composable
fun EditScheduleDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onEdit: (Int, String, String, String, String) -> Unit,
    schedule: Schedule?
) {
    if (show && schedule != null) {
        var editedName by remember { mutableStateOf(schedule.name) }
        var editedLength by remember { mutableStateOf(schedule.length) }
        var editedStartTime by remember { mutableStateOf(schedule.start_time) }
        var editedEndTime by remember { mutableStateOf(schedule.end_time) }

        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("Editar Horario", style = MaterialTheme.typography.h6)
                    OutlinedTextField(value = editedName, onValueChange = { editedName = it }, label = { Text("Nombre del horario") })
                    OutlinedTextField(value = editedLength, onValueChange = { editedLength = it }, label = { Text("Duracion en horas") })
                    OutlinedTextField(value = editedStartTime, onValueChange = { editedStartTime = it }, label = { Text("Hora de inicio") })
                    OutlinedTextField(value = editedEndTime, onValueChange = { editedEndTime = it }, label = { Text("HOra de finalizacion") })
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) {Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onEdit(schedule.id, editedName, editedLength,editedStartTime,editedEndTime); onDismiss() }) {Text("Guardar") }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteScheduleDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    schedule: Schedule?
) {
    if (show && schedule != null) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("¿Seguro que desea eliminar al horario: ${schedule.name}?", style = MaterialTheme.typography.h6)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) {Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onDelete(); onDismiss() }) {Text("Eliminar") }
                    }
                }
            }
        }
    }
}