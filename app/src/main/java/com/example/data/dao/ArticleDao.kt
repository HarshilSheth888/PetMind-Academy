package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ArticleProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {
  @Query("SELECT * FROM article_progress")
  fun getAllArticleProgress(): Flow<List<ArticleProgressEntity>>

  @Query("SELECT * FROM article_progress WHERE articleId = :articleId LIMIT 1")
  suspend fun getProgressForArticle(articleId: String): ArticleProgressEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertArticleProgress(progress: ArticleProgressEntity)

  @Query("SELECT COUNT(*) FROM article_progress WHERE isRead = 1")
  fun getReadArticlesCount(): Flow<Int>

  @Query("SELECT COUNT(*) FROM article_progress WHERE isBookmarked = 1")
  fun getBookmarkedArticlesCount(): Flow<Int>
}
