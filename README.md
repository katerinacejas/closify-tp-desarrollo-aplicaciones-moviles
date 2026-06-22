# Closify

TP integrador de Desarrollo de Aplicaciones Móviles — UADE.

Closify es una app Android para organizar tu guardarropa, generar outfits según el clima y la ocasión, planificar looks para días específicos y compartirlos con amigos.

---

## Funcionalidades

- **Generador de outfits** — seleccionás clima y ocasión, la app combina automáticamente tus prendas y sugiere hasta 5 outfits
- **Guardarropa** — cargás prendas desde la galería o la cámara, las clasificás por categoría, clima y ocasión
- **Planificador** — organizás outfits para fechas futuras con pronóstico del clima
- **Favoritos** — guardás los outfits que más te gustan con un nombre personalizado
- **Perfil** — tus posts de outfits favoritos y planificados, estadísticas de uso del guardarropa
- **Amigos** — seguís a otros usuarios, ves su feed de outfits, enviás y aceptás solicitudes
- **Notificaciones** — solicitudes de amistad y actividad en tus posts
- **Modo oscuro** — tema claro y oscuro con persistencia de preferencia

---

## Stack tecnológico

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje principal |
| Jetpack Compose | UI declarativa |
| Material 3 | Design system |
| Navigation Compose | Navegación entre pantallas |
| ViewModel + StateFlow | Manejo de estado (MVVM) |
| Coil | Carga de imágenes |
| Coroutines | Operaciones asincrónicas |

---

## Arquitectura

La app sigue **Clean Architecture** con tres capas:

```
ui/          → Screens, ViewModels, Components
domain/      → Models, UseCases
data/        → Repositories, MockClosifyData
```

**Patrones utilizados:**
- Single Activity con Compose Navigation
- MVVM con eventos sellados (sealed interface)
- Repository Pattern
- Use Cases para lógica de negocio compleja

**Persistencia actual:** datos en memoria con `MockClosifyData`. La arquitectura está preparada para migrar a Firebase Firestore y Storage.

---

## Requisitos

- Android Studio Hedgehog o superior
- SDK mínimo: API 30 (Android 11)
- SDK objetivo: API 36

---

## Instalación

```bash
git clone https://github.com/katerinacejas/closify-tp-desarrollo-aplicaciones-moviles
```

Abrí el proyecto en Android Studio y ejecutalo en un emulador o dispositivo con API 30+.

---

## Usuarios de prueba

| Usuario | Email | Password |
|---|---|---|
| Maria Cejas | maria@gmail.com | Maria123! |
| Juan Perez | juan@gmail.com | Juan123! |
| Ayelen Martinez | user_2@closify.com | Ayelen123! |

> Ayelen no tiene prendas cargadas — útil para probar el estado vacío del guardarropa y el generador de outfits.

---

## Documentación

Los diagramas de secuencia de los flujos principales están en [`docs/diagramas_secuencia.md`](docs/diagramas_secuencia.md).

Incluye flujos internos actuales y diagramas de integración futura con Firebase y OpenWeather API.

---

## Integraciones futuras

- **Firebase Authentication** — login con email/password y Google Sign-In
- **Firebase Firestore** — persistencia de prendas, outfits, posts y relaciones sociales
- **Firebase Storage** — almacenamiento de imágenes de prendas
- **OpenWeather API** — pronóstico real del clima para el planificador

---

## Equipo

Trabajo práctico integrador — Desarrollo de Aplicaciones Móviles, UADE.
