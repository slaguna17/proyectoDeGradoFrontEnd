package com.example.proyectodegrado.di

import android.content.Context
import com.example.proyectodegrado.data.api.*
import com.example.proyectodegrado.data.repository.*
import com.example.proyectodegrado.ui.screens.cash.CashViewModel
import com.example.proyectodegrado.ui.screens.categories.CategoryViewModel
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.proyectodegrado.data.model.MenuItemDTO
import com.example.proyectodegrado.ui.screens.purchases.PurchasesViewModel
import com.example.proyectodegrado.ui.screens.sales.SalesViewModel
import com.example.proyectodegrado.ui.screens.whatsapp_sales.WhatsappSalesViewModel

data class SessionState(
    val userId: Int? = null,
    val storeId: Int? = null,
    val isAdmin: Boolean = false,
    val menu: List<MenuItemDTO> = emptyList()
)

object DependencyProvider {

    private lateinit var applicationContext: Context
    private lateinit var preferences: AppPreferences

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

    private val _sessionState = MutableStateFlow(SessionState())
    val sessionState = _sessionState.asStateFlow()

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
        preferences = AppPreferences(applicationContext)

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
        shoppingCartRepository = ShoppingCartRepository(shoppingCartService)

        // Load saved session
        val userId = preferences.getUserId()?.toIntOrNull()
        val storeId = preferences.getStoreId()?.toIntOrNull()
        if (userId != null && storeId != null) {
            val isAdmin = preferences.getIsAdmin()
            _sessionState.value = SessionState(userId, storeId, isAdmin, menu = emptyList())
        }
    }

    // Saves session in App preferences
    fun saveCurrentSession(
        userId: Int,
        storeId: Int,
        isAdmin: Boolean,
        userEmail: String,
        userName: String?,
        menu: List<MenuItemDTO>
    ) {
        _sessionState.value = SessionState(userId, storeId, isAdmin, menu)
        preferences.saveUserId(userId.toString())
        preferences.saveStoreId(storeId.toString())
        preferences.saveIsAdmin(isAdmin)
        preferences.saveUserEmail(userEmail)
        preferences.saveUserName(userName)
    }

    // Temporary Session
    fun setTemporarySession(
        userId: Int,
        storeId: Int,
        isAdmin: Boolean,
        menu: List<MenuItemDTO>
    ) {
        _sessionState.value = SessionState(userId, storeId, isAdmin, menu)
    }

    fun clearCurrentSession() {
        preferences.clear()
        _sessionState.value = SessionState()
    }

    // Helpers
    fun getCurrentStoreId(): Int = _sessionState.value.storeId ?: 1
    fun getCurrentUserId(): Int = _sessionState.value.userId ?: 1
    fun getCurrentMenu(): List<MenuItemDTO> = _sessionState.value.menu
    fun updateMenu(newMenu: List<MenuItemDTO>) {
        val s = _sessionState.value
        _sessionState.value = s.copy(menu = newMenu)
    }

    // --- ViewModels ---
    fun provideSessionViewModel(): SessionViewModel = SessionViewModel(preferences)
    fun provideLoginViewModel(): LoginViewModel = LoginViewModel(userRepository, preferences) // Ahora pasamos 'preferences'
    fun provideRegisterViewModel(): RegisterViewModel = RegisterViewModel(userRepository, imageRepository)
    fun provideProductViewModel(): ProductViewModel = ProductViewModel(productRepository, categoryRepository, imageRepository, storeRepository)
    fun provideCategoryViewModel(): CategoryViewModel = CategoryViewModel(categoryRepository, imageRepository)
    fun provideStoreViewModel(): StoreViewModel = StoreViewModel(storeRepository, imageRepository)
    fun provideRoleViewModel(): RoleViewModel = RoleViewModel(roleRepository, permitRepository)
    fun provideProviderViewModel(): ProvidersViewModel = ProvidersViewModel(providerRepository)
    fun provideScheduleViewModel(): ScheduleViewModel = ScheduleViewModel(scheduleRepository)
    fun provideWorkersViewModel(): WorkersViewModel = WorkersViewModel(workerRepository, storeRepository, scheduleRepository)
    fun provideSalesViewModel(): SalesViewModel {
        return SalesViewModel(salesRepository, productRepository)
    }
    fun providePurchasesViewModel(): PurchasesViewModel {
        return PurchasesViewModel(purchasesRepository, productRepository)
    }
    fun provideProfileViewModel(): ProfileViewModel = ProfileViewModel(userRepository, imageRepository)
    fun provideCashViewModel(
        storeId: Int? = null,
        userId: Int? = null
    ): CashViewModel {
        val sid = storeId ?: _sessionState.value.storeId ?: 1
        val uid = userId ?: _sessionState.value.userId ?: 1
        return CashViewModel(cashRepository, sid, uid)
    }
    fun provideWhatsappSalesViewModel(): WhatsappSalesViewModel {
        return WhatsappSalesViewModel(shoppingCartRepository)
    }
}