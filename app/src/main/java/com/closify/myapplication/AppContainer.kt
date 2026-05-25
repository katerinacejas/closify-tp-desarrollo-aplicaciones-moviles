package com.closify.myapplication

import com.closify.myapplication.data.repository.FakeUserRepository
import com.closify.myapplication.domain.repository.UserRepository

/**
 * Contenedor de dependencias de la app.
 * Centraliza la creación de repositorios para que todos los ViewModels
 * compartan la misma instancia.
 *
 * TODO: reemplazar por Hilt cuando se integre Firebase.
 */
object AppContainer {
    val userRepository: UserRepository = FakeUserRepository()
}
