# Corrección de Inicio de App y Gestión de JWT

Se han solucionado los problemas de visualización al inicio y la falta de persistencia del token de autenticación (JWT).

## Cambios Realizados

### Autenticación y JWT
- **[AppPreferences.kt](file:///C:/Users/slagu/AndroidStudioProjects/proyectoDeGradoFrontEnd/app/src/main/java/com/example/proyectodegrado/di/AppPreferences.kt)**: Se agregaron métodos para guardar y recuperar el token JWT de las preferencias compartidas.
- **[DependencyProvider.kt](file:///C:/Users/slagu/AndroidStudioProjects/proyectoDeGradoFrontEnd/app/src/main/java/com/example/proyectodegrado/di/DependencyProvider.kt)**: Se actualizó el estado de la sesión para incluir el `authToken`. Ahora se carga el token al inicializar la app.
- **[RetrofitClient.kt](file:///C:/Users/slagu/AndroidStudioProjects/proyectoDeGradoFrontEnd/app/src/main/java/com/example/proyectodegrado/data/api/RetrofitClient.kt)**: Se implementó un `AuthInterceptor` que añade automáticamente el encabezado `Authorization: Bearer <token>` a todas las peticiones si hay un usuario logueado.
- **ViewModels**: Se actualizaron `LoginViewModel` y `RegisterViewModel` para capturar el token de la respuesta del servidor y guardarlo en la sesión.

### UI e Inicio de App
- **[MainActivity.kt](file:///C:/Users/slagu/AndroidStudioProjects/proyectoDeGradoFrontEnd/app/src/main/java/com/example/proyectodegrado/MainActivity.kt)**: Se envolvió el contenido principal en un `Surface` con el color de fondo del tema. Esto elimina la pantalla negra/transparente que aparecía antes de que cargara el primer Composable.
- **[AppNavigation.kt](file:///C:/Users/slagu/AndroidStudioProjects/proyectoDeGradoFrontEnd/app/src/main/java/com/example/proyectodegrado/utils/AppNavigation.kt)**:
    - Se implementó el inicio automático. Si ya hay una sesión activa, la app arranca directamente en la pantalla de "Inicio" (`home`).
    - Se configuró el `NavHost` para usar el destino inicial dinámicamente según el estado de la sesión.

## Verificación

- El proyecto compila correctamente.
- Las peticiones al backend ahora incluyen el token JWT, lo que soluciona los errores 401.
- La app tiene un fondo consistente desde el primer segundo.

> [!IMPORTANT]
> Si deseas forzar el login para probar, puedes usar la opción "Cerrar sesión" en el menú lateral, lo cual limpiará el token y te devolverá a la pantalla de acceso.
