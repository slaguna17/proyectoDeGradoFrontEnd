package com.example.proyectodegrado.ui.screens.providers

import com.example.proyectodegrado.ui.screens.store.StoreViewModel
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.proyectodegrado.data.model.Provider
import com.example.proyectodegrado.data.model.ProviderRequest
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.StoreRequest
import com.example.proyectodegrado.di.AppPreferences
import com.example.proyectodegrado.ui.components.Header
import com.example.proyectodegrado.ui.components.uploadImage

@Composable
fun ProvidersScreen(navController: NavController, viewModel: ProvidersViewModel){
    //State variables
    var providers by remember { mutableStateOf<List<Provider>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    //Dialog variables
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    //Create Provider variables
    var newProviderName by remember { mutableStateOf("") }
    var newProviderAddress by remember { mutableStateOf("") }
    var newProviderEmail by remember { mutableStateOf("") }
    var newProviderPhone by remember { mutableStateOf("") }
    var newProviderContactName by remember { mutableStateOf("") }
    var newProviderNotes by remember { mutableStateOf("") }

    //Edit and delete variables
    var providerToEdit by remember { mutableStateOf<Provider?>(null) }
    var providerToDelete by remember { mutableStateOf<Provider?>(null) }

    // Refresh function
    val refreshProviders: () -> Unit = {
        viewModel.fetchProviders(
            onSuccess = { providers = viewModel.providers.value },
            onError = { errorMessage = it }
        )
    }

    // Load providers when initializing screeen
    LaunchedEffect(Unit) {
        refreshProviders()
    }

    Scaffold(
        topBar = { Header(navController = navController, title = "Proveedores")},
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                //Dialogs
                CreateProviderDialog(
                    show = showCreateDialog,
                    onDismiss = { showCreateDialog = false },
                    onCreate = { name, address,email, phone, contactName, notes ->
                        viewModel.createProvider(
                            request = ProviderRequest( name, address, email, phone, contactName, notes),
//                            socials)
                            onSuccess = {
                                refreshProviders()
                                newProviderName = ""
                                newProviderAddress = ""
                                newProviderEmail = ""
                                newProviderPhone = ""
                                newProviderContactName = ""
                                newProviderNotes = ""
                            },
                            onError = {
                                errorMessage = it
                            }
                        )
                    },
                    name = newProviderName,
                    onNameChange = {newProviderName = it},
                    address = newProviderAddress,
                    onAddressChange = {newProviderAddress = it},
                    email = newProviderEmail,
                    onEmailChange = {newProviderEmail = it},
                    phone = newProviderPhone,
                    onPhoneChange = {newProviderPhone = it},
                    contactName = newProviderContactName,
                    onContactNameChange = {newProviderContactName = it},
                    notes = newProviderNotes,
                    onNotesChange = {newProviderNotes = it},
                )
                EditProviderDialog(
                    show = showEditDialog,
                    onDismiss = { showEditDialog = false },
                    onEdit = {id, name, address,email, phone, contactName, notes ->
                        if (providerToEdit != null) {
                            viewModel.updateProvider(
                                id = id,
                                request = ProviderRequest(name, address,email, phone, contactName, notes),
                                onSuccess = {
                                    refreshProviders()
                                },
                                onError = { errorMessage = it }
                            )

                        }
                    },
                    provider = providerToEdit
                )

                DeleteProviderDialog(
                    show = showDeleteDialog,
                    onDismiss = { showDeleteDialog = false },
                    onDelete = {
                        if (providerToDelete != null) {
                            viewModel.deleteProvider(
                                id = providerToDelete!!.id,
                                onSuccess = {
                                    refreshProviders()
                                },
                                onError = {
                                    errorMessage = it
                                }
                            )
                        }
                    },
                    provider = providerToDelete
                )

                //Create Provider
                Button(onClick = {
                    showCreateDialog = true
                }) {
                    Text("Crear Proveedor")
                }
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp)
                    )
                } else if (providers.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(providers) { provider ->
                            ProviderItem(
                                provider = provider,
                                onEdit = {
                                    providerToEdit = it
                                    showEditDialog = true
                                },
                                onDelete = {
                                    providerToDelete = it
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
    )
}

@Composable
fun ProviderItem(
    provider: Provider,
    onEdit: (Provider) -> Unit,
    onDelete: (Provider) -> Unit
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
                Text(text = "ID: ${provider.id}")
                Text(text = "Name: ${provider.name}")
                Text(text = "Address: ${provider.address}")
                Text(text = "Email: ${provider.email}")
                Text(text = "Phone: ${provider.phone}")
                Text(text = "Contact: ${provider.contact_person_name}")
                Text(text = "Notes: ${provider.notes}")
            }
            Row {
                IconButton(onClick = { onEdit(provider) }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar Proveedor")
                }
                IconButton(onClick = {onDelete(provider)}) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar Proveedor")
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(2.dp))
}

@Composable
fun CreateProviderDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, String, String, String, String, String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    address:String,
    onAddressChange:(String) -> Unit,
    email: String,
    onEmailChange : (String) -> Unit,
    phone:String,
    onPhoneChange: (String) -> Unit,
    contactName : String,
    onContactNameChange :(String) -> Unit,
    notes:String,
    onNotesChange:(String) -> Unit,

) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("Crear Proveedor", style = MaterialTheme.typography.h6)
                    OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Nombre (Empresa)") })
                    OutlinedTextField(value = address, onValueChange = onAddressChange, label = { Text("Direccion") })
                    OutlinedTextField(value = email, onValueChange = onEmailChange, label = { Text("Correo Electronico") })
                    OutlinedTextField(value = phone, onValueChange = onPhoneChange, label = { Text("Telefono") })
                    OutlinedTextField(value = contactName, onValueChange = onContactNameChange, label = { Text("Nombre de la persona del contacto") })
                    OutlinedTextField(value = notes, onValueChange = onNotesChange, label = { Text("Notas") })
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) {Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onCreate(name, address,email, phone, contactName, notes); onDismiss() }) {Text("Crear") }
                    }
                }
            }
        }
    }
}

@Composable
fun EditProviderDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onEdit: (Int, String, String, String, String, String, String) -> Unit,
    provider: Provider?
) {
    if (show && provider != null) {
        var editedName by remember { mutableStateOf(provider.name) }
        var editedAddress by remember { mutableStateOf(provider.address) }
        var editedEmail by remember { mutableStateOf(provider.email) }
        var editedPhone by remember { mutableStateOf(provider.phone) }
        var editedContactName by remember { mutableStateOf(provider.contact_person_name) }
        var editedNotes by remember { mutableStateOf(provider.notes) }


        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("Editar Proveedor", style = MaterialTheme.typography.h6)
                    OutlinedTextField(value = editedName, onValueChange = { editedName = it }, label = { Text("Nombre (de la empresa)") })
                    OutlinedTextField(value = editedAddress, onValueChange = { editedAddress = it }, label = { Text("Direccion") })
                    OutlinedTextField(value = editedEmail, onValueChange = { editedEmail = it }, label = { Text("Email") })
                    OutlinedTextField(value = editedPhone, onValueChange = { editedPhone = it }, label = { Text("Telefono") })
                    OutlinedTextField(value = editedContactName, onValueChange = { editedContactName = it }, label = { Text("Nombre de la persona del contacto") })
                    uploadImage(buttonText = "Elegir logo de la tienda")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) {Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onEdit(provider.id, editedName, editedAddress,editedEmail,editedPhone,editedContactName,editedNotes); onDismiss() }) {Text("Guardar") }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteProviderDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    provider: Provider?
) {
    if (show && provider != null) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("¿Seguro que desea eliminar al proveedor: ${provider.name}?", style = MaterialTheme.typography.h6)
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