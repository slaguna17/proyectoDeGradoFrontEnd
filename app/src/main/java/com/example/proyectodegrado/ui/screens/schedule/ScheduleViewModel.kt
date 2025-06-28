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

class ScheduleViewModel (private val scheduleRepository: ScheduleRepository) : ViewModel() {
    //Result Messages
    private var scheduleResult: String = ""

    //List and state flows
    private val _schedules = MutableStateFlow<List<Schedule>>(emptyList())
    var schedules: StateFlow<List<Schedule>> = _schedules.asStateFlow()

    //Single object flow
    private val emptySchedule = Schedule(-1, "",0, "","")
    private val _schedule = MutableStateFlow<Schedule>(emptySchedule)

    //Schedule Functions
    fun fetchSchedules(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                _schedules.value = scheduleRepository.getAllSchedules()
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

    fun fetchSchedule(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val schedule = scheduleRepository.getSchedule(id)
                _schedule.value = schedule
                onSuccess()
            } catch (e: Exception) {
                onError("Network error: ${e.message}")
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

    fun createSchedule(request: ScheduleRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = scheduleRepository.createSchedule(request)
                if (response.isSuccessful) {
                    scheduleResult = response.body()?.message ?: "Created Schedule successful!"
                    fetchSchedules(onSuccess = onSuccess, onError = onError)
                } else {
                    onError("Failed: ${response.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                onError("Network error: ${e.message}")
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

    fun updateSchedule(id:Int, request: ScheduleRequest, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = scheduleRepository.updateSchedule(id,request)
                if (response.isSuccessful) {
                    scheduleResult = response.body()?.message ?: "Updated Schedule successfully!"
                    fetchSchedules(onSuccess = onSuccess, onError = onError)
                } else {
                    onError("Failed: ${response.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                onError("Network error: ${e.message}")
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }

    fun deleteSchedule(id: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = scheduleRepository.deleteSchedule(id)
                if (response.isSuccessful) {
                    scheduleResult = response.body()?.message ?: "Deleted schedule successfully!"
                    fetchSchedules(onSuccess = onSuccess, onError = onError)
                } else {
                    onError("Failed: ${response.errorBody()?.string()}")
                }
            } catch (e: IOException) {
                onError("Network error: ${e.message}")
            } catch (e: HttpException) {
                onError("Unexpected error: ${e.message}")
            }
        }
    }
}