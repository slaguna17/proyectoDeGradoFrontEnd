package com.example.proyectodegrado.ui.screens.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.Schedule
import com.example.proyectodegrado.data.model.ScheduleRequest
import com.example.proyectodegrado.data.repository.ScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class ScheduleViewModel(private val repository: ScheduleRepository) : ViewModel() {

    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    val schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

    fun fetchSchedules(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _schedules.value = repository.getAllSchedules()
                onSuccess()
            } catch (e: IOException) {
                onError("Error de red: ${e.message}")
            } catch (e: HttpException) {
                onError("Error del servidor: ${e.message}")
            } catch (e: Exception) {
                onError("Error desconocido: ${e.message}")
            }
        }
    }

    // --- INICIO DE LA CORRECCIÓN ---

    fun createSchedule(request: ScheduleRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.createSchedule(request)
                if (response.isSuccessful) {
                    onSuccess() // Notifica a la UI inmediatamente.
                    // Refresca la lista en segundo plano.
                    fetchSchedules(onSuccess = {}, onError = {})
                } else {
                    onError("Falló la creación: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("Error de conexión al crear.")
            }
        }
    }

    fun updateSchedule(id: Int, request: ScheduleRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.updateSchedule(id, request)
                if (response.isSuccessful) {
                    onSuccess() // Notifica a la UI inmediatamente.
                    // Refresca la lista en segundo plano.
                    fetchSchedules(onSuccess = {}, onError = {})
                } else {
                    onError("Falló la actualización: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("Error de conexión al actualizar.")
            }
        }
    }

    fun deleteSchedule(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = repository.deleteSchedule(id)
                if (response.isSuccessful) {
                    onSuccess() // Notifica a la UI inmediatamente.
                    // Refresca la lista en segundo plano.
                    fetchSchedules(onSuccess = {}, onError = {})
                } else {
                    onError("Falló la eliminación: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                onError("Error de conexión al eliminar.")
            }
        }
    }
    // --- FIN DE LA CORRECCIÓN ---
}