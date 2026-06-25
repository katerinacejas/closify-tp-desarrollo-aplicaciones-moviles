package com.closify.myapplication.data.local.mapper

import com.closify.myapplication.data.local.entity.CommentEntity
import com.closify.myapplication.data.local.entity.LikeEntity
import com.closify.myapplication.data.local.entity.OutfitPostEntity
import com.closify.myapplication.domain.model.Comment
import com.closify.myapplication.domain.model.Like
import com.closify.myapplication.domain.model.Outfit
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.OutfitPostType
import com.closify.myapplication.domain.model.UserSummary
import com.google.firebase.firestore.DocumentSnapshot

fun OutfitPostEntity.toDomain(
    author: UserSummary,
    outfit: Outfit,
    likes: List<Like>,
    comments: List<Comment>
): OutfitPost = OutfitPost(
    id = id,
    author = author,
    outfit = outfit,
    title = title,
    type = OutfitPostType.valueOf(type),
    createdAt = createdAt,
    plannedDate = plannedDate,
    likedBy = likes,
    comments = comments
)

fun OutfitPost.toEntity(): OutfitPostEntity = OutfitPostEntity(
    id = id,
    authorId = author.id,
    outfitId = outfit.id,
    title = title,
    type = type.name,
    createdAt = createdAt,
    plannedDate = plannedDate
)

fun OutfitPost.toFirestoreMap(): Map<String, Any> = mapOf(
    "authorId" to author.id,
    "outfitId" to outfit.id,
    "title" to (title ?: ""),
    "type" to type.name,
    "createdAt" to createdAt,
    "plannedDate" to (plannedDate ?: "")
)

fun DocumentSnapshot.toOutfitPostEntity(): OutfitPostEntity? {
    return try {
        OutfitPostEntity(
            id = id,
            authorId = getString("authorId") ?: "",
            outfitId = getString("outfitId") ?: "",
            title = getString("title")?.ifEmpty { null },
            type = getString("type") ?: OutfitPostType.FAVORITE.name,
            createdAt = getString("createdAt") ?: "",
            plannedDate = getString("plannedDate")?.ifEmpty { null }
        )
    } catch (e: Exception) { null }
}

// Like Mappers
fun LikeEntity.toDomain(user: UserSummary): Like = Like(
    id = id,
    user = user,
    createdAt = createdAt
)

fun Like.toEntity(postId: String): LikeEntity = LikeEntity(
    id = id,
    postId = postId,
    userId = user.id,
    createdAt = createdAt
)

fun Like.toFirestoreMap(): Map<String, Any> = mapOf(
    "userId" to user.id,
    "createdAt" to createdAt
)

fun DocumentSnapshot.toLikeEntity(postId: String): LikeEntity? {
    return try {
        LikeEntity(
            id = id,
            postId = postId,
            userId = getString("userId") ?: "",
            createdAt = getString("createdAt") ?: ""
        )
    } catch (e: Exception) { null }
}

// Comment Mappers
fun CommentEntity.toDomain(user: UserSummary): Comment = Comment(
    id = id,
    user = user,
    text = text,
    createdAt = createdAt
)

fun Comment.toEntity(postId: String): CommentEntity = CommentEntity(
    id = id,
    postId = postId,
    userId = user.id,
    text = text,
    createdAt = createdAt
)

fun Comment.toFirestoreMap(): Map<String, Any> = mapOf(
    "userId" to user.id,
    "text" to text,
    "createdAt" to createdAt
)

fun DocumentSnapshot.toCommentEntity(postId: String): CommentEntity? {
    return try {
        CommentEntity(
            id = id,
            postId = postId,
            userId = getString("userId") ?: "",
            text = getString("text") ?: "",
            createdAt = getString("createdAt") ?: ""
        )
    } catch (e: Exception) { null }
}
