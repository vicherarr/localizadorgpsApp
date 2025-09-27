# Localizador GPS App

Aplicación Android profesional para monitoreo de vehículos desarrollada con Kotlin y Jetpack Compose. Compatible con el backend [`localizadorgps`](https://github.com/vicherarr/localizadorgps) y preparada para escalar nuevas funcionalidades.

## Características principales

- **Autenticación JWT** integrada con el endpoint `/api/autenticacion/inicio-sesion`.
- **Listado de flota activa** con actualizaciones periódicas automáticas.
- **Detalle de vehículo** con mapa de Google Maps, historial reciente y métricas clave (velocidad, precisión, última actualización).
- **Temas claro/oscuro** basados en Material Design 3.
- Arquitectura **Clean + MVVM** con separación clara en capas (`data`, `domain`, `ui`).
- Persistencia de sesión mediante **DataStore** y consumo de API con **Retrofit** y **OkHttp**.

## Configuración

1. **Backend**
   - Ejecuta la API de [localizadorgps](https://github.com/vicherarr/localizadorgps) en tu entorno local siguiendo sus instrucciones.
   - Asegúrate de exponerla en `https://10.0.2.2:5001/` (emulador) o ajusta la constante `API_BASE_URL`.

2. **API Base URL**
   - Por defecto, `BuildConfig.API_BASE_URL` apunta a `https://10.0.2.2:5001/`.
   - Cambia el valor en `app/build.gradle.kts` si tu backend vive en otra URL.

3. **Clave de Google Maps**
   - Sustituye la cadena `TU_API_KEY_AQUI` en `app/src/main/res/values/strings.xml` por tu clave válida de Maps SDK for Android.

4. **Compilación**
   ```bash
   ./gradlew assembleDebug
   ```

## Arquitectura

```text
app/
├── data/          # Fuentes remotas/locales + repositorios
├── domain/        # Modelos y casos de uso
├── ui/            # Pantallas Compose, navegación y tema
├── core/          # Contenedor de dependencias
└── LocalizadorGpsApp.kt
```

- **Data layer**: Retrofit + OkHttp (interceptor JWT) y DataStore para persistir sesión.
- **Domain layer**: Modelos puros y casos de uso reutilizables.
- **UI layer**: Jetpack Compose + Navigation. ViewModels consumen casos de uso y exponen `StateFlow` para la UI.

## Pantallas

- **Splash/Login**: flujo de acceso con validaciones y feedback visual.
- **Listado**: tarjetas con estado del vehículo y última ubicación, acciones de refresco y cierre de sesión.
- **Detalle**: mapa con marcador, panel informativo y carrusel de historial.

## Tecnologías

- Kotlin, Coroutines, Flow
- Jetpack Compose + Material 3
- Navigation Compose
- Retrofit + Gson + OkHttp Logging
- DataStore Preferences
- Google Maps Compose + Play Services Location

## Próximos pasos sugeridos

- Integrar notificaciones push o WebSockets (SignalR) cuando el backend lo permita.
- Gestión de perfiles y configuración avanzada (limites geográficos, alertas personalizadas).
- Descarga y visualización de rutas históricas completas sobre el mapa.

---
Con esta base podrás iterar rápidamente nuevas funcionalidades manteniendo un diseño profesional y una arquitectura mantenible.
