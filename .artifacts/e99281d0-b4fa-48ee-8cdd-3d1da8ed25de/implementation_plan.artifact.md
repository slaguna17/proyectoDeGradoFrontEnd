# Corregir Inicio de App y Manejo de JWT

Se han identificado dos problemas principales:
1.  **Pantalla negra/transparente al inicio**: Probablemente debido a la falta de un fondo base en `MainActivity` y a que se instancian todos los ViewModels simultáneamente al arrancar `AppNavigation`, algunos de los cuales realizan peticiones de red inmediatamente.
2.  **Manejo de JWT**: El token devuelto por el login no se está guardando ni enviando en las peticiones posteriores, lo que causa errores 401 (Unauthorized) en el backend.

## Proposed Changes

### [Core/DI]

#### [MODIFY] [AppPreferences.kt](file:///C:/Users/slagu/AndroidStudioProjects/proyectoDeGradoFrontEnd/app/src/main/java/com/example/proyectodegrado/di/AppPreferences.kt)
- Agregar métodos `saveAuthToken(token: String)` y `getAuthToken(): String?`.

#### [MODIFY] [DependencyProvider.kt](file:///C:/Users/slagu/AndroidStudioProjects/proyectoDeGradoFrontEnd/app/src/main/java/com/example/proyectodegrado/di/DependencyProvider.kt)
- Incluir `authToken: String?` en `SessionState`.
- Actualizar `initialize` para cargar el token guardado.
- Actualizar `saveCurrentSession` para recibir y guardar el token.
- Agregar un método `isLoggedIn(): Boolean`.

#### [MODIFY] [RetrofitClient.kt](file:///C:/Users/slagu/AndroidStudioProjects/proyectoDeGradoFrontEnd/app/src/main/java/com/example/proyectodegrado/data/api/RetrofitClient.kt)
- Agregar un `AuthInterceptor` que inserte el encabezado `Authorization: Bearer <token>` si el token está disponible en `DependencyProvider`.

### [UI Layer]

#### [MODIFY] [MainActivity.kt](file:///C:/Users/slagu/AndroidStudioProjects/proyectoDeGradoFrontEnd/app/src/main/java/com/example/proyectodegrado/MainActivity.kt)
- Envolver `MyApp()` en un `Surface` con el color de fondo del tema para evitar la pantalla negra/transparente inicial.

#### [MODIFY] [LoginViewModel.kt](file:///C:/Users/slagu/AndroidStudioProjects/proyectoDeGradoFrontEnd/app/src/main/java/com/example/proyectodegrado/ui/screens/login/LoginViewModel.kt)
- Capturar el `token` de la respuesta de login y pasarlo a `DependencyProvider.saveCurrentSession`.

#### [MODIFY] [AppNavigation.kt](file:///C:/Users/slagu/AndroidStudioProjects/proyectoDeGradoFrontEnd/app/src/main/java/com/example/proyectodegrado/utils/AppNavigation.kt)
- Implementar redirección automática: si el usuario ya está logueado al iniciar la app, navegar directamente a "home".
- (Opcional) Considerar diferir la creación de algunos ViewModels para evitar ráfagas de peticiones de red al inicio.

## Verification Plan

### Manual Verification
1.  **Login**: Iniciar sesión y verificar en el Logcat que las peticiones posteriores (como cargar categorías o productos) incluyan el encabezado `Authorization`.
2.  **Persistencia**: Cerrar la app y volverla a abrir. Debería entrar directamente a la pantalla de Inicio si la sesión está activa, sin pasar por el login.
3.  **Pantalla de Inicio**: Verificar que al abrir la app ya no se vea un fondo negro/transparente momentáneo.
