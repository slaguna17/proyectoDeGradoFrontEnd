package com.example.proyectodegrado.di

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun saveStoreId(storeId: String) {
        sharedPreferences.edit().putString("store_id", storeId).apply()
    }

    fun getStoreId(): String? {
        return sharedPreferences.getString("store_id", null)
    }

    fun saveUserName(userName: String?) {
        sharedPreferences.edit().putString("user_name", userName).apply()
    }

    fun getUserName(): String? {
        return sharedPreferences.getString("user_name", null)
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("notifications_enabled", enabled).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return sharedPreferences.getBoolean("notifications_enabled", true) // true por defecto
    }

    fun saveUserEmail(email: String) {
        sharedPreferences.edit().putString("user_email", email).apply()
    }

    fun getUserEmail(): String? {
        return sharedPreferences.getString("user_email", null)
    }

    fun clearUserEmail() {
        sharedPreferences.edit().remove("user_email").apply()
    }

    fun saveUserId(userId: String) {
        sharedPreferences.edit().putString("user_id", userId).apply()
    }

    fun getUserId(): String? {
        return sharedPreferences.getString("user_id", null)
    }

    fun clearUserId() {
        sharedPreferences.edit().remove("user_id").apply()
    }

    fun saveIsAdmin(isAdmin: Boolean) = sharedPreferences.edit().putBoolean("is_admin", isAdmin).apply()
    fun getIsAdmin(): Boolean = sharedPreferences.getBoolean("is_admin", false)

    fun clear() = sharedPreferences.edit().clear().apply()

}