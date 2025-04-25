package com.example.proyectodegrado.di

import com.example.proyectodegrado.ui.screens.login.LoginViewModel
import UserService
import android.content.Context
import com.example.proyectodegrado.data.api.CategoryService
import com.example.proyectodegrado.data.api.ImageApiService
import com.example.proyectodegrado.data.api.ProductService
import com.example.proyectodegrado.data.api.ProviderService
import com.example.proyectodegrado.data.api.RetrofitClient
import com.example.proyectodegrado.data.api.ScheduleService
import com.example.proyectodegrado.data.api.StoreService
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

// Importa otros ViewModels o Factories que necesites modificar

object DependencyProvider {

    private lateinit var applicationContext: Context

    fun initialize(context: Context) {
        applicationContext = context.applicationContext
    }

    // --- Servicios API ---
    // (Tu código existente para userService, productService, etc. aquí...)
    val userService: UserService by lazy { RetrofitClient.createService(UserService::class.java) }
    val productService: ProductService by lazy { RetrofitClient.createService(ProductService::class.java) }
    val categoryService: CategoryService by lazy { RetrofitClient.createService(CategoryService::class.java) }
    val storeService: StoreService by lazy { RetrofitClient.createService(StoreService::class.java) }
    val providerService: ProviderService by lazy { RetrofitClient.createService(ProviderService::class.java) }
    val scheduleService: ScheduleService by lazy { RetrofitClient.createService(ScheduleService::class.java) }
    // Asegúrate que ImageApiService esté aquí
    val imageApiService: ImageApiService by lazy { RetrofitClient.createService(ImageApiService::class.java) }

    // --- Repositorios ---
    // (Tu código existente para userRepository, productRepository, etc. aquí...)
    val userRepository: UserRepository by lazy { UserRepository(userService) }
    val productRepository: ProductRepository by lazy { ProductRepository(productService) }
    val categoryRepository: CategoryRepository by lazy { CategoryRepository(categoryService) }
    val storeRepository: StoreRepository by lazy { StoreRepository(storeService) }
    val providerRepository: ProviderRepository by lazy { ProviderRepository(providerService) }
    val scheduleRepository: ScheduleRepository by lazy { ScheduleRepository(scheduleService) }

    // --- NUEVO: Repositorio de Imágenes ---
    val imageRepository: ImageRepository by lazy {
        if (!::applicationContext.isInitialized) {
            throw IllegalStateException("DependencyProvider debe ser inicializado con Context antes de acceder a imageRepository.")
        }
        ImageRepository(imageApiService, applicationContext)
    }
    // ------------------------------------

    // --- ViewModels ---
    // Modifica estos métodos para inyectar 'imageRepository' donde sea necesario

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

}