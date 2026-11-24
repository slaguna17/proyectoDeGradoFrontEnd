package com.example.proyectodegrado.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.proyectodegrado.data.model.MenuItemDTO
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AppPreferences(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    private val securePreferences: SharedPreferences by lazy {
        try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                "secure_app_prefs",
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (_: Exception) {
            sharedPreferences
        }
    }

    init {
        migrateAuthToken()
    }

    private fun migrateAuthToken() {
        val legacyToken = sharedPreferences.getString("auth_token", null)
        if (legacyToken != null) {
            securePreferences.edit().putString("auth_token", legacyToken).apply()
            sharedPreferences.edit().remove("auth_token").apply()
        }
    }

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
        return sharedPreferences.getBoolean("notifications_enabled", true)
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

    fun saveMenu(menu: List<MenuItemDTO>) {
        val json = Gson().toJson(menu)
        sharedPreferences.edit().putString("saved_menu", json).apply()
    }

    fun getMenu(): List<MenuItemDTO> {
        val json = sharedPreferences.getString("saved_menu", null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<MenuItemDTO>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun clear() {
        clearAuthToken()
        sharedPreferences.edit().clear().apply()
    }

    fun clearSession() {
        clearAuthToken()
        sharedPreferences.edit()
            .remove("user_id")
            .remove("store_id")
            .remove("user_name")
            .remove("store_name")
            .remove("is_admin")
            .remove("saved_menu")
            .apply()
    }

    fun saveStoreName(storeName: String) {
        sharedPreferences.edit().putString("store_name", storeName).apply()
    }

    fun getStoreName(): String? {
        return sharedPreferences.getString("store_name", null)
    }

    fun saveAuthToken(token: String) {
        securePreferences.edit().putString("auth_token", token).apply()
    }

    fun getAuthToken(): String? {
        return securePreferences.getString("auth_token", null)
    }

    fun clearAuthToken() {
        securePreferences.edit().remove("auth_token").apply()
        if (sharedPreferences.contains("auth_token")) {
            sharedPreferences.edit().remove("auth_token").apply()
        }
    }

    fun saveLastStockNotificationDate(date: String) {
        sharedPreferences.edit().putString("last_stock_notification_date", date).apply()
    }

    fun getLastStockNotificationDate(): String? {
        return sharedPreferences.getString("last_stock_notification_date", null)
    }
}
