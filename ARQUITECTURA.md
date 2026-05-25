# Closify — Documento de Arquitectura
**Fecha:** Mayo 2026  
**Tecnologías:** Kotlin · Jetpack Compose · MVVM · Clean Architecture  
**Proyecto:** TP Integrador — Desarrollo de Aplicaciones Móviles (UADE)

---

## 1. Descripción del proyecto

**Closify** es una aplicación Android para digitalizar ropa, generar outfits y organizar el guardarropa personal.

### Funcionalidades actuales
- Onboarding (ya implementado)
- Login y Registro (en desarrollo por otro integrante)
- Home Screen (a implementar)
- Generación de Outfits (a implementar)
- Favoritos (a implementar)

### Funcionalidades futuras
- Firebase Auth (autenticación)
- Firebase Firestore (base de datos en la nube)
- IA para recorte de prendas
- Planificación por calendario
- Social / amigos

---

## 2. Criterio arquitectónico

La arquitectura fue pensada para ser **apropiada para el scope del TP**, sin caer en sobrearquitectura.

### Regla aplicada

> Si una clase solo delega a otra sin agregar lógica propia, no existe.

### Lo que se mantiene y por qué

| Patrón | ¿Se usa? | Por qué |
|---|---|---|
| Repository + interfaces | ✅ Sí | Permite reemplazar datos mock por Firebase sin tocar ViewModel ni UI |
| ViewModel + StateFlow | ✅ Sí | Estándar Android moderno, requerido por la consigna |
| Screen vs Content | ✅ Sí | Composables limpios, UI pura separada de la lógica |
| Events pattern (UDF) | ✅ Sí | Unidirectional Data Flow correcto |
| AuthNavGraph / AppNavGraph | ✅ Sí | Necesario para proteger pantallas según sesión |
| Use Cases simples | ❌ No | Solo agregan una capa que delega sin lógica propia |
| `GenerateOutfitsUseCase` | ✅ Sí | Único con lógica real: filtra prendas y arma combinaciones |
| Hilt (Dependency Injection) | ❌ No | Complejidad innecesaria para este scope |

### ¿Por qué no Hilt?

Hilt es la librería estándar de Dependency Injection en Android. Para este TP se decidió no usarla porque agrega conceptos nuevos (`@Module`, `@Provides`, `@Binds`) que no aportan valor con el scope actual. La arquitectura está diseñada para incorporarlo fácilmente en el futuro si se necesita.

---

## 3. Estructura de capas

```
ViewModel
    └── llama directo al Repository
            └── FakeRepository (hoy)
            └── FirebaseRepository (después)
```

Solo `GenerateOutfitsUseCase` rompe esta regla porque tiene lógica genuina que no pertenece ni al ViewModel ni al Repository.

---

## 4. Estructura de carpetas

```
com/closify/myapplication/
│
├── MainActivity.kt
│
├── navigation/
│   ├── AppNavGraph.kt              ← Rutas protegidas (requieren sesión)
│   ├── AuthNavGraph.kt             ← Rutas públicas (Onboarding, Login, Registro)
│   └── Screen.kt                   ← Definición tipada de todas las rutas
│
├── ui/
│   ├── theme/                      ← (existente: Color, Theme, Type)
│   ├── components/                 ← Componentes reutilizables globales
│   │   ├── ClosifyLogo.kt          ← (existente)
│   │   ├── ClosifyButton.kt
│   │   └── SelectableChip.kt
│   │
│   ├── screens/
│   │   ├── Onboarding.kt           ← (existente, NO modificar)
│   │   │
│   │   ├── home/
│   │   │   ├── HomeScreen.kt               ← Conecta ViewModel + NavController
│   │   │   ├── HomeContent.kt              ← UI pura, recibe estado y lambdas
│   │   │   └── components/
│   │   │       ├── WeatherSelector.kt
│   │   │       ├── OccasionSelector.kt
│   │   │       ├── TimeOfDaySelector.kt
│   │   │       └── GenerateOutfitButton.kt
│   │   │
│   │   └── outfits/
│   │       ├── OutfitResultScreen.kt       ← Conecta ViewModel + NavController
│   │       ├── OutfitResultContent.kt      ← UI pura, recibe estado y lambdas
│   │       └── components/
│   │           ├── OutfitCard.kt
│   │           ├── GarmentItem.kt
│   │           └── FavoriteToggle.kt
│   │
│   └── viewmodel/
│       ├── Onboarding.kt                   ← (existente, NO modificar)
│       ├── MainViewModel.kt                ← Maneja SessionState global
│       ├── HomeViewModel.kt
│       └── OutfitResultViewModel.kt
│
├── domain/
│   ├── model/
│   │   ├── Garment.kt
│   │   ├── Outfit.kt
│   │   ├── WeatherCondition.kt
│   │   ├── Occasion.kt
│   │   └── TimeOfDay.kt
│   │
│   ├── repository/
│   │   ├── GarmentRepository.kt            ← Interface
│   │   ├── OutfitRepository.kt             ← Interface
│   │   └── SessionRepository.kt            ← Interface
│   │
│   └── usecase/
│       └── GenerateOutfitsUseCase.kt       ← Único Use Case: tiene lógica real
│
└── data/
    ├── repository/
    │   ├── FakeGarmentRepository.kt        ← Mock (reemplazable por Firebase)
    │   ├── FakeOutfitRepository.kt         ← Mock
    │   └── FakeSessionRepository.kt        ← Mock (reemplazable por Firebase Auth)
    │
    └── source/
        ├── LocalGarmentDataSource.kt       ← Prendas hardcodeadas
        └── PreferencesDataSource.kt        ← Lógica de DataStore
```

---

## 5. Manejo de sesión

### ¿Cómo sabe la app si hay sesión activa?

La app usa **DataStore** para persistir flags localmente en el dispositivo:

| Key | Valor | Propósito |
|---|---|---|
| `has_seen_onboarding` | `true / false` | Saber si ya vio el onboarding |
| `is_logged_in` | `true / false` | Saber si hay sesión activa |

> **Ahora (mock):** `saveSession()` simplemente guarda `is_logged_in = true` en DataStore. No hay userId real porque las credenciales son hardcodeadas.
>
> **Con Firebase Auth:** `saveSession()` ni se llama. Firebase persiste la sesión internamente de forma segura. `isLoggedIn()` pasa a leer `FirebaseAuth.getInstance().currentUser != null` directamente. No se guarda el token manualmente.

### SessionState

```kotlin
sealed interface SessionState {
    data object Loading   : SessionState  // leyendo DataStore al abrir la app
    data object LoggedOut : SessionState  // sin sesión → mostrar Auth
    data object LoggedIn  : SessionState  // con sesión → mostrar App
}
```

### Flujo al abrir la app

```
App abre
    │
    ▼
SessionState.Loading (lee DataStore)
    │
    ├── has_seen_onboarding = false
    │       └──► Onboarding → guarda flag → Login
    │
    ├── has_seen_onboarding = true, is_logged_in = false
    │       └──► Login
    │
    └── has_seen_onboarding = true, is_logged_in = true
            └──► Home
```

### ¿Por qué DataStore y no Room para la sesión?

Se evaluaron dos opciones para persistir la sesión localmente:

**Escenario A — Usuario ya logueado, abre la app sin internet**
DataStore resuelve esto completamente: lee `is_logged_in = true` y va directo a Home. No necesita Room ni internet.

**Escenario B — Usuario nunca se logueó, intenta loguearse sin internet**
Room permitiría validar credenciales localmente, pero implica guardar contraseñas en el dispositivo (incluso hasheadas), lo cual es un riesgo de seguridad si el teléfono es rooteado. Las apps reales no soportan este escenario por esta razón.

| Opción | Ya logueado offline | Primer login offline | Seguridad | Complejidad |
|---|---|---|---|---|
| DataStore | ✅ Resuelto | ❌ No soportado | ✅ Bien | Baja |
| Room con usuarios | ✅ Resuelto | ✅ Soportado | ⚠️ Riesgo | Alta |
| Firebase Auth | ✅ Automático | ❌ No soportado | ✅ Muy bien | Baja |

**Decisión: DataStore.** El caso de uso más común (Escenario A) queda cubierto. El Escenario B no es soportado por diseño, igual que en apps reales. Cuando se integre Firebase Auth, él maneja la persistencia de sesión de forma segura sin necesidad de DataStore ni Room.

---

### ¿Cómo se protegen las pantallas?

En Android no hay middleware de rutas como en web. La protección se logra con dos NavGraphs separados. Si no hay sesión, el `AppNavGraph` no existe en el árbol de composición — el usuario no puede llegar a Home aunque lo intente.

```kotlin
when (sessionState) {
    SessionState.Loading   -> SplashScreen()
    SessionState.LoggedOut -> AuthNavGraph()   // Onboarding, Login, Registro
    SessionState.LoggedIn  -> AppNavGraph()    // Home, Outfits, Favoritos
}
```

---

### Contrato de integración para quien implementa Login

Solo hay **un punto de contacto** entre el feature de Login y la arquitectura de sesión.

**Lo único que necesita hacer el LoginViewModel cuando el login es exitoso:**

```kotlin
class LoginViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    fun onLoginSuccess() {
        viewModelScope.launch {
            sessionRepository.saveSession()  // ← esta línea, sin parámetros
            // No hay que navegar manualmente.
            // MainViewModel detecta el cambio y muestra Home automáticamente.
        }
    }
}
```

**Por qué no hay que navegar manualmente:**

```
saveSession()
    → DataStore guarda is_logged_in = true
        → SessionRepository emite isLoggedIn = true
            → MainViewModel actualiza SessionState.LoggedIn
                → MainActivity muestra AppNavGraph → Home aparece solo ✅
```

**Cómo obtener el SessionRepository en el LoginViewModel:**

```kotlin
// Por ahora, desde AppContainer (objeto global de dependencias)
val sessionRepository = AppContainer.sessionRepository
```

> **Nota para Firebase:** cuando se integre Firebase Auth, `saveSession()` no se llama más.
> El LoginViewModel llama a `FirebaseAuth.signInWithEmailAndPassword()` y Firebase
> persiste la sesión solo. El `SessionRepository` actualiza su implementación internamente
> sin que el LoginViewModel cambie.

---

## 6. Navegación — Screen.kt

Todas las rutas se definen en un solo archivo con clases tipadas para evitar errores en strings:

```kotlin
sealed class Screen(val route: String) {
    // Auth
    data object Onboarding   : Screen("onboarding")
    data object Login        : Screen("login")
    data object Register     : Screen("register")

    // App
    data object Home         : Screen("home")
    data object OutfitResult : Screen("outfit_result/{weather}/{occasion}/{timeOfDay}") {
        fun createRoute(weather: String, occasion: String, timeOfDay: String) =
            "outfit_result/$weather/$occasion/$timeOfDay"
    }
}
```

---

## 7. Patrón UI — UDF (Unidirectional Data Flow)

```
Usuario interactúa → Event → ViewModel procesa → UiState actualizado → UI recompone
```

La UI nunca modifica el estado directamente. Solo envía Events al ViewModel.

### Separación Screen vs Content

| Composable | Conoce el ViewModel | Conoce NavController | UI pura testeable |
|---|---|---|---|
| `HomeScreen` | ✅ Sí | ✅ Sí | ❌ No |
| `HomeContent` | ❌ No | ❌ No | ✅ Sí |

`HomeContent` recibe solo `HomeUiState` y lambdas. No tiene dependencias de Android.

---

## 8. UiState de cada feature

### HomeUiState
```kotlin
data class HomeUiState(
    val selectedWeather: WeatherCondition? = null,
    val selectedOccasion: Occasion? = null,
    val selectedTimeOfDay: TimeOfDay? = null,
    val isAutoWeather: Boolean = false,
    val isGenerateEnabled: Boolean = false,  // derivado automáticamente en el ViewModel
    val isLoading: Boolean = false
)
```

### OutfitResultUiState
```kotlin
data class OutfitResultUiState(
    val outfits: List<Outfit> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val selectedOutfitId: String? = null,
    val isLoading: Boolean = false,
    val isEmpty: Boolean = false             // derivado de outfits.isEmpty()
)
```

---

## 9. Events de cada feature

### HomeEvent
```kotlin
sealed interface HomeEvent {
    data class SelectWeather(val weather: WeatherCondition) : HomeEvent
    data class SelectOccasion(val occasion: Occasion) : HomeEvent
    data class SelectTimeOfDay(val time: TimeOfDay) : HomeEvent
    data object ToggleAutoWeather : HomeEvent
    data object GenerateOutfits : HomeEvent
}
```

### OutfitResultEvent
```kotlin
sealed interface OutfitResultEvent {
    data class ToggleFavorite(val outfitId: String) : OutfitResultEvent
    data class SelectOutfit(val outfitId: String) : OutfitResultEvent
    data object GoBack : OutfitResultEvent
}
```

---

## 10. Side Effects (efectos de una sola vez)

Para navegación y mensajes que ocurren una vez (no son estado persistente), se usa un `Channel`:

```kotlin
// En el ViewModel
private val _navigationEffect = Channel<HomeNavigationEffect>()
val navigationEffect = _navigationEffect.receiveAsFlow()

// En la Screen (no en el Content)
LaunchedEffect(Unit) {
    viewModel.navigationEffect.collect { effect ->
        when (effect) {
            is HomeNavigationEffect.NavigateToOutfitResult ->
                navController.navigate(Screen.OutfitResult.createRoute(...))
        }
    }
}
```

El ViewModel nunca toca el NavController.

---

## 11. Repositories

### ¿Por qué interfaces?

Permiten tener múltiples implementaciones del mismo contrato:

```
SessionRepository  (interface)
    ├── FakeSessionRepository      ← hoy: simula sesión activa
    └── FirebaseSessionRepository  ← después: Firebase Auth

GarmentRepository  (interface)
    ├── FakeGarmentRepository      ← hoy: datos hardcodeados
    └── FirebaseGarmentRepository  ← después: Firestore

OutfitRepository   (interface)
    ├── FakeOutfitRepository       ← hoy: favoritos en memoria
    └── FirebaseOutfitRepository   ← después: Firestore
```

Cuando se integre Firebase, solo se reemplaza la implementación. ViewModels y UI no se tocan.

### Interfaces

```kotlin
interface SessionRepository {
    fun isLoggedIn(): Flow<Boolean>
    fun hasSeenOnboarding(): Flow<Boolean>
    suspend fun saveSession()           // sin userId: solo marca is_logged_in = true
    suspend fun clearSession()
    suspend fun markOnboardingAsSeen()
}

interface GarmentRepository {
    suspend fun getAllGarments(): List<Garment>
}

interface OutfitRepository {
    fun getFavorites(): Flow<List<Outfit>>
    suspend fun toggleFavorite(outfitId: String)
}
```

---

## 12. El único Use Case: GenerateOutfitsUseCase

Es el único Use Case porque es el único con **lógica real** que no pertenece ni al ViewModel ni al Repository.

```kotlin
class GenerateOutfitsUseCase(
    private val garmentRepository: GarmentRepository
) {
    suspend operator fun invoke(
        weather: WeatherCondition,
        occasion: Occasion,
        timeOfDay: TimeOfDay
    ): List<Outfit> {
        val allGarments = garmentRepository.getAllGarments()
        // Lógica de filtrado y combinación de prendas
        val filtered = allGarments.filter { garment ->
            weather in garment.suitableWeather &&
            occasion in garment.suitableOccasions
        }
        // Armar combinaciones válidas (superior + inferior + calzado)
        return buildOutfitCombinations(filtered)
    }
}
```

---

## 13. Datos mockeados

```
LocalGarmentDataSource.kt
    └── Lista de ~20 prendas hardcodeadas con:
            id, nombre, categoría, climas compatibles, ocasiones compatibles

FakeGarmentRepository   → devuelve LocalGarmentDataSource.garments
FakeOutfitRepository    → guarda favoritos en un Set en memoria
FakeSessionRepository   → isLoggedIn() devuelve flowOf(true) para desarrollar sin Login
```

---

## 14. Migración a Firebase

### Lo que NO cambia
- Modelos de dominio (Garment, Outfit, etc.)
- Interfaces de Repository
- ViewModels
- UI completa
- Navegación

### Lo que SÍ se reemplaza

| Hoy | Con Firebase |
|---|---|
| `FakeSessionRepository` | `FirebaseSessionRepository` |
| `FakeGarmentRepository` | `FirebaseGarmentRepository` |
| `FakeOutfitRepository` | `FirebaseOutfitRepository` |
| DataStore `is_logged_in` | `FirebaseAuth.currentUser != null` |
| `saveSession()` guarda flag manual | Firebase persiste sesión solo, no se llama |

### ¿Por qué Firebase y no Room para los datos de la app?

Se evaluaron tres opciones para persistir los datos de prendas, outfits y favoritos:

**Room solo** — Los datos viven únicamente en el teléfono. Si el usuario cambia de dispositivo, pierde todo. Las features sociales (compartir outfits, amigos) son imposibles porque los datos no están en la nube.

**Firebase Firestore solo (sin cache)** — Los datos viven en la nube. Sin internet la app no funciona.

**Firebase Firestore con persistencia offline** — Firebase cachea los datos localmente de forma automática con una sola línea de configuración:

```kotlin
val settings = firestoreSettings {
    isPersistenceEnabled = true  // cache offline automático
}
db.firestoreSettings = settings
```

Con esto Firebase maneja la redundancia solo:
- Con internet → lee de Firestore, actualiza el cache local
- Sin internet → lee del cache local transparentemente
- Vuelve internet → sincroniza los cambios automáticamente

| Opción | Cache offline | Multi-dispositivo | Features sociales | Complejidad |
|---|---|---|---|---|
| Solo Room | ✅ Siempre | ❌ No | ❌ No | Baja |
| Solo Firebase | ❌ No | ✅ Sí | ✅ Sí | Baja |
| Firebase + `isPersistenceEnabled` | ✅ Automático | ✅ Sí | ✅ Sí | Baja |
| Firebase + Room (offline-first) | ✅ Total control | ✅ Sí | ✅ Sí | Muy alta |

**Decisión: Firebase con `isPersistenceEnabled = true`**. Se obtiene cache offline, sincronización multi-dispositivo y soporte para features sociales, sin agregar complejidad. El patrón Firebase + Room (offline-first) queda descartado por ser arquitectura de apps de producción grandes, innecesaria para este scope.

> **Si los profesores solicitan Room:** gracias al patrón Repository, el cambio está contenido únicamente en la capa de datos. Se reemplaza `FirebaseGarmentRepository` por `RoomGarmentRepository` implementando la misma interface. ViewModels y UI no se tocan.

---

### Cumplimiento de la consigna de autenticación

> *"autenticación mediante email/contraseña y/o federada mediante Cloud Services"*

Firebase Auth cubre ambos:
- `signInWithEmailAndPassword()` → email/contraseña
- `signInWithCredential(GoogleAuthProvider...)` → federada (Google, Apple, Facebook)

---

## 15. Diagrama de capas

```
┌─────────────────────────────────────────────────┐
│                    UI LAYER                      │
│                                                  │
│  Screen ──── ViewModel ──── UiState (StateFlow)  │
│    │              │                              │
│    │       NavigationEffect (Channel)            │
│    │           UiEffect (Channel)                │
│  Content                                         │
│  (composables puros, reciben lambdas)            │
└──────────────────┬──────────────────────────────┘
                   │ llama directo (sin Use Cases intermedios)
┌──────────────────▼──────────────────────────────┐
│                 DOMAIN LAYER                     │
│                                                  │
│  GenerateOutfitsUseCase  ← único con lógica real │
│                                                  │
│  GarmentRepository  (interface)                  │
│  OutfitRepository   (interface)                  │
│  SessionRepository  (interface)                  │
│                                                  │
│  Modelos: Garment · Outfit · WeatherCondition    │
│           Occasion · TimeOfDay                   │
└──────────────────┬──────────────────────────────┘
                   │ implements
┌──────────────────▼──────────────────────────────┐
│                  DATA LAYER                      │
│                                                  │
│  FakeGarmentRepository   ──► FirebaseGarment...  │
│  FakeOutfitRepository    ──► FirebaseOutfit...   │
│  FakeSessionRepository   ──► FirebaseSession...  │
│                                                  │
│  LocalGarmentDataSource  (mock data)             │
│  PreferencesDataSource   (DataStore)             │
└─────────────────────────────────────────────────┘
```

---

## 16. Orden de implementación

| Paso | Qué | Estado |
|---|---|---|
| 1 | `Screen.kt` + rutas tipadas | Pendiente |
| 2 | `SessionRepository` + `PreferencesDataSource` (DataStore) | Pendiente |
| 3 | `MainViewModel` + `AuthNavGraph` + `AppNavGraph` | Pendiente |
| 4 | Modelos de dominio (Garment, Outfit, enums) | Pendiente |
| 5 | `FakeGarmentRepository` + `LocalGarmentDataSource` | Pendiente |
| 6 | `HomeScreen` + `HomeViewModel` + `HomeUiState` | Pendiente |
| 7 | `GenerateOutfitsUseCase` | Pendiente |
| 8 | `OutfitResultScreen` + `OutfitResultViewModel` | Pendiente |
| 9 | Firebase Auth (reemplaza FakeSessionRepository) | Futuro |
| 10 | Firebase Firestore (reemplaza Fake Repositories) | Futuro |

---

*Documento generado como referencia arquitectónica del proyecto Closify.*
