package com.example.proyectodegrado.di

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    // Métodos para manejar el ID de la tienda
    fun saveStoreId(storeId: String) {
        sharedPreferences.edit().putString("store_id", storeId).apply()
    }

    fun getStoreId(): String? {
        return sharedPreferences.getString("store_id", null)
    }

    // Métodos para manejar el nombre de usuario
    fun saveUserName(userName: String) {
        sharedPreferences.edit().putString("user_name", userName).apply()
    }

    fun getUserName(): String? {
        return sharedPreferences.getString("user_name", null)
    }

    // Métodos para manejar la configuración de notificaciones
    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean("notifications_enabled", true) // true por defecto
    }

    // Agrega más métodos según sea necesario para otras características
}