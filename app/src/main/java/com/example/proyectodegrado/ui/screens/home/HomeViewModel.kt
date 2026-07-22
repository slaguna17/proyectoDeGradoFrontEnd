package com.example.proyectodegrado.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodegrado.data.model.ApiResult
import com.example.proyectodegrado.data.model.StockAlertSummaryDto
import com.example.proyectodegrado.data.repository.ProductRepository
import com.example.proyectodegrado.di.AppPreferences
import com.example.proyectodegrado.utils.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val preferences: AppPreferences
) : ViewModel() {

    private val _alertSummary = MutableStateFlow<StockAlertSummaryDto?>(null)
    val alertSummary: StateFlow<StockAlertSummaryDto?> = _alertSummary

    fun checkStockAlerts(context: Context) {
        viewModelScope.launch {
            when (val result = productRepository.getStockAlertSummary()) {
                is ApiResult.Success -> {
                    _alertSummary.value = result.data
                    if (result.data.hasAlerts) {
                        maybeShowNotification(context, result.data)
                    }
                }
                else -> {
                    // Handle error silently or as needed
                }
            }
        }
    }

    private fun maybeShowNotification(context: Context, summary: StockAlertSummaryDto) {
        val lastNotificationDate = preferences.getLastStockNotificationDate()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (lastNotificationDate != today) {
            NotificationHelper.showLowStockNotification(
                context,
                summary.lowStockCount,
                summary.outOfStockCount
            )
            preferences.saveLastStockNotificationDate(today)
        }
    }
}
