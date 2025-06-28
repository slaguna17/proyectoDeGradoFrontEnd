package com.example.proyectodegrado.ui.screens.workers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.AssignScheduleFormState
import com.example.proyectodegrado.data.model.SelectedWorkerContext
import com.example.proyectodegrado.data.model.Worker
import com.example.proyectodegrado.data.repository.WorkerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkersViewModel(
    private val workerRepository: WorkerRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _employees = MutableStateFlow<List<Worker>>(emptyList())
    val employees: StateFlow<List<Worker>> = _employees.asStateFlow()

    private val _selectedWorkerContext = MutableStateFlow<SelectedWorkerContext?>(null)
    val selectedWorkerContext: StateFlow<SelectedWorkerContext?> = _selectedWorkerContext.asStateFlow()

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        searchEmployees(newQuery)
    }

    private fun searchEmployees(query: String) {
        viewModelScope.launch {
            try {
                val results = workerRepository.searchEmployees(query)
                _employees.value = results
            } catch (e: Exception) {
                _employees.value = emptyList() // Manejo básico de errores
            }
        }
    }

    fun openAssignScheduleDialog(worker: Worker) {
        _selectedWorkerContext.value = SelectedWorkerContext(worker)
    }

    fun closeDialog() {
        _selectedWorkerContext.value = null
    }

    fun updateAssignScheduleForm(state: AssignScheduleFormState) {
        _selectedWorkerContext.value = _selectedWorkerContext.value?.copy(formState = state)
    }

    fun confirmScheduleAssignment(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val context = _selectedWorkerContext.value ?: return
        val storeId = context.formState.storeId
        val scheduleId = context.formState.scheduleId
        if (storeId == null || scheduleId == null) {
            onError("Tienda y turno requeridos")
            return
        }

        viewModelScope.launch {
            val success = workerRepository.assignSchedule(context.worker.id, storeId, scheduleId)
            if (success) {
                closeDialog()
                onSuccess()
            } else {
                onError("Error al asignar")
            }
        }
    }
}
