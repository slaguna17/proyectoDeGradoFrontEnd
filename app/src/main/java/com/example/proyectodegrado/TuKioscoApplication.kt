package com.example.proyectodegrado // Asegúrate que el package sea el correcto

import android.app.Application
import com.example.proyectodegrado.di.DependencyProvider // Importa tu provider

class TuKioskoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicialize DependencyProvider here with context
        DependencyProvider.initialize(this)
        println("DependencyProvider inicializado.")
    }
}