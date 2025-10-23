package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

data class CashSession(
    val id: Int,
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("opening_amount") val openingAmount: Double,
    @SerializedName("closing_amount") val closingAmount: Double? = null,
    val status: String, // "open" | "closed"
    @SerializedName("opened_at") val openedAt: String,
    @SerializedName("closed_at") val closedAt: String? = null
)

data class CashMovement(
    val id: Int,
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("cash_session_id") val cashSessionId: Int,
    @SerializedName("user_id") val userId: Int,
    val direction: String, // "IN" | "OUT" | "ADJUST"
    val amount: Double,
    val category: String? = null,
    val notes: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    @SerializedName("origin_type") val originType: String?, // "SALE", "PURCHASE", "MANUAL"
    @SerializedName("origin_id") val originId: Int?
)

data class CashTotals(
    val opening: Double,
    val income: Double,  // Total income (sales + manuals)
    val expenses: Double, // Total expenses (purchases + manuals)
    val expectedClosing: Double,
    val closingAmount: Double? = null,
    val difference: Double? = null
)

data class SessionWithTotals(
    val session: CashSession,
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

data class OpenCashRequest(
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("opening_amount") val openingAmount: Double
)

data class OpenCashResponse(
    val message: String,
    val session: CashSession
)

data class CashCountItem(
    val currency: String = "BOB",
    val denomination: Double,
    val quantity: Int
)

data class CloseCashRequest(
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("user_id") val userId: Int,
    val date: String, // "YYYY-MM-DD"
    @SerializedName("closing_amount") val closingAmount: Double? = null,
    @SerializedName("cash_count") val cashCount: List<CashCountItem>? = null
)

data class CloseCashResponse(
    val message: String,
    val session: CashSession
)

data class CreateMovementRequest(
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("user_id") val userId: Int,
    val direction: String, // "IN" | "OUT" | "ADJUST"
    val amount: Double,
    val category: String? = null,
    val notes: String? = null,
    val date: String? = null
)

data class CreateMovementResponse(
    val message: String,
    val movement: CashMovement
)

data class CurrentCashResponse(
    val session: CashSession,
    val totals: CashTotals
)

data class SessionsListResponse(
    val sessions: List<SessionWithTotals>
)

data class SessionDetailResponse(
    val session: CashSession,
    val totals: CashTotals
)

data class MovementsResponse(
    val movements: List<CashMovement>
)

sealed class ApiResult<out T> {
    // 2. Haz que la clase Success también sea genérica con "<T>"
    //    y asegúrate de que 'data' sea de tipo T.
    data class Success<T>(val data: T): ApiResult<T>()

    // 3. La clase Error no necesita ser genérica.
    data class Error(val message: String, val code: Int? = null): ApiResult<Nothing>()
}
