# Diagramas de Secuencia — Closify

## Flujos internos

### Login

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as LoginScreen
    participant VM as LoginViewModel
    participant R as UserRepository
    participant M as MockClosifyData

    U->>S: Ingresa email y password
    S->>VM: Submit
    VM->>VM: Valida campos
    VM->>R: login(email, password)
    R->>M: findAuthUser(email, password)
    M-->>R: Usuario encontrado o null
    R-->>VM: Result success o failure
    VM-->>S: Navega a Home o muestra error
```

### Registro

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as RegisterScreen
    participant VM as RegisterViewModel
    participant R as UserRepository
    participant M as MockClosifyData

    U->>S: Paso 1 - email, username, password
    S->>VM: NextStep
    VM->>R: isUsernameAvailable
    R->>M: Busca en listas de usuarios
    M-->>VM: disponible si o no
    VM-->>S: Avanza a paso 2 o muestra errores
    U->>S: Paso 2 - nombre, fecha, bio
    S->>VM: Submit
    VM->>R: register con todos los datos
    R->>M: Crea y agrega nuevo usuario
    M-->>R: Usuario creado
    R-->>VM: Result success
    VM-->>S: Navega a Home
```

### Generar outfits

```mermaid
sequenceDiagram
    actor U as Usuario
    participant H as HomeScreen
    participant HVM as HomeViewModel
    participant UC as GenerateOutfitsUseCase
    participant GR as GarmentRepository
    participant OR as OutfitRepository

    U->>H: Selecciona clima y ocasion
    U->>H: Presiona Generar
    H->>HVM: GenerateOutfits
    HVM->>GR: getAllByUserId
    GR-->>HVM: prendas del usuario
    HVM->>UC: invoke con clima y ocasion
    UC->>UC: Filtra y genera combinaciones
    UC-->>HVM: lista de outfits
    HVM->>OR: guarda currentOutfits
    HVM-->>H: Navega a OutfitResult
```

### Guardar outfits favoritos

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as OutfitResultScreen
    participant OVM as OutfitResultViewModel
    participant SF as SaveFavoritesScreen
    participant SVM as SaveFavoritesViewModel
    participant OR as OutfitRepository
    participant M as MockClosifyData

    U->>S: Marca favoritos
    S->>OVM: ToggleFavorite por cada outfit
    U->>S: Presiona Continuar
    S->>OVM: Continue
    OVM->>OR: pendingFavorites con seleccionados
    OVM-->>S: Navega a SaveFavorites
    U->>SF: Escribe nombres y presiona Guardar
    SF->>SVM: Save
    SVM->>OR: saveFavorites con nombres
    OR->>M: addOutfitPost tipo FAVORITE
    SVM-->>SF: Muestra confirmacion
```

### Agregar prenda

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as AddGarmentScreen
    participant CVM as CameraViewModel
    participant GVM as ClassifyGarmentViewModel
    participant GR as GarmentRepository
    participant M as MockClosifyData

    U->>S: Selecciona imagen
    S->>CVM: SetImageUri
    S-->>U: Navega a ClassifyGarment
    U->>S: Paso 1 - nombre, categoria, clima
    S->>GVM: Continue
    GVM->>GVM: Valida campos
    GVM-->>S: Avanza a paso 2 o muestra errores
    U->>S: Paso 2 - ocasion
    S->>GVM: Save
    GVM->>GVM: Construye Garment con datos del formulario
    GVM->>GR: addGarment
    GR->>M: agrega prenda a la lista
    GVM-->>S: Muestra confirmacion
```

### Planificador

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as PlannerScreen
    participant VM as PlannerViewModel
    participant OR as OutfitRepository
    participant M as MockClosifyData

    VM->>OR: getPlannedPosts
    OR->>M: lee posts planificados
    M-->>VM: posts del usuario
    U->>S: Selecciona fecha
    S->>VM: onDateSelected
    U->>S: Confirma y avanza
    S->>VM: onConfirmSelection
    U->>S: Scrollea carruseles de prendas
    S->>VM: onCentered por categoria
    U->>S: Confirma seleccion
    S->>VM: onContinueToPlanningReview
    U->>S: Escribe titulo y guarda
    S->>VM: onSavePlanning
    VM->>OR: savePlannedOutfitPost
    OR->>M: addOutfitPost tipo PLANNED
    VM-->>S: Muestra confirmacion
```

### Amigos

```mermaid
sequenceDiagram
    actor U as Usuario
    participant S as FriendsScreen
    participant VM as FriendsViewModel
    participant SR as SocialRepository
    participant M as MockClosifyData

    S->>VM: refresh
    VM->>SR: getFriends y getPosts
    SR->>M: lee datos
    M-->>VM: amigos y feed
    VM-->>S: muestra lista y posts

    U->>S: Busca usuario
    S->>VM: onSearchQueryChange
    VM->>SR: getAllUserSummaries
    SR-->>VM: resultados
    VM-->>S: muestra con estado FRIEND o NONE

    U->>S: Presiona Agregar
    S->>VM: onToggleFriend
    VM->>SR: sendFriendRequest
    SR->>M: crea FriendRequest PENDING
    VM-->>S: boton cambia a Pendiente

    U->>S: Acepta solicitud
    S->>VM: onRespondToFriendRequest true
    VM->>SR: respondToFriendRequest
    SR->>M: addFriend bidireccional
    VM-->>S: feed actualizado con nuevo amigo

    U->>S: Presiona Eliminar
    S->>VM: onToggleFriend
    VM->>SR: removeFriend
    SR->>M: removeFriend bidireccional
    VM-->>S: feed actualizado
```

---

## Flujos de integracion futura

### Firebase Authentication

```mermaid
sequenceDiagram
    participant VM as ViewModel
    participant UR as UserRepository
    participant FA as Firebase Auth

    VM->>UR: login(email, password)
    UR->>FA: signInWithEmailAndPassword
    FA-->>UR: FirebaseUser o error
    UR-->>VM: Result success o failure

    VM->>UR: register(email, password, username)
    UR->>FA: createUserWithEmailAndPassword
    FA-->>UR: FirebaseUser
    UR->>FA: updateProfile con username
    UR-->>VM: Result success

    VM->>UR: logout
    UR->>FA: signOut
    FA-->>UR: ok
    UR-->>VM: navega a Login
```

### Firebase Storage y Firestore — Guardar prenda

```mermaid
sequenceDiagram
    participant VM as ClassifyGarmentViewModel
    participant GR as GarmentRepository
    participant FS as Firebase Storage
    participant DB as Firestore

    VM->>GR: addGarment con imagen local
    GR->>FS: sube imagen con ruta del usuario
    FS-->>GR: url remota de descarga
    GR->>DB: guarda documento en coleccion garments
    DB-->>GR: confirmacion
    GR-->>VM: Result success
```

### Firestore — Cargar prendas

```mermaid
sequenceDiagram
    participant VM as HomeViewModel
    participant GR as GarmentRepository
    participant DB as Firestore

    VM->>GR: getAllByUserId
    GR->>DB: consulta garments por ownerUserId
    DB-->>GR: documentos encontrados
    GR->>GR: mapea a lista de Garment
    GR-->>VM: lista de prendas
```

### Firestore — Guardar outfit favorito

```mermaid
sequenceDiagram
    participant VM as SaveFavoritesViewModel
    participant OR as OutfitRepository
    participant DB as Firestore

    VM->>OR: saveFavorites
    OR->>DB: guarda cada outfit en coleccion outfitPosts
    DB-->>OR: confirmaciones
    OR-->>VM: Result success
    VM-->>VM: muestra confirmacion
```

### Firestore — Amigos

```mermaid
sequenceDiagram
    participant VM as FriendsViewModel
    participant SR as SocialRepository
    participant DB as Firestore

    VM->>SR: sendFriendRequest
    SR->>DB: crea documento en friendRequests con status PENDING
    SR->>DB: crea notificacion para el receptor
    DB-->>SR: confirmaciones
    SR-->>VM: solicitud creada

    VM->>SR: respondToFriendRequest aceptar
    SR->>DB: actualiza status a ACCEPTED
    SR->>DB: crea documento en friendships
    SR->>DB: crea notificacion de aceptacion
    DB-->>SR: confirmaciones
    SR-->>VM: amistad creada
```

### Retrofit — Pronostico del clima

```mermaid
sequenceDiagram
    participant VM as PlannerViewModel
    participant WR as WeatherRepository
    participant API as OpenWeather API

    VM->>WR: getForecast con coordenadas
    WR->>API: GET forecast con apiKey
    API-->>WR: respuesta JSON con pronostico
    WR->>WR: mapea a lista de PlannerForecastDay
    WR-->>VM: pronostico de 7 dias
    VM-->>VM: actualiza calendario con clima
```
