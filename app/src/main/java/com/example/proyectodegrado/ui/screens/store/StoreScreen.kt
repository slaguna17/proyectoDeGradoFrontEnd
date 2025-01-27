package com.example.proyectodegrado.ui.screens.store

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
import com.example.proyectodegrado.data.model.Store
import com.example.proyectodegrado.data.model.StoreRequest
import com.example.proyectodegrado.ui.components.Header
import com.example.proyectodegrado.ui.components.uploadImage

@Composable
fun StoreScreen(navController: NavController, viewModel: StoreViewModel){
    //State variables
    var stores by remember { mutableStateOf<List<Store>>(emptyList()) }
    var errorMessage by remember { mutableStateOf("") }

    //Dialog variables
    var showCreateDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    //Create Store variables
    var newStoreName by remember { mutableStateOf("") }
    var newStoreAddress by remember { mutableStateOf("") }
    var newStoreCity by remember { mutableStateOf("") }
    var newStoreLogo by remember { mutableStateOf("") }
    var newStoreHistory by remember { mutableStateOf("") }
    var newStorePhone by remember { mutableStateOf("") }
//    var newStoreSocials by remember { mutableStateListOf(emptyList()) }

    //Edit and delete variables
    var storeToEdit by remember { mutableStateOf<Store?>(null) }
    var storeToDelete by remember { mutableStateOf<Store?>(null) }

    // Refresh function
    val refreshStores: () -> Unit = {
        viewModel.fetchStores(
            onSuccess = { stores = viewModel.stores.value },
            onError = { errorMessage = it }
        )
    }
    // Load stores when initializing screeen
    LaunchedEffect(Unit) {
        refreshStores()
    }

    Scaffold(
        topBar = { Header(navController = navController)},
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                //Dialogs
                CreateStoreDialog(
                    show = showCreateDialog,
                    onDismiss = { showCreateDialog = false },
                    onCreate = { name, address, city, logo, history, phone ->
                        //    socials ->
                        viewModel.createStore(
                            request = StoreRequest( name, address, city, logo, history, phone),
//                            socials)
                            onSuccess = {
                                refreshStores()
                                newStoreName = ""
                                newStoreAddress = ""
                                newStoreCity = ""
                                newStoreLogo = ""
                                newStoreHistory = ""
                                newStorePhone = ""
                            //    newStoreSocials = emptyList()
                            },
                            onError = {
                                errorMessage = it
                            }
                        )
                    },
                    name = newStoreName,
                    onNameChange = {newStoreName = it},
                    address = newStoreAddress,
                    onAddressChange = {newStoreAddress = it},
                    city = newStoreCity,
                    onCityChange = {newStoreCity = it},
                    logo = newStoreLogo,
                    onLogoChange = {newStoreLogo = it},
                    history = newStoreHistory,
                    onHistoryChange = {newStoreHistory = it},
                    phone = newStorePhone,
                    onPhoneChange = {newStorePhone = it}
                )
                EditStoreDialog(
                    show = showEditDialog,
                    onDismiss = { showEditDialog = false },
                    onEdit = {id, name, address, city, logo, history, phone ->
                        if (storeToEdit != null) {
                            viewModel.updateStore(
                                id = id,
                                request = StoreRequest(name, address, city, logo, history, phone),
                                onSuccess = {
                                    refreshStores()
                                },
                                onError = { errorMessage = it }
                            )

                        }
                    },
                    store = storeToEdit
                )
                DeleteStoreDialog(
                    show = showDeleteDialog,
                    onDismiss = { showDeleteDialog = false },
                    onDelete = {
                        if (storeToDelete != null) {
                            viewModel.deleteStore(
                                id = storeToDelete!!.id,
                                onSuccess = {
                                    refreshStores()
                                },
                                onError = {
                                    errorMessage = it
                                }
                            )
                        }
                    },
                    store = storeToDelete
                )
                //Create Store
                Button(onClick = {
                    showCreateDialog = true
                }) {
                    Text("Crear Tienda")
                }
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        modifier = Modifier.padding(16.dp)
                    )
                } else if (stores.isNotEmpty()) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(stores) { store ->
                            StoreItem(
                                store = store,
                                onEdit = {
                                    storeToEdit = it
                                    showEditDialog = true
                                },
                                onDelete = {
                                    storeToDelete = it
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
fun StoreItem(
    store: Store,
    onEdit: (Store) -> Unit,
    onDelete: (Store) -> Unit
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
                Text(text = "ID: ${store.id}")
                Text(text = "Name: ${store.name}")
                Text(text = "Address: ${store.address}")
                Text(text = "City: ${store.city}")
                Text(text = "Logo: ${store.logo}")
                Text(text = "History: ${store.history}")
                Text(text = "Phone: ${store.phone}")
            }
            Row {
                IconButton(onClick = { onEdit(store) }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar tienda")
                }
                IconButton(onClick = {onDelete(store)}) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar tienda")
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(2.dp))
}

@Composable
fun CreateStoreDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    //name, address,city,logo,history,phone
    onCreate: (String, String, String, String, String, String) -> Unit,
    name: String,
    onNameChange: (String) -> Unit,
    address:String,
    onAddressChange:(String) -> Unit,
    city: String,
    onCityChange : (String) -> Unit,
    logo : String,
    onLogoChange :(String) -> Unit,
    history:String,
    onHistoryChange:(String) -> Unit,
    phone:String,
    onPhoneChange: (String) -> Unit
) {
    if (show) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("Crear Tienda", style = MaterialTheme.typography.h6)
                    OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Nombre") })
                    OutlinedTextField(value = address, onValueChange = onAddressChange, label = { Text("Direccion") })
                    OutlinedTextField(value = city, onValueChange = onCityChange, label = { Text("Ciudad") })
                    OutlinedTextField(value = history, onValueChange = onHistoryChange, label = { Text("Historia") })
                    OutlinedTextField(value = phone, onValueChange = onPhoneChange, label = { Text("Telefono") })
                    uploadImage(buttonText = "Elegir logo de la tienda")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) {Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onCreate(name, address,city, logo, history, phone); onDismiss() }) {Text("Crear") }
                    }
                }
            }
        }
    }
}

@Composable
fun EditStoreDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onEdit: (Int, String, String, String, String, String, String) -> Unit,
    store: Store?
) {
    if (show && store != null) {
        var editedName by remember { mutableStateOf(store.name) }
        var editedAddress by remember { mutableStateOf(store.address) }
        var editedCity by remember { mutableStateOf(store.city) }
        var editedLogo by remember { mutableStateOf(store.logo) }
        var editedHistory by remember { mutableStateOf(store.history) }
        var editedPhone by remember { mutableStateOf(store.phone) }


        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("Editar Tienda", style = MaterialTheme.typography.h6)
                    OutlinedTextField(value = editedName, onValueChange = { editedName = it }, label = { Text("Nombre") })
                    OutlinedTextField(value = editedAddress, onValueChange = { editedAddress = it }, label = { Text("Direccion") })
                    OutlinedTextField(value = editedCity, onValueChange = { editedCity = it }, label = { Text("Ciudad") })
                    OutlinedTextField(value = editedHistory, onValueChange = { editedHistory = it }, label = { Text("Historia") })
                    OutlinedTextField(value = editedPhone, onValueChange = { editedPhone = it }, label = { Text("Telefono") })
                    uploadImage(buttonText = "Elegir logo de la tienda")
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = onDismiss) {Text("Cancelar") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = { onEdit(store.id, editedName, editedAddress,editedCity,editedLogo,editedHistory,editedPhone); onDismiss() }) {Text("Guardar") }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteStoreDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    store: Store?
) {
    if (show && store != null) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    Text("¿Seguro que desea eliminar la tienda: ${store.name}?", style = MaterialTheme.typography.h6)
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