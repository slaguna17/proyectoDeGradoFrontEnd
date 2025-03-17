package com.example.proyectodegrado.di

import LoginViewModel
import UserService
import com.example.proyectodegrado.data.api.CategoryService
import com.example.proyectodegrado.data.api.ProductService
import com.example.proyectodegrado.data.api.ProviderService
import com.example.proyectodegrado.data.api.RetrofitClient
import com.example.proyectodegrado.data.api.ScheduleService
import com.example.proyectodegrado.data.api.StoreService
import com.example.proyectodegrado.data.model.Provider
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

object DependencyProvider {
    // Servicio de usuario
    val userService: UserService by lazy {
        RetrofitClient.createService(UserService::class.java)
    }

    // Repositorio de usuario
    val userRepository: UserRepository by lazy {
        UserRepository(userService)
    }

    // ViewModel para login
    fun provideLoginViewModel(): LoginViewModel {
        return LoginViewModel(userRepository)
    }

    // ViewModel para Register
    fun provideRegisterViewModel(): RegisterViewModel {
        return RegisterViewModel(userRepository)
    }

    // Servicio de productos
    val productService: ProductService by lazy {
        RetrofitClient.createService(ProductService::class.java)
    }

    // Servicio de productos
    val categoryService: CategoryService by lazy {
        RetrofitClient.createService(CategoryService::class.java)
    }

    // Repositorio de productos
    val productRepository: ProductRepository by lazy {
        ProductRepository(productService)
    }

    // Repositorio de categorias
    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(categoryService)
    }

    // ViewModel para Productos
    fun provideProductViewModel(): ProductViewModel {
        return ProductViewModel(productRepository, categoryRepository)
    }

    // Servicio de Store
    val storeService: StoreService by lazy {
        RetrofitClient.createService(StoreService::class.java)
    }

    // Repositorio de Store
    val storeRepository: StoreRepository by lazy {
        StoreRepository(storeService)
    }

    // ViewModel para Stores
    fun provideStoreViewModel(): StoreViewModel {
        return StoreViewModel(storeRepository)
    }

    // Servicio de Provider
    val providerService: ProviderService by lazy {
        RetrofitClient.createService(ProviderService::class.java)
    }

    // Repositorio de Provider
    val providerRepository: ProviderRepository by lazy {
        ProviderRepository(providerService)
    }

    // ViewModel para Provider
    fun provideProviderViewModel(): ProvidersViewModel {
        return ProvidersViewModel(providerRepository)
    }

    // Servicio de Schedule
    val scheduleService: ScheduleService by lazy {
        RetrofitClient.createService(ScheduleService::class.java)
    }

    // Repositorio de Schedule
    val scheduleRepository: ScheduleRepository by lazy {
        ScheduleRepository(scheduleService)
    }

    // ViewModel para Schedules
    fun provideScheduleViewModel(): ScheduleViewModel {
        return ScheduleViewModel(scheduleRepository)
    }


}