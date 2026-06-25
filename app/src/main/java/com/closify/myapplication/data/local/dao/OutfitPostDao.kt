package com.closify.myapplication.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.closify.myapplication.data.local.entity.CommentEntity
import com.closify.myapplication.data.local.entity.LikeEntity
import com.closify.myapplication.data.local.entity.OutfitPostEntity

@Dao
interface OutfitPostDao {
    @Query("SELECT * FROM outfit_posts WHERE authorId = :userId ORDER BY createdAt DESC")
    suspend fun getPostsByUserId(userId: String): List<OutfitPostEntity>

    @Query("SELECT * FROM outfit_posts WHERE authorId IN (:userIds) ORDER BY createdAt DESC")
    suspend fun getPostsByAuthors(userIds: List<String>): List<OutfitPostEntity>

    @Query("SELECT * FROM outfit_posts WHERE id = :postId")
    suspend fun getPostById(postId: String): OutfitPostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPost(post: OutfitPostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPosts(posts: List<OutfitPostEntity>)

    @Query("DELETE FROM outfit_posts WHERE id = :postId")
    suspend fun deletePostById(postId: String)

    // Likes
    @Query("SELECT * FROM likes WHERE postId = :postId")
    suspend fun getLikesForPost(postId: String): List<LikeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLike(like: LikeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLikes(likes: List<LikeEntity>)

    @Query("DELETE FROM likes WHERE postId = :postId AND userId = :userId")
    suspend fun deleteLike(postId: String, userId: String)

    // Comments
    @Query("SELECT * FROM comments WHERE postId = :postId ORDER BY createdAt ASC")
    suspend fun getCommentsForPost(postId: String): List<CommentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertComment(comment: CommentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertComments(comments: List<CommentEntity>)

    @Query("DELETE FROM comments WHERE id = :commentId")
    suspend fun deleteCommentById(commentId: String)
    
    @Query("DELETE FROM outfit_posts")
    suspend fun deleteAllPosts()
}
