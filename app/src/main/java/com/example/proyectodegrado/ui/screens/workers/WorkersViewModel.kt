package com.example.proyectodegrado.ui.screens.workers

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.*
import com.example.proyectodegrado.data.repository.ScheduleRepository
import com.example.proyectodegrado.data.repository.StoreRepository
import com.example.proyectodegrado.data.repository.WorkerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkersViewModel(
    private val workerRepository: WorkerRepository,
    private val storeRepository: StoreRepository,
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    private val _employees = MutableStateFlow<List<Worker>>(emptyList())
    val employees: StateFlow<List<Worker>> = _employees.asStateFlow()

    private val _stores = MutableStateFlow<List<Store>>(emptyList())
    val stores: StateFlow<List<Store>> = _stores.asStateFlow()

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

    private val _selectedWorkerContext = MutableStateFlow<SelectedWorkerContext?>(null)
    val selectedWorkerContext: StateFlow<SelectedWorkerContext?> = _selectedWorkerContext.asStateFlow()

    private val _assignError = MutableStateFlow<String?>(null)
    val assignError: StateFlow<String?> = _assignError.asStateFlow()

    private var selectedStoreId: Int? = null

    // Filtrado por tienda
    fun filterByStore(storeId: Int?) {
        selectedStoreId = storeId
        viewModelScope.launch {
            val result = if (storeId != null) {
                workerRepository.getEmployeesByStore(storeId)
            } else {
                workerRepository.getAllEmployees()
            }
            Log.d("WORKERS", "Empleados recibidos: ${result.size} $result")
            _employees.value = result.distinctBy { it.id }
        }
    }

    // Registrar nuevo empleado
    fun registerWorker(
        request: RegisterWorkerRequest,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val result = workerRepository.registerWorker(request)
            if (result.isSuccess) {
                filterByStore(null) // Recargar lista general después de registrar
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Error registrando empleado")
            }
        }
    }

    // Diálogo de asignación de tienda/turno
    fun openAssignScheduleDialog(worker: Worker) {
        _selectedWorkerContext.value = SelectedWorkerContext(
            worker,
            AssignScheduleFormState(
                storeId = worker.storeId,
                scheduleId = worker.scheduleId
            )
        )
        _assignError.value = null
    }
    fun closeAssignScheduleDialog() {
        _selectedWorkerContext.value = null
        _assignError.value = null
    }
    fun updateAssignForm(state: AssignScheduleFormState) {
        _selectedWorkerContext.value = _selectedWorkerContext.value?.copy(formState = state)
    }

    fun assignSchedule(
        onSuccess: () -> Unit
    ) {
        val context = _selectedWorkerContext.value ?: return
        val worker = context.worker
        val storeId = context.formState.storeId
        val scheduleId = context.formState.scheduleId

        // Validación: solo una tienda a la vez
        if (worker.storeId != null && worker.storeId != storeId) {
            _assignError.value = "El empleado ya está asignado a otra tienda"
            return
        }
        if (storeId == null || scheduleId == null) {
            _assignError.value = "Selecciona tienda y turno"
            return
        }
        viewModelScope.launch {
            val ok = workerRepository.assignSchedule(worker.id, storeId, scheduleId)
            if (ok) {
                closeAssignScheduleDialog()
                filterByStore(storeId)
                onSuccess()
            } else {
                _assignError.value = "Error al asignar"
            }
        }
    }

    // Cargar tiendas y turnos
    fun loadStoresAndSchedules() {
        viewModelScope.launch {
            _stores.value = storeRepository.getAllStores()
            _schedules.value = scheduleRepository.getAllSchedules()
        }
    }

    fun updateWorker(workerId: Int, name: String, email: String, phone: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            workerRepository.updateWorker(workerId, name, email, phone)
            filterByStore(selectedStoreId) // actualiza la lista
            onSuccess()
        }
    }

    fun deleteWorker(workerId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            workerRepository.deleteWorker(workerId)
            filterByStore(selectedStoreId)
            onSuccess()
        }
    }

}
