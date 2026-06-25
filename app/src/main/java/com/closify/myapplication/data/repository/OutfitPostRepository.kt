package com.closify.myapplication.data.repository

import android.content.Context
import com.closify.myapplication.data.local.AppDatabase
import com.closify.myapplication.data.local.mapper.toCommentEntity
import com.closify.myapplication.data.local.mapper.toDomain
import com.closify.myapplication.data.local.mapper.toEntity
import com.closify.myapplication.data.local.mapper.toFirestoreMap
import com.closify.myapplication.data.local.mapper.toLikeEntity
import com.closify.myapplication.data.local.mapper.toOutfitPostEntity
import com.closify.myapplication.domain.model.Comment
import com.closify.myapplication.domain.model.Like
import com.closify.myapplication.domain.model.OutfitPost
import com.closify.myapplication.domain.model.UserSummary
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class OutfitPostRepository private constructor(
    context: Context,
    private val notificationRepository: NotificationRepository = NotificationRepository.instance,
    private val userRepository: UserRepository = UserRepository.instance
) {
    private val outfitRepository: OutfitRepository by lazy { OutfitRepository.instance }
    private var syncedThisSession = false

    companion object {
        @Volatile private var _instance: OutfitPostRepository? = null

        fun initialize(context: Context) {
            if (_instance == null) {
                synchronized(this) {
                    if (_instance == null) {
                        _instance = OutfitPostRepository(context.applicationContext)
                    }
                }
            }
        }

        val instance: OutfitPostRepository
            get() = _instance ?: error("OutfitPostRepository.initialize(context) no fue llamado.")
    }

    private val db = AppDatabase.getInstance(context)
    private val postDao = db.outfitPostDao()
    private val userDao = db.userDao()
    private val firestore = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun getPost(postId: String): OutfitPost? {
        val entity = postDao.getPostById(postId) ?: return null
        return assemblePost(entity)
    }

    suspend fun getPostsByUser(userId: String): List<OutfitPost> {
        val entities = postDao.getPostsByUserId(userId)
        return entities.mapNotNull { assemblePost(it) }
    }

    suspend fun getPostsByAuthors(userIds: Set<String>): List<OutfitPost> {
        val entities = postDao.getPostsByAuthors(userIds.toList())
        return entities.mapNotNull { assemblePost(it) }
    }

    suspend fun addPost(post: OutfitPost): OutfitPost {
        postDao.upsertPost(post.toEntity())
        scope.launch {
            firestore.collection("outfit_posts")
                .document(post.id)
                .set(post.toFirestoreMap())
                .await()
        }
        return post
    }

    suspend fun updatePost(post: OutfitPost): OutfitPost {
        postDao.upsertPost(post.toEntity())
        scope.launch {
            firestore.collection("outfit_posts")
                .document(post.id)
                .update(post.toFirestoreMap())
                .await()
        }
        return post
    }

    suspend fun toggleLike(postId: String, user: UserSummary): OutfitPost? {
        val post = getPost(postId) ?: return null
        val alreadyLiked = post.likedBy.any { it.user.id == user.id }
        
        if (alreadyLiked) {
            postDao.deleteLike(postId, user.id)
            scope.launch {
                firestore.collection("outfit_posts/$postId/likes").document(user.id).delete().await()
            }
        } else {
            val like = Like(
                id = UUID.randomUUID().toString(),
                user = user,
                createdAt = currentDate()
            )
            postDao.upsertLike(like.toEntity(postId))
            scope.launch {
                firestore.collection("outfit_posts/$postId/likes").document(user.id).set(like.toFirestoreMap()).await()
            }
            notificationRepository.createPostLikeNotification(post = post, sender = user)
        }
        
        return getPost(postId)
    }

    suspend fun addComment(postId: String, user: UserSummary, text: String): OutfitPost? {
        val cleanText = text.trim()
        if (cleanText.isBlank()) return null

        val comment = Comment(
            id = UUID.randomUUID().toString(),
            user = user,
            text = cleanText,
            createdAt = currentDate()
        )
        
        postDao.upsertComment(comment.toEntity(postId))
        scope.launch {
            firestore.collection("outfit_posts/$postId/comments").document(comment.id).set(comment.toFirestoreMap()).await()
        }
        
        val post = getPost(postId)
        if (post != null) {
            notificationRepository.createPostCommentNotification(
                post = post,
                commentId = comment.id,
                sender = user
            )
        }
        return post
    }

    suspend fun updatePostTitle(postId: String, title: String): OutfitPost? {
        val entity = postDao.getPostById(postId) ?: return null
        val updatedEntity = entity.copy(title = title.take(100).ifBlank { null })
        postDao.upsertPost(updatedEntity)
        scope.launch {
            firestore.collection("outfit_posts").document(postId).update("title", updatedEntity.title).await()
        }
        return assemblePost(updatedEntity)
    }

    suspend fun deletePost(postId: String) {
        postDao.deletePostById(postId)
        scope.launch {
            firestore.collection("outfit_posts").document(postId).delete().await()
        }
    }

    private suspend fun assemblePost(entity: com.closify.myapplication.data.local.entity.OutfitPostEntity): OutfitPost? {
        val author = userDao.getById(entity.authorId)?.toDomain()?.toSummary() ?: return null
        val outfit = outfitRepository.getOutfitById(entity.outfitId) ?: return null
            
        val likes = postDao.getLikesForPost(entity.id).mapNotNull { likeEntity ->
            val likeUser = userDao.getById(likeEntity.userId)?.toDomain()?.toSummary()
            if (likeUser != null) likeEntity.toDomain(likeUser) else null
        }
        
        val comments = postDao.getCommentsForPost(entity.id).mapNotNull { commentEntity ->
            val commentUser = userDao.getById(commentEntity.userId)?.toDomain()?.toSummary()
            if (commentUser != null) commentEntity.toDomain(commentUser) else null
        }
        
        return entity.toDomain(author, outfit, likes, comments)
    }

    suspend fun syncFromFirestore() {
        if (syncedThisSession) return
        try {
            // This is a simplified global sync. In a real app we might only sync followed users.
            val snapshot = firestore.collection("outfit_posts").get().await()
            val posts = snapshot.documents.mapNotNull { it.toOutfitPostEntity() }
            postDao.upsertPosts(posts)
            
            // Sync likes and comments for each post (can be heavy, should be optimized)
            posts.forEach { post ->
                val likesSnapshot = firestore.collection("outfit_posts/${post.id}/likes").get().await()
                val likes = likesSnapshot.documents.mapNotNull { it.toLikeEntity(post.id) }
                postDao.upsertLikes(likes)
                
                val commentsSnapshot = firestore.collection("outfit_posts/${post.id}/comments").get().await()
                val comments = commentsSnapshot.documents.mapNotNull { it.toCommentEntity(post.id) }
                postDao.upsertComments(comments)
            }
            syncedThisSession = true
        } catch (e: Exception) {
            android.util.Log.w("OutfitPostRepository", "syncFromFirestore failed: ${e.message}")
        }
    }

    fun resetSessionSync() { syncedThisSession = false }

    private fun currentDate(): String = LocalDate.now().format(
        DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es-AR"))
    )
}
