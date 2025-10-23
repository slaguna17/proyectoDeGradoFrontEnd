package com.example.proyectodegrado.utils

fun determineTitle(route: String?, categoryName: String? = null): String {
    return when {
        route == "home" -> "Inicio"
        route == "products" -> "Productos"
        route?.startsWith("products/") == true -> categoryName ?: "Productos"
        route == "categories" -> "Categorías"
        route == "store" -> "Tienda"
        route == "role" -> "Roles"
        route == "workers" -> "Empleados"
        route == "sales" -> "Ventas"
        route == "purchases" -> "Compras"
        route == "schedule" -> "Horarios"
        route == "cash" || route?.startsWith("cash/") == true -> "Caja"
        route == "providers" -> "Proveedores"
        route == "settings" -> "Ajustes"
        route == "login" -> ""
        route == "registerEmployee" -> "Nuevo Empleado"
        else -> "TuKiosco"
    }
}

fun shouldShowTopBar(route: String?): Boolean {
    return route != "login" && route != "register"
}

fun shouldShowBack(route: String?): Boolean {
    return route?.startsWith("products/") == true || route == "registerEmployee"
}