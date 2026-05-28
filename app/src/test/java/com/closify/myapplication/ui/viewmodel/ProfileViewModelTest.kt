package com.closify.myapplication.ui.viewmodel

import com.closify.myapplication.data.repository.MockClosifyData
import com.closify.myapplication.data.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProfileViewModelTest {

    @Before
    fun setUp() = runBlocking {
        MockClosifyData.resetCurrentUserFriends()
        UserRepository.instance.login("maria@gmail.com", "Maria123!")
        Unit
    }

    @After
    fun tearDown() = runBlocking {
        MockClosifyData.resetCurrentUserFriends()
        UserRepository.instance.login("maria@gmail.com", "Maria123!")
        Unit
    }

    @Test
    fun mariaLogin_loadsMariaProfile() = runBlocking {
        UserRepository.instance.login("maria@gmail.com", "Maria123!")

        val state = ProfileViewModel().uiState.value

        assertEquals(MockClosifyData.MARIA_USER_ID, state.userId)
        assertEquals("Maria Cejas", state.name)
        assertEquals("@maria_cejas", state.username)
        assertTrue(state.friendsCount > 0)
        assertTrue(state.posts.isNotEmpty())
        assertTrue(state.posts.all { it.author.id == MockClosifyData.MARIA_USER_ID })
    }

    @Test
    fun juanLogin_loadsJuanProfile() = runBlocking {
        UserRepository.instance.login("juan@gmail.com", "Juan123!")

        val state = ProfileViewModel().uiState.value

        assertEquals(MockClosifyData.JUAN_USER_ID, state.userId)
        assertEquals("Juan Perez", state.name)
        assertEquals("@juan_perez", state.username)
        assertTrue(state.friendsCount > 0)
        assertTrue(state.posts.isNotEmpty())
        assertTrue(state.posts.all { it.author.id == MockClosifyData.JUAN_USER_ID })
    }
}
