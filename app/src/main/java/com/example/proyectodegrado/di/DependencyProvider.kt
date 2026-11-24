package com.example.proyectodegrado.di

import android.content.Context
import com.example.proyectodegrado.data.api.*
import com.example.proyectodegrado.data.repository.*
import com.example.proyectodegrado.ui.screens.cash.CashViewModel
import com.example.proyectodegrado.ui.screens.categories.CategoryViewModel
import com.example.proyectodegrado.ui.screens.home.HomeViewModel
import com.example.proyectodegrado.ui.screens.login.LoginViewModel
import com.example.proyectodegrado.ui.screens.products.ProductViewModel
import com.example.proyectodegrado.ui.screens.profile.ProfileViewModel
import com.example.proyectodegrado.ui.screens.providers.ProvidersViewModel
import com.example.proyectodegrado.ui.screens.register.RegisterViewModel
import com.example.proyectodegrado.ui.screens.role.RoleViewModel
import com.example.proyectodegrado.ui.screens.schedule.ScheduleViewModel
import com.example.proyectodegrado.ui.screens.session.SessionViewModel
import com.example.proyectodegrado.ui.screens.store.StoreViewModel
import com.example.proyectodegrado.ui.screens.workers.WorkersViewModel
import kotlinx.coroutines.flow.StateFlow
import com.example.proyectodegrado.data.model.MenuItemDTO
import com.example.proyectodegrado.ui.screens.purchases.PurchasesViewModel
import com.example.proyectodegrado.ui.screens.reports.ReportsViewModel
import com.example.proyectodegrado.ui.screens.sales.SalesViewModel
import com.example.proyectodegrado.ui.screens.whatsapp_sales.WhatsappSalesViewModel

object DependencyProvider {

    private lateinit var applicationContext: Context
    private lateinit var preferences: AppPreferences
    private lateinit var sessionManager: SessionManager

    // Services
    private lateinit var userService: UserService
    private lateinit var productService: ProductService
    private lateinit var categoryService: CategoryService
    private lateinit var storeService: StoreService
    private lateinit var roleService: RoleService
    private lateinit var providerService: ProviderService
    private lateinit var scheduleService: ScheduleService
    private lateinit var workerService: WorkerService
    private lateinit var cashService: CashService
    private lateinit var imageApiService: ImageApiService
    private lateinit var permitService: PermitService
    private lateinit var userRepository: UserRepository
    private lateinit var salesService: SalesService
    private lateinit var purchasesService: PurchasesService
    private lateinit var shoppingCartService: ShoppingCartService
    private lateinit var reportsService: ReportsService

    // Repositories
    private lateinit var imageRepository: ImageRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var storeRepository: StoreRepository
    private lateinit var roleRepository: RoleRepository
    private lateinit var providerRepository: ProviderRepository
    private lateinit var scheduleRepository: ScheduleRepository
    private lateinit var workerRepository: WorkerRepository
    private lateinit var cashRepository: CashRepository
    private lateinit var permitRepository: PermitRepository
    private lateinit var salesRepository: SalesRepository
    private lateinit var purchasesRepository: PurchasesRepository
    private lateinit var shoppingCartRepository: ShoppingCartRepository
    private lateinit var reportsRepository: ReportsRepository

    val sessionState: StateFlow<SessionState>
        get() = sessionManager.sessionState

    val session: SessionManager
        get() = sessionManager

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
        preferences = AppPreferences(applicationContext)
        sessionManager = SessionManager(preferences)

        // Services Initializations
        userService = RetrofitClient.createService(UserService::class.java)
        productService = RetrofitClient.createService(ProductService::class.java)
        categoryService = RetrofitClient.createService(CategoryService::class.java)
        storeService = RetrofitClient.createService(StoreService::class.java)
        roleService = RetrofitClient.createService(RoleService::class.java)
        providerService = RetrofitClient.createService(ProviderService::class.java)
        scheduleService = RetrofitClient.createService(ScheduleService::class.java)
        workerService = RetrofitClient.createService(WorkerService::class.java)
        cashService = RetrofitClient.createService(CashService::class.java)
        imageApiService = RetrofitClient.createService(ImageApiService::class.java)
        permitService = RetrofitClient.createService(PermitService::class.java)
        salesService = RetrofitClient.createService(SalesService::class.java)
        purchasesService = RetrofitClient.createService(PurchasesService::class.java)
        reportsService = RetrofitClient.createService(ReportsService::class.java)
        shoppingCartService = RetrofitClient.createService(ShoppingCartService::class.java)

        // Repository Initializations
        userRepository = UserRepository(userService, applicationContext)
        imageRepository = ImageRepository(imageApiService, applicationContext)
        productRepository = ProductRepository(productService)
        categoryRepository = CategoryRepository(categoryService)
        storeRepository = StoreRepository(storeService)
        roleRepository = RoleRepository(roleService)
        providerRepository = ProviderRepository(providerService)
        scheduleRepository = ScheduleRepository(scheduleService)
        workerRepository = WorkerRepository(workerService)
        cashRepository = CashRepository(cashService)
        permitRepository = PermitRepository(permitService)
        salesRepository = SalesRepository(salesService)
        purchasesRepository = PurchasesRepository(purchasesService)
        reportsRepository = ReportsRepository(reportsService)
        shoppingCartRepository = ShoppingCartRepository(shoppingCartService)

        // Initialize session
        sessionManager.initialize()
    }

    // Session Management Delegation
    fun saveCurrentSession(
        userId: Int,
        storeId: Int,
        isAdmin: Boolean,
        userEmail: String,
        userName: String?,
        menu: List<MenuItemDTO>,
        token: String
    ) {
        sessionManager.saveCurrentSession(userId, storeId, isAdmin, userName, menu, token)
    }

    fun setTemporarySession(
        userId: Int,
        storeId: Int,
        isAdmin: Boolean,
        menu: List<MenuItemDTO>
    ) {
        sessionManager.setTemporarySession(userId, storeId, isAdmin, menu)
    }

    fun clearCurrentSession() {
        sessionManager.clearCurrentSession()
    }

    fun getAuthToken(): String? = sessionManager.getAuthToken()

    fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()

    fun getCurrentStoreId(): Int = sessionManager.getCurrentStoreId()
    fun getCurrentUserId(): Int = sessionManager.getCurrentUserId()
    fun getCurrentMenu(): List<MenuItemDTO> = sessionManager.getCurrentMenu()
    fun updateMenu(newMenu: List<MenuItemDTO>) {
        sessionManager.updateMenu(newMenu)
    }

    suspend fun refreshMenu() {
        val token = getAuthToken() ?: return
        try {
            userRepository.refreshMenuWithToken(token)
        } catch (_: Exception) {
            // Ignores network error if offline; cached menu is already loaded
        }
    }

    // --- ViewModels ---
    fun provideHomeViewModel(): HomeViewModel = HomeViewModel(productRepository, preferences)
    fun provideSessionViewModel(): SessionViewModel = SessionViewModel(preferences)
    fun provideLoginViewModel(): LoginViewModel = LoginViewModel(userRepository, preferences)
    fun provideRegisterViewModel(): RegisterViewModel = RegisterViewModel(userRepository, imageRepository)
    fun provideProductViewModel(): ProductViewModel = ProductViewModel(productRepository, categoryRepository, imageRepository, storeRepository)
    fun provideCategoryViewModel(): CategoryViewModel = CategoryViewModel(categoryRepository, imageRepository)
    fun provideStoreViewModel(): StoreViewModel = StoreViewModel(storeRepository, imageRepository)
    fun provideRoleViewModel(): RoleViewModel = RoleViewModel(roleRepository, permitRepository)
    fun provideProviderViewModel(): ProvidersViewModel = ProvidersViewModel(
        providerRepository = providerRepository,
        productRepository = productRepository
    )
    fun provideScheduleViewModel(): ScheduleViewModel = ScheduleViewModel(scheduleRepository)
    fun provideWorkersViewModel(): WorkersViewModel = WorkersViewModel(workerRepository, storeRepository, scheduleRepository)
    fun provideSalesViewModel(): SalesViewModel {
        return SalesViewModel(salesRepository, productRepository)
    }
    fun provideCashRepository(): CashRepository = cashRepository

    fun providePurchasesViewModel(): PurchasesViewModel {
        return PurchasesViewModel(purchasesRepository, productRepository)
    }
    fun provideProfileViewModel(): ProfileViewModel = ProfileViewModel(userRepository, imageRepository)
    fun provideReportsViewModel(): ReportsViewModel {
        return ReportsViewModel(reportsRepository)
    }
    fun provideCashViewModel(
        storeId: Int? = null,
        userId: Int? = null
    ): CashViewModel {
        val sid = storeId ?: sessionManager.getCurrentStoreId()
        val uid = userId ?: sessionManager.getCurrentUserId()
        return CashViewModel(cashRepository, sid, uid)
    }
    fun provideWhatsappSalesViewModel(): WhatsappSalesViewModel {
        return WhatsappSalesViewModel(shoppingCartRepository)
    }
}
