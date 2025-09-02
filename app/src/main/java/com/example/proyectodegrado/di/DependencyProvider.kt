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
import com.example.proyectodegrado.ui.screens.schedule.ScheduleViewModel
import com.example.proyectodegrado.ui.screens.store.StoreViewModel
import com.example.proyectodegrado.ui.screens.workers.WorkersViewModel

object DependencyProvider {

    // --- App context ---
    private lateinit var applicationContext: Context
    fun initialize(context: Context) {
        applicationContext = context.applicationContext
        // ← Primea la sesión desde prefs si estaban guardadas
        val uid = preferences.getUserId()?.toIntOrNull()
        val sid = preferences.getStoreId()?.toIntOrNull()
        if (uid != null) session.userId = uid
        if (sid != null) session.storeId = sid
    }

    // --- Sesión (simple) ---
    private data class Session(var userId: Int? = null, var storeId: Int? = null)
    private val session = Session()

    fun setCurrentSession(userId: Int, storeId: Int) {
        session.userId = userId
        session.storeId = storeId
        preferences.saveUserId(userId.toString())
        preferences.saveStoreId(storeId.toString())
    }

    private val preferences by lazy { AppPreferences(applicationContext) }

    fun getCurrentStoreId(): Int = session.storeId ?: 1
    fun getCurrentUserId(): Int = session.userId ?: 1

    // --- API Services (unificados con RetrofitClient) ---
    private val userService: UserService by lazy { RetrofitClient.createService(UserService::class.java) }
    private val productService: ProductService by lazy { RetrofitClient.createService(ProductService::class.java) }
    private val categoryService: CategoryService by lazy { RetrofitClient.createService(CategoryService::class.java) }
    private val storeService: StoreService by lazy { RetrofitClient.createService(StoreService::class.java) }
    private val providerService: ProviderService by lazy { RetrofitClient.createService(ProviderService::class.java) }
    private val scheduleService: ScheduleService by lazy { RetrofitClient.createService(ScheduleService::class.java) }
    private val workerService: WorkerService by lazy { RetrofitClient.createService(WorkerService::class.java) }
    private val cashService: CashService by lazy { RetrofitClient.createService(CashService::class.java) }
    private val imageApiService: ImageApiService by lazy { RetrofitClient.createService(ImageApiService::class.java) }

    // --- Repositories ---
    private val userRepository: UserRepository by lazy { UserRepository(userService, applicationContext) }
    private val productRepository: ProductRepository by lazy { ProductRepository(productService) }
    private val categoryRepository: CategoryRepository by lazy { CategoryRepository(categoryService) }
    private val storeRepository: StoreRepository by lazy { StoreRepository(storeService) }
    private val providerRepository: ProviderRepository by lazy { ProviderRepository(providerService) }
    private val scheduleRepository: ScheduleRepository by lazy { ScheduleRepository(scheduleService) }
    private val workerRepository: WorkerRepository by lazy { WorkerRepository(workerService) }
    private val cashRepository: CashRepository by lazy { CashRepository(cashService) }

    // ⚠️ Usa la MISMA firma que ya usas en el proyecto (api + context)
    // Si tu ImageRepository tuviera otra firma, ajusta aquí.
    private val imageRepository: ImageRepository by lazy {
        check(::applicationContext.isInitialized) {
            "DependencyProvider.initialize(context) debe llamarse antes de usar imageRepository."
        }
        ImageRepository(imageApiService,applicationContext)
    }

    // --- ViewModels ---
    fun provideLoginViewModel(): LoginViewModel =
        LoginViewModel(userRepository)

    fun provideRegisterViewModel(): RegisterViewModel =
        RegisterViewModel(userRepository, imageRepository)

    fun provideProductViewModel(): ProductViewModel =
        ProductViewModel(productRepository, categoryRepository, imageRepository, storeRepository)

    fun provideCategoryViewModel(): CategoryViewModel =
        CategoryViewModel(categoryRepository, imageRepository)

    fun provideStoreViewModel(): StoreViewModel =
        StoreViewModel(storeRepository, imageRepository)

    fun provideProviderViewModel(): ProvidersViewModel =
        ProvidersViewModel(providerRepository)

    fun provideScheduleViewModel(): ScheduleViewModel =
        ScheduleViewModel(scheduleRepository)

    fun provideWorkersViewModel(): WorkersViewModel =
        WorkersViewModel(workerRepository, storeRepository, scheduleRepository)

    fun provideProfileViewModel(): ProfileViewModel =
        ProfileViewModel(userRepository, imageRepository, preferences)

    fun provideCashViewModel(
        storeId: Int? = null,
        userId: Int? = null
    ): CashViewModel {
        val sid = storeId ?: session.storeId ?: 1
        val uid = userId ?: session.userId ?: 1
        return CashViewModel(cashRepository, sid, uid)
    }

}
