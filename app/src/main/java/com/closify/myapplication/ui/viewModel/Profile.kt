package com.closify.myapplication.ui.viewModel

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import com.closify.myapplication.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val name: String = "Katerina Cejas",
    val username: String = "@kate_cejas_1999",
    val bio: String = "hola soy kate, me gusta planificar outfits porque sino colapso a ultimo momento. me gusta el rosita",
    val birthDate: String = "3 de septiembre de 1999",
    val friendsCount: Int = 12,
    val garmentsCount: Int = 23,
    val wardrobeUsagePercentage: Int = 70,
    val favoriteOutfitsCount: Int = 2,
    val plannedOutfitsCount: Int = 2,
    @param:DrawableRes val bannerImageResId: Int? = R.drawable.banner_default,
    @param:DrawableRes val profileImageResId: Int? = R.drawable.avatar_default,
    val friends: List<ProfileFriend> = emptyList(),
    val outfits: List<ProfileOutfit> = emptyList()
)

data class ProfileFriend(
    val id: String,
    val name: String,
    val username: String
)

data class ProfileOutfit(
    val id: String,
    val title: String,
    val date: String,
    val likes: Int,
    val comments: Int,
    val isLiked: Boolean = false,
    val garments: List<String>
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadMockData()
    }

    private fun loadMockData() {
        _uiState.value = _uiState.value.copy(
            bannerImageResId = R.drawable.banner_default,
            profileImageResId = R.drawable.avatar_default,
            friends = listOf(
                ProfileFriend("1", "Ayelen Martinez", "@aye_martinez"),
                ProfileFriend("2", "Milagros Fava", "@miliifava"),
                ProfileFriend("3", "Ailen Garcia", "@ailu_garcia"),
                ProfileFriend("4", "Ayelen Balmaceda", "@ayee_balmaceda_1"),
                ProfileFriend("5", "Camila Martinez", "@camii_martinez")
            ),
            outfits = listOf(
                ProfileOutfit(
                    id = "1",
                    title = "Mi outfit para mi cumpleanos! <3",
                    date = "Anadido a favoritos el: 25 de mayo de 2026",
                    likes = 3,
                    comments = 2,
                    isLiked = false,
                    garments = listOf("blusa_1", "jean_1", "zapatillas_blancas")
                ),
                ProfileOutfit(
                    id = "2",
                    title = "El outfit que me voy a poner en el cumple de mi novio <3",
                    date = "Planificado para el dia: 25 de diciembre de 2026",
                    likes = 5,
                    comments = 1,
                    isLiked = false,
                    garments = listOf("vestido_floral", "botas_negras")
                )
            )
        )
    }

    fun onLikeClick(outfitId: String) {
        _uiState.value = _uiState.value.copy(
            outfits = _uiState.value.outfits.map { outfit ->
                if (outfit.id == outfitId) {
                    val nextLiked = !outfit.isLiked
                    outfit.copy(
                        isLiked = nextLiked,
                        likes = outfit.likes + if (nextLiked) 1 else -1
                    )
                } else {
                    outfit
                }
            }
        )
    }
}
