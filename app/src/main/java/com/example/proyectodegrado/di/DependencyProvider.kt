package com.example.proyectodegrado.di

import LoginViewModel
import UserService
import com.example.proyectodegrado.data.api.CategoryService
import com.example.proyectodegrado.data.api.ProductService
import com.example.proyectodegrado.data.api.RetrofitClient
import com.example.proyectodegrado.data.repository.CategoryRepository
import com.example.proyectodegrado.data.repository.ProductRepository
import com.example.proyectodegrado.data.repository.UserRepository
import com.example.proyectodegrado.ui.screens.products.ProductViewModel
import com.example.proyectodegrado.ui.screens.register.RegisterViewModel

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

    // Puedes agregar más dependencias aquí:
    // val productService: ProductService
    // val productRepository: ProductRepository
    // val productViewModel: ProductViewModel
}