package com.example.proyectodegrado.ui.screens.sales

import org.json.JSONObject

object SalesErrorMapper {

    fun mapCreateSaleError(serverMessage: String?): String {
        if (serverMessage.isNullOrBlank()) {
            return "Ocurrió un error al registrar la venta."
        }

        /*
         * PRIMER INTENTO:
         * Intentar parsear el body JSON devuelto por el backend.
         *
         * Ejemplo esperado:
         * {
         *   "error": "Insufficient stock for this product in the store.",
         *   "code": "INSUFFICIENT_STOCK",
         *   "details": {
         *     "storeId": 1,
         *     "productId": 10,
         *     "productName": "Leche Pil 1L",
         *     "availableStock": 2,
         *     "requestedQuantity": 5,
         *     "missingQuantity": 3
         *   }
         * }
         */
        try {
            val json = JSONObject(serverMessage)

            val error = json.optString("error")
            val code = json.optString("code")
            val details = json.optJSONObject("details")

            if (code.equals("CASH_SESSION_NOT_OPEN", ignoreCase = true)) {
                return "No hay una caja abierta. Debes abrir una caja para registrar la venta."
            }

            if (code.equals("INSUFFICIENT_STOCK", ignoreCase = true)) {
                val productName = details?.optString("productName")
                val availableStock = details?.optInt("availableStock")
                val requestedQuantity = details?.optInt("requestedQuantity")
                val missingQuantity = details?.optInt("missingQuantity")

                return buildInsufficientStockMessage(
                    productName = productName,
                    availableStock = availableStock,
                    requestedQuantity = requestedQuantity,
                    missingQuantity = missingQuantity
                )
            }

            if (code.equals("PRODUCT_NOT_FOUND_IN_STORE_INVENTORY", ignoreCase = true)) {
                return "El producto no existe en el inventario de esta tienda."
            }

            if (error.isNotBlank()) {
                return error
            }
        } catch (_: Exception) {
            // Si no es JSON válido, seguimos con los fallbacks de texto.
        }

        /*
         * FALLBACKS por texto, por si el backend todavía no estuviera actualizado
         * o si llega un mensaje plano.
         */
        if (
            serverMessage.contains(
                "There is not an opened cashbox session",
                ignoreCase = true
            )
        ) {
            return "No hay una caja abierta. Debes abrir una caja para registrar la venta."
        }

        if (
            serverMessage.contains(
                "Insufficient stock for this product in the store",
                ignoreCase = true
            )
        ) {
            return "No hay stock suficiente en la tienda para completar la venta."
        }

        if (
            serverMessage.contains(
                "chk_store_product_stock_nonnegative",
                ignoreCase = true
            ) ||
            serverMessage.contains(
                "violates check constraint",
                ignoreCase = true
            )
        ) {
            return "No hay stock suficiente en la tienda para completar la venta."
        }

        return serverMessage
    }

    private fun buildInsufficientStockMessage(
        productName: String?,
        availableStock: Int?,
        requestedQuantity: Int?,
        missingQuantity: Int?
    ): String {
        val safeProductName = if (productName.isNullOrBlank()) "sin nombre" else productName
        val safeAvailableStock = availableStock ?: 0
        val safeRequestedQuantity = requestedQuantity ?: 0
        val safeMissingQuantity = missingQuantity ?: 0

        return "No hay stock suficiente en la tienda del producto \"$safeProductName\". " +
                "Se necesita $safeMissingQuantity unidades para llegar a las " +
                "$safeRequestedQuantity unidades requeridas. " +
                "Stock disponible actual: $safeAvailableStock."
    }
}