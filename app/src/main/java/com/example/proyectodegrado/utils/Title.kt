package com.example.proyectodegrado.utils

fun determineTitle(route: String?, categoryName: String? = null): String {
    return when {
        route == "home" -> "Inicio"
        route == "products" -> "Productos"
        route?.startsWith("products/") == true -> categoryName ?: "Productos"
        route == "categories" -> "Categorías"
        route == "store" -> "Tienda"
        route == "workers" -> "Empleados"
        route == "schedule" -> "Horarios"
        route == "forecast" -> "Pronósticos"
        route == "cash" || route?.startsWith("cash/") == true -> "Caja"
        route == "providers" -> "Proveedores"
        route == "barcode" -> "Código de Barras"
        route == "settings" -> "Ajustes"
        route == "login" || route == "register" -> ""
        route == "registerEmployee" -> "Nuevo Empleado"
        else -> "TuKiosco"
    }
}

fun shouldShowTopBar(route: String?): Boolean {
    return route != "login" && route != "register"
}

fun shouldShowBack(route: String?): Boolean {
    // Muestra "atrás" en rutas dinámicas de productos, o donde lo necesites
    return route?.startsWith("products/") == true || route == "registerEmployee"
}