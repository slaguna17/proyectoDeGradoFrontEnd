package com.example.proyectodegrado // Asegúrate que el package sea el correcto

import android.app.Application
import com.example.proyectodegrado.di.DependencyProvider // Importa tu provider

class TuKioskoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializa el DependencyProvider aquí, pasándole el contexto de la aplicación
        DependencyProvider.initialize(this)
        println("DependencyProvider inicializado.") // Log opcional para verificar
    }
}