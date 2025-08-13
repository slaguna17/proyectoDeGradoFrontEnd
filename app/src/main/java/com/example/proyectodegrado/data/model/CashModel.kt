package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

// ------------ Core entities ------------

data class CashboxSession(
    val id: Int,
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("purchase_box_id") val purchaseBoxId: Int? = null,
    @SerializedName("sales_box_id") val salesBoxId: Int? = null,
    @SerializedName("opening_amount") val openingAmount: Double,
    @SerializedName("closing_amount") val closingAmount: Double? = null,
    val status: String, // "open" | "closed"
    @SerializedName("opened_at") val openedAt: String,
    @SerializedName("closed_at") val closedAt: String? = null,
    // opcionales del backend
    @SerializedName("isProfit") val isProfit: Boolean? = null,
    val period: Int? = null,
    val filters: String? = null,
    val graphs: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

// Movimientos manuales de la sesión
data class CashMovement(
    val id: Int,
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("cash_summary_id") val cashSummaryId: Int,
    @SerializedName("user_id") val userId: Int,
    val direction: String, // "IN" | "OUT" | "ADJUST"
    val amount: Double,
    val category: String? = null,
    val notes: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

// Totales calculados para dashboard
data class CashTotals(
    val opening: Double,
    val salesCash: Double,
    val purchasesCash: Double,
    val manualIn: Double,
    val manualOut: Double,
    val expectedClosing: Double,
    val closingAmount: Double? = null,
    val difference: Double? = null
)

// Wrapper usado en listados: una sesión + sus totales
data class SessionWithTotals(
    val session: CashboxSession,
    val opening: Double,
    val salesCash: Double,
    val purchasesCash: Double,
    val manualIn: Double,
    val manualOut: Double,
    val expectedClosing: Double,
    val closingAmount: Double? = null,
    val difference: Double? = null
)

// ------------ Requests/Responses ------------

data class OpenCashboxRequest(
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("opening_amount") val openingAmount: Double
)
data class OpenCashboxResponse(
    val message: String,
    val cashbox: CashboxSession
)

data class CashCountItem(
    val currency: String = "BOB",
    val denomination: Double,
    val quantity: Int
)

data class CloseCashboxRequest(
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("user_id") val userId: Int,
    val date: String, // "YYYY-MM-DD"
    @SerializedName("closing_amount") val closingAmount: Double? = null,
    @SerializedName("cash_count") val cashCount: List<CashCountItem>? = null
)
data class CloseCashboxResponse(
    val message: String,
    val summary: CashboxSession
)

data class CreateMovementRequest(
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("user_id") val userId: Int,
    val direction: String, // "IN" | "OUT" | "ADJUST"
    val amount: Double,
    val category: String? = null,
    val notes: String? = null,
    val date: String? = null // si tu backend lo requiere para ubicar la sesión abierta
)
data class CreateMovementResponse(
    val message: String,
    val movement: CashMovement
)

data class CurrentCashResponse(
    val session: CashboxSession,
    val totals: CashTotals
)

data class SessionsListResponse(
    val sessions: List<SessionWithTotals>
)

data class SessionDetailResponse(
    val session: CashboxSession,
    val totals: CashTotals
)

data class MovementsResponse(
    val movements: List<CashMovement>
)

// ------------ Result wrapper para el repo ------------
sealed class ApiResult<out T> {
    data class Success<T>(val data: T): ApiResult<T>()
    data class Error(val message: String, val code: Int? = null): ApiResult<Nothing>()
}
