package com.example.proyectodegrado.utils

fun determineTitle(route: String?, categoryName: String? = null): String {
    return when (route) {
        "home" -> "Inicio"
        "products" -> "Productos"
        "categories" -> "Categorías"
        "products/{categoryId}" -> categoryName ?: "Productos"
        "store" -> "Tienda"
        "workers" -> "Empleados"
        "schedule" -> "Horarios"
        "forecast" -> "Pronósticos"
        "balance" -> "Caja"
        "providers" -> "Proveedores"
        "barcode" -> "Código de Barras"
        "settings" -> "Ajustes"
        "login", "register" -> ""
        "registerEmployee" -> "Nuevo Empleado"
        else -> "TuKiosco"
    }
}

fun shouldShowTopBar(route: String?): Boolean {
    return route != "login" && route != "register"
}

fun shouldShowBack(route: String?): Boolean {
    return route == "products/{categoryId}" || route == "registerEmployee"
}