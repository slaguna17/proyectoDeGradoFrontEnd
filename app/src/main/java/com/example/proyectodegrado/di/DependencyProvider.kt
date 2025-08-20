package com.example.proyectodegrado.di

import android.content.Context
import com.example.proyectodegrado.data.api.CashService
import com.example.proyectodegrado.data.api.CategoryService
import com.example.proyectodegrado.data.api.ImageApiService
import com.example.proyectodegrado.data.api.ProductService
import com.example.proyectodegrado.data.api.ProviderService
import com.example.proyectodegrado.data.api.RetrofitClient
import com.example.proyectodegrado.data.api.ScheduleService
import com.example.proyectodegrado.data.api.StoreService
import com.example.proyectodegrado.data.api.UserService
import com.example.proyectodegrado.data.api.WorkerService
import com.example.proyectodegrado.data.repository.CategoryRepository
import com.example.proyectodegrado.data.repository.ImageRepository
import com.example.proyectodegrado.data.repository.ProductRepository
import com.example.proyectodegrado.data.repository.ProviderRepository
import com.example.proyectodegrado.data.repository.ScheduleRepository
import com.example.proyectodegrado.data.repository.StoreRepository
import com.example.proyectodegrado.data.repository.UserRepository
import com.example.proyectodegrado.data.repository.WorkerRepository
import com.example.proyectodegrado.data.repository.CashRepository
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
    }

    // --- Sesión en memoria (simple y suficiente por ahora) ---
    private data class Session(var userId: Int? = null, var storeId: Int? = null)
    private val session = Session()

    /** Llamar esto tras login o cuando el usuario elija la tienda activa */
    fun setCurrentSession(userId: Int, storeId: Int) {
        session.userId = userId
        session.storeId = storeId
    }

    // --- API Services ---
    private val userService: UserService by lazy { RetrofitClient.createService(UserService::class.java) }
    private val productService: ProductService by lazy { RetrofitClient.createService(ProductService::class.java) }
    private val categoryService: CategoryService by lazy { RetrofitClient.createService(CategoryService::class.java) }
    private val storeService: StoreService by lazy { RetrofitClient.createService(StoreService::class.java) }
    private val providerService: ProviderService by lazy { RetrofitClient.createService(ProviderService::class.java) }
    private val scheduleService: ScheduleService by lazy { RetrofitClient.createService(ScheduleService::class.java) }
    private val workerService: WorkerService by lazy { RetrofitClient.createService(WorkerService::class.java) }
    private val imageApiService: ImageApiService by lazy { RetrofitClient.createService(ImageApiService::class.java) }
    private val cashService: CashService by lazy { RetrofitClient.createService(CashService::class.java) }

    // --- Repositories ---
    private val userRepository: UserRepository by lazy { UserRepository(userService, applicationContext) }
    private val productRepository: ProductRepository by lazy { ProductRepository(productService) }
    private val categoryRepository: CategoryRepository by lazy { CategoryRepository(categoryService) }
    private val storeRepository: StoreRepository by lazy { StoreRepository(storeService) }
    private val providerRepository: ProviderRepository by lazy { ProviderRepository(providerService) }
    private val scheduleRepository: ScheduleRepository by lazy { ScheduleRepository(scheduleService) }
    private val workerRepository: WorkerRepository by lazy { WorkerRepository(workerService) }
    private val cashRepository: CashRepository by lazy { CashRepository(cashService) }

    private val imageRepository: ImageRepository by lazy {
        if (!::applicationContext.isInitialized) {
            throw IllegalStateException("DependencyProvider debe ser inicializado con Context antes de acceder a imageRepository.")
        }
        ImageRepository(imageApiService, applicationContext)
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
        ProfileViewModel(userRepository)

    /**
     * CashViewModel puede recibir IDs explícitos o tomar los de la sesión.
     * Para evitar crashes en desarrollo, si nada está seteado cae a (1,1).
     * En producción, cambia esos defaults por un manejo de selección de tienda/usuario.
     */
    fun provideCashViewModel(
        storeId: Int? = null,
        userId: Int? = null
    ): CashViewModel {
        val sid = storeId ?: session.storeId ?: 1   // ← fallback seguro para desarrollo
        val uid = userId ?: session.userId ?: 1     // ← fallback seguro para desarrollo
        return CashViewModel(cashRepository, sid, uid)
    }

    fun getCurrentStoreId(): Int = try { // si aún no seteaste sesión, usa 1 por ahora
        val field = this::class.java.getDeclaredField("session").apply { isAccessible = true }
        val s = field.get(this)
        val storeId = s.javaClass.getDeclaredField("storeId").apply { isAccessible = true }.get(s) as Int?
        storeId ?: 1
    } catch (_: Exception) { 1 }

    fun getCurrentUserId(): Int = try {
        val field = this::class.java.getDeclaredField("session").apply { isAccessible = true }
        val s = field.get(this)
        val userId = s.javaClass.getDeclaredField("userId").apply { isAccessible = true }.get(s) as Int?
        userId ?: 1
    } catch (_: Exception) { 1 }

}

