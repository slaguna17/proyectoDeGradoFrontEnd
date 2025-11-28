package com.example.proyectodegrado

import android.app.Application
import com.example.proyectodegrado.di.DependencyProvider

class TuKioskoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize DependencyProvider here with context
        DependencyProvider.initialize(this)
        println("DependencyProvider inicializado.")
    }
}