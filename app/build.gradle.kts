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
        kotlinCompilerExtensionVersion = "1.5.11" // Revisa si esta versión es la recomendada para la BOM que uses
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
    // 1) Bill of Materials (BOM) de Compose - ¡Esto está perfecto!
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))

    // 2) Dependencias de Compose SIN versión (manejadas por la BOM)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.runtime:runtime-livedata")

    // ¡CORREGIDO! Esta es la dependencia oficial para Pull-to-Refresh
//    implementation("androidx.compose.material:material-pull-refresh")

    // 3) Iconos Material
    implementation("androidx.compose.material:material-icons-core")
    implementation("androidx.compose.material:material-icons-extended")

    // 4) Activity & Navigation Compose
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // --- Otras Dependencias ---
    // 5) Coil para imágenes
    implementation("io.coil-kt:coil-compose:2.6.0")

    // 6) Accompanist (solo si necesitas otras funcionalidades que no sean pull-refresh)
    implementation("com.google.accompanist:accompanist-permissions:0.30.1")
    implementation("com.google.accompanist:accompanist-flowlayout:0.30.1")

    // 7) Retrofit + Gson + OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // 8) DataStore Preferences
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // 9) AndroidX Core, AppCompat y Lifecycle
    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")

    // 10) Material Components Android
    implementation("com.google.android.material:material:1.11.0")
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.protolite.well.known.types)

    // --- Testing ---
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}