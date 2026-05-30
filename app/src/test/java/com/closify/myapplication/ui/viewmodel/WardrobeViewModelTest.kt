package com.closify.myapplication.ui.viewmodel

import com.closify.myapplication.data.repository.MockClosifyData
import com.closify.myapplication.data.repository.UserRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WardrobeViewModelTest {

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
    fun searchQuery_filtersGarmentsAsUserTypes() {
        val viewModel = WardrobeViewModel()

        viewModel.onEvent(WardrobeEvent.SearchQueryChanged("blu"))

        val state = viewModel.uiState.value
        assertEquals(WardrobeFilter.ALL, state.selectedFilter)
        assertTrue(state.filteredGarments.isNotEmpty())
        assertTrue(state.filteredGarments.all { it.name.contains("blu", ignoreCase = true) })
    }

    @Test
    fun clearingSearch_showsAllWardrobeGarments() {
        val viewModel = WardrobeViewModel()
        val allGarmentsCount = viewModel.uiState.value.allGarments.size

        viewModel.onEvent(WardrobeEvent.SearchQueryChanged("blu"))
        viewModel.onEvent(WardrobeEvent.SearchQueryChanged(""))

        val state = viewModel.uiState.value
        assertEquals(WardrobeFilter.ALL, state.selectedFilter)
        assertEquals(allGarmentsCount, state.filteredGarments.size)
    }
}
