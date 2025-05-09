plugins {
    // alias(libs.plugins.android.application) // Asumo que usas alias del archivo libs.versions.toml
    // alias(libs.plugins.jetbrains.kotlin.android)
    // Si no usas alias, serían así:
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // Añade 'kotlin-kapt' o 'id("com.google.devtools.ksp")' si usas anotaciones/procesadores
}

android {
    namespace = "com.example.proyectodegrado"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.proyectodegrado"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        // Asegúrate que esta versión sea compatible con la BOM y tus otras librerías
        kotlinCompilerExtensionVersion = "1.5.1" // Revisa si esta versión es la recomendada para la BOM que uses
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // --- Compose ---
    // 1) Importa la Bill of Materials (BOM) de Compose - ¡ESTO ES CLAVE!
    // Usa la versión estable más reciente o una compatible con tu proyecto.
    // Puedes encontrar la última en: https://developer.android.com/jetpack/compose/bom/bom-mapping
    implementation(platform("androidx.compose:compose-bom:2024.04.01")) // Ejemplo: BOM de Abril 2024

    // 2) Ahora puedes declarar las dependencias de Compose SIN versión explícita
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material") // Material 2 (si aún lo usas)
    implementation("androidx.compose.material3:material3") // Material 3
    implementation("androidx.compose.runtime:runtime-livedata") // También gestionado por la BOM

    // 3) Iconos Material (core + extended) - Sin versión gracias a la BOM
    implementation("androidx.compose.material:material-icons-core") // No necesita -jvm con la BOM
    implementation("androidx.compose.material:material-icons-extended")

    // 4) Activity & Navigation Compose (Normalmente también alineadas con la BOM)
    implementation("androidx.activity:activity-compose:1.9.0") // O usa la versión de la BOM si aplica
    implementation("androidx.navigation:navigation-compose:2.7.7") // O usa la versión de la BOM si aplica

    // --- Otras Dependencias ---
    // 5) Coil para imágenes (Necesita su propia versión)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // 6) Accompanist (Necesita su propia versión)
    implementation("com.google.accompanist:accompanist-permissions:0.30.1") // Verifica si aún necesitas Accompanist para permisos

    // 7) Retrofit + Gson + OkHttp (Necesitan sus propias versiones)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0") // OkHttp se incluye transitivamente con Retrofit, pero el interceptor no.

    // 8) DataStore Preferences (Necesita su propia versión)
    implementation("androidx.datastore:datastore-preferences:1.1.1") // Versión actualizada

    // 9) AndroidX Core, AppCompat y Lifecycle (Necesitan sus propias versiones)
    implementation("androidx.core:core-ktx:1.13.0") // Versión actualizada
    implementation("androidx.appcompat:appcompat:1.6.1") // Versión actualizada (o la que necesites)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0") // Versión actualizada
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0") // Versión actualizada

    // 10) Material Components Android (Necesita su propia versión - OJO: diferente a Compose Material)
    implementation("com.google.android.material:material:1.11.0")
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.protolite.well.known.types) // Verifica si realmente la necesitas si usas Compose Material3

    // --- Testing ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5") // Versión actualizada
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1") // Versión actualizada
    // Las dependencias de testing de Compose también se benefician de la BOM
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}