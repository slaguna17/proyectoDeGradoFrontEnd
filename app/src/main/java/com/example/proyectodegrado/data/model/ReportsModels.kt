package com.example.proyectodegrado.data.model

import com.google.gson.annotations.SerializedName

data class ReportFiltersDto(
    @SerializedName("store_id") val storeId: Int? = null,
    @SerializedName("user_id") val userId: Int? = null,
    @SerializedName("provider_id") val providerId: Int? = null,
    val from: String? = null,
    val to: String? = null,
    @SerializedName("payment_method") val paymentMethod: String? = null,
    val status: String? = null
)

data class SalesSummaryMetrics(
    @SerializedName("sales_count") val salesCount: Int = 0,
    @SerializedName("total_amount") val totalAmount: Double = 0.0,
    @SerializedName("average_ticket") val averageTicket: Double = 0.0,
    @SerializedName("total_quantity") val totalQuantity: Int = 0
)

data class SalesByDayItem(
    val date: String? = null,
    @SerializedName("sales_count") val salesCount: Int = 0,
    @SerializedName("total_amount") val totalAmount: Double = 0.0
)

data class SalesByPaymentMethodItem(
    @SerializedName("payment_method") val paymentMethod: String? = null,
    @SerializedName("sales_count") val salesCount: Int = 0,
    @SerializedName("total_amount") val totalAmount: Double = 0.0
)

data class SalesByStatusItem(
    val status: String? = null,
    @SerializedName("sales_count") val salesCount: Int = 0,
    @SerializedName("total_amount") val totalAmount: Double = 0.0
)

data class SalesDetailItem(
    val id: Int,
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("store_name") val storeName: String? = null,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_name") val userName: String? = null,
    val total: Double,
    @SerializedName("sale_date") val saleDate: String? = null,
    @SerializedName("payment_method") val paymentMethod: String? = null,
    val status: String? = null,
    val notes: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class SalesTopProductItem(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("SKU") val sku: String? = null,
    @SerializedName("product_name") val productName: String? = null,
    val brand: String? = null,
    @SerializedName("total_quantity") val totalQuantity: Int = 0,
    @SerializedName("total_amount") val totalAmount: Double = 0.0,
    @SerializedName("sales_count") val salesCount: Int = 0
)

data class SalesSummaryResponse(
    val filters: ReportFiltersDto? = null,
    val summary: SalesSummaryMetrics = SalesSummaryMetrics(),
    @SerializedName("by_day") val byDay: List<SalesByDayItem> = emptyList(),
    @SerializedName("by_payment_method") val byPaymentMethod: List<SalesByPaymentMethodItem> = emptyList(),
    @SerializedName("by_status") val byStatus: List<SalesByStatusItem> = emptyList()
)

data class SalesDetailsResponse(
    val filters: ReportFiltersDto? = null,
    val details: List<SalesDetailItem> = emptyList()
)

data class SalesTopProductsResponse(
    val filters: ReportFiltersDto? = null,
    val products: List<SalesTopProductItem> = emptyList()
)

data class PurchasesSummaryMetrics(
    @SerializedName("purchases_count") val purchasesCount: Int = 0,
    @SerializedName("total_amount") val totalAmount: Double = 0.0,
    @SerializedName("average_purchase") val averagePurchase: Double = 0.0,
    @SerializedName("total_quantity") val totalQuantity: Int = 0
)

data class PurchasesByDayItem(
    val date: String? = null,
    @SerializedName("purchases_count") val purchasesCount: Int = 0,
    @SerializedName("total_amount") val totalAmount: Double = 0.0
)

data class PurchasesByPaymentMethodItem(
    @SerializedName("payment_method") val paymentMethod: String? = null,
    @SerializedName("purchases_count") val purchasesCount: Int = 0,
    @SerializedName("total_amount") val totalAmount: Double = 0.0
)

data class PurchasesByStatusItem(
    val status: String? = null,
    @SerializedName("purchases_count") val purchasesCount: Int = 0,
    @SerializedName("total_amount") val totalAmount: Double = 0.0
)

data class PurchaseDetailItem(
    val id: Int,
    @SerializedName("store_id") val storeId: Int,
    @SerializedName("store_name") val storeName: String? = null,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_name") val userName: String? = null,
    @SerializedName("provider_id") val providerId: Int? = null,
    @SerializedName("provider_name") val providerName: String? = null,
    val total: Double,
    @SerializedName("purchase_date") val purchaseDate: String? = null,
    @SerializedName("payment_method") val paymentMethod: String? = null,
    val status: String? = null,
    val notes: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class PurchasesTopProductItem(
    @SerializedName("product_id") val productId: Int,
    @SerializedName("SKU") val sku: String? = null,
    @SerializedName("product_name") val productName: String? = null,
    val brand: String? = null,
    @SerializedName("total_quantity") val totalQuantity: Int = 0,
    @SerializedName("total_amount") val totalAmount: Double = 0.0,
    @SerializedName("purchases_count") val purchasesCount: Int = 0
)

data class PurchasesByProviderItem(
    @SerializedName("provider_id") val providerId: Int? = null,
    @SerializedName("provider_name") val providerName: String? = null,
    @SerializedName("purchases_count") val purchasesCount: Int = 0,
    @SerializedName("total_amount") val totalAmount: Double = 0.0
)

data class PurchasesSummaryResponse(
    val filters: ReportFiltersDto? = null,
    val summary: PurchasesSummaryMetrics = PurchasesSummaryMetrics(),
    @SerializedName("by_day") val byDay: List<PurchasesByDayItem> = emptyList(),
    @SerializedName("by_payment_method") val byPaymentMethod: List<PurchasesByPaymentMethodItem> = emptyList(),
    @SerializedName("by_status") val byStatus: List<PurchasesByStatusItem> = emptyList()
)

data class PurchasesDetailsResponse(
    val filters: ReportFiltersDto? = null,
    val details: List<PurchaseDetailItem> = emptyList()
)

data class PurchasesTopProductsResponse(
    val filters: ReportFiltersDto? = null,
    val products: List<PurchasesTopProductItem> = emptyList()
)

data class PurchasesByProviderResponse(
    val filters: ReportFiltersDto? = null,
    val providers: List<PurchasesByProviderItem> = emptyList()
)

data class SalesReportBundle(
    val summary: SalesSummaryResponse,
    val details: SalesDetailsResponse,
    val topProducts: SalesTopProductsResponse
)

data class PurchasesReportBundle(
    val summary: PurchasesSummaryResponse,
    val details: PurchasesDetailsResponse,
    val topProducts: PurchasesTopProductsResponse,
    val byProvider: PurchasesByProviderResponse
)