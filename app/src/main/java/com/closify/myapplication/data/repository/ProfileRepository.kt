package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.Garment
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserProfile
import com.closify.myapplication.domain.model.UserSummary

class ProfileRepository(
    private val wardrobeRepository: WardrobeRepository = WardrobeRepository.instance,
    private val socialRepository: SocialRepository = SocialRepository.instance
) {

    companion object {
        val instance = ProfileRepository()
    }

    fun getProfile(userId: String = MockClosifyData.CURRENT_USER_ID): UserProfile =
        requireNotNull(MockClosifyData.userById(userId)).profile

    fun getFriends(userId: String = MockClosifyData.CURRENT_USER_ID): List<UserSummary> =
        socialRepository.getFriends(userId)

    fun getWardrobeGarments(userId: String = MockClosifyData.CURRENT_USER_ID): List<Garment> =
        wardrobeRepository.getAllGarments(userId)

    fun getWardrobeUsagePercentage(userId: String = MockClosifyData.CURRENT_USER_ID): Int =
        wardrobeRepository.calculateWardrobeUsagePercentage(getPosts(userId))

    fun getPosts(userId: String = MockClosifyData.CURRENT_USER_ID): List<OutfitPost> =
        MockClosifyData.outfitPosts
            .filter { it.author.id == userId }
            .sortedByDescending { it.createdAt }
}
