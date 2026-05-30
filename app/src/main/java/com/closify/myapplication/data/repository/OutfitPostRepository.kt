package com.closify.myapplication.data.repository

import com.closify.myapplication.domain.model.Comment
import com.closify.myapplication.domain.model.Like
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserSummary

class OutfitPostRepository(
    private val notificationRepository: NotificationRepository = NotificationRepository.instance
) {

    companion object {
        val instance = OutfitPostRepository()
    }

    fun getPost(postId: String): OutfitPost? =
        MockClosifyData.outfitPosts.firstOrNull { it.id == postId }

    fun getPostsByUser(userId: String): List<OutfitPost> =
        sortNewestFirst(MockClosifyData.outfitPosts.filter { it.author.id == userId })

    fun getPostsByAuthors(userIds: Set<String>): List<OutfitPost> =
        sortNewestFirst(MockClosifyData.outfitPosts.filter { it.author.id in userIds })

    fun addPost(post: OutfitPost): OutfitPost =
        MockClosifyData.addOutfitPost(post)

    fun updatePost(post: OutfitPost): OutfitPost =
        MockClosifyData.updateOutfitPost(post)

    fun toggleLike(postId: String, user: UserSummary): OutfitPost? {
        val post = getPost(postId) ?: return null
        val alreadyLiked = post.likedBy.any { it.user.id == user.id }
        val updatedPost = post.copy(
            likedBy = if (alreadyLiked) {
                post.likedBy.filterNot { it.user.id == user.id }
            } else {
                listOf(
                    Like(
                        id = "like_${user.id}_${post.id}_${post.likedBy.size + 1}",
                        user = user,
                        createdAt = MockClosifyData.CURRENT_DATE_LABEL
                    )
                ) + post.likedBy
            }
        )

        updatePost(updatedPost)
        if (!alreadyLiked) {
            notificationRepository.createPostLikeNotification(post = updatedPost, sender = user)
        }
        return updatedPost
    }

    fun addComment(postId: String, user: UserSummary, text: String): OutfitPost? {
        val cleanText = text.trim()
        if (cleanText.isBlank()) return null

        val post = getPost(postId) ?: return null
        val comment = Comment(
            id = "comment_${user.id}_${post.id}_${post.comments.size + 1}",
            user = user,
            text = cleanText,
            createdAt = MockClosifyData.CURRENT_DATE_LABEL
        )
        val updatedPost = post.copy(comments = post.comments + comment)

        updatePost(updatedPost)
        notificationRepository.createPostCommentNotification(
            post = updatedPost,
            commentId = comment.id,
            sender = user
        )
        return updatedPost
    }

    fun updatePostTitle(postId: String, title: String): OutfitPost? {
        val post = getPost(postId) ?: return null
        val updatedPost = post.copy(title = title.take(100).ifBlank { null })
        return updatePost(updatedPost)
    }

    fun deletePost(postId: String) {
        MockClosifyData.deleteOutfitPost(postId)
    }

    fun sortNewestFirst(posts: List<OutfitPost>): List<OutfitPost> =
        posts.sortedByDescending { it.createdAt.toMockDateOrder() }

    private fun String.toMockDateOrder(): Int {
        val parts = split(" de ")
        if (parts.size != 3) return 0

        val day = parts[0].toIntOrNull() ?: return 0
        val month = when (parts[1].lowercase()) {
            "enero" -> 1
            "febrero" -> 2
            "marzo" -> 3
            "abril" -> 4
            "mayo" -> 5
            "junio" -> 6
            "julio" -> 7
            "agosto" -> 8
            "septiembre" -> 9
            "octubre" -> 10
            "noviembre" -> 11
            "diciembre" -> 12
            else -> return 0
        }
        val year = parts[2].toIntOrNull() ?: return 0

        return year * 10_000 + month * 100 + day
    }
}
