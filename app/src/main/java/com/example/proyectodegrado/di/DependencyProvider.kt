package com.example.proyectodegrado.di

import com.example.proyectodegrado.ui.screens.login.LoginViewModel
import android.content.Context
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
import com.example.proyectodegrado.data.repository.ProductRepository
import com.example.proyectodegrado.data.repository.ProviderRepository
import com.example.proyectodegrado.data.repository.ScheduleRepository
import com.example.proyectodegrado.data.repository.StoreRepository
import com.example.proyectodegrado.data.repository.UserRepository
import com.example.proyectodegrado.ui.screens.products.ProductViewModel
import com.example.proyectodegrado.ui.screens.providers.ProvidersViewModel
import com.example.proyectodegrado.ui.screens.register.RegisterViewModel
import com.example.proyectodegrado.ui.screens.schedule.ScheduleViewModel
import com.example.proyectodegrado.ui.screens.store.StoreViewModel
import com.example.proyectodegrado.data.repository.* // Importa todos tus repositorios
import com.example.proyectodegrado.ui.screens.categories.CategoryViewModel
import com.example.proyectodegrado.ui.screens.workers.WorkersViewModel

object DependencyProvider {

    private lateinit var applicationContext: Context

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    // --- Servicios API ---
    private val userService: UserService by lazy { RetrofitClient.createService(UserService::class.java) }
    private val productService: ProductService by lazy { RetrofitClient.createService(ProductService::class.java) }
    private val categoryService: CategoryService by lazy { RetrofitClient.createService(CategoryService::class.java) }
    private val storeService: StoreService by lazy { RetrofitClient.createService(StoreService::class.java) }
    private val providerService: ProviderService by lazy { RetrofitClient.createService(ProviderService::class.java) }
    private val scheduleService: ScheduleService by lazy { RetrofitClient.createService(ScheduleService::class.java) }
    private val workerService: WorkerService by lazy { RetrofitClient.createService(WorkerService::class.java) }
    private val imageApiService: ImageApiService by lazy { RetrofitClient.createService(ImageApiService::class.java) }

    // --- Repositorios ---
    private val userRepository: UserRepository by lazy { UserRepository(userService) }
    private val productRepository: ProductRepository by lazy { ProductRepository(productService) }
    private val categoryRepository: CategoryRepository by lazy { CategoryRepository(categoryService) }
    private val storeRepository: StoreRepository by lazy { StoreRepository(storeService) }
    private val providerRepository: ProviderRepository by lazy { ProviderRepository(providerService) }
    private val scheduleRepository: ScheduleRepository by lazy { ScheduleRepository(scheduleService) }
    private val workerRepository: WorkerRepository by lazy { WorkerRepository(workerService) }

    private val imageRepository: ImageRepository by lazy {
        if (!::applicationContext.isInitialized) {
            throw IllegalStateException("DependencyProvider debe ser inicializado con Context antes de acceder a imageRepository.")
        }
        ImageRepository(imageApiService, applicationContext)
    }
    // ------------------------------------

    // --- ViewModels ---
    fun provideLoginViewModel(): LoginViewModel {
        return LoginViewModel(userRepository)
    }

    fun provideRegisterViewModel(): RegisterViewModel {
        // Ejemplo: Si el registro necesita subir avatar
        return RegisterViewModel(userRepository, imageRepository) // <--- INYECTADO
    }

    fun provideProductViewModel(): ProductViewModel {
        // Ejemplo: Si productos necesita subir imagen
        return ProductViewModel(productRepository, categoryRepository, imageRepository) // <--- INYECTADO
    }
    fun provideCategoryViewModel(): CategoryViewModel {
        // Ejemplo: Si productos necesita subir imagen
        return CategoryViewModel(categoryRepository, imageRepository) // <--- INYECTADO
    }


    fun provideStoreViewModel(): StoreViewModel {
        // Ejemplo: Si tienda necesita subir logo
        return StoreViewModel(storeRepository, imageRepository) // <--- INYECTADO
    }

    fun provideProviderViewModel(): ProvidersViewModel {
        return ProvidersViewModel(providerRepository)
    }

    fun provideScheduleViewModel(): ScheduleViewModel {
        return ScheduleViewModel(scheduleRepository)
    }

    fun provideWorkersViewModel(): WorkersViewModel {
        return WorkersViewModel(workerRepository)
    }

}