package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pets")
data class PetEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val species: String, // "DOG", "CAT", "OTHER"
  val breed: String,
  val ageMonths: Int,
  val gender: String, // "BOY", "GIRL"
  val avatarIndex: Int = 0,
  val personalityTags: String = "Playful, Curious",
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "training_logs")
data class TrainingLogEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val petId: Long,
  val guideId: String,
  val guideTitle: String,
  val durationSeconds: Int,
  val repetitions: Int,
  val successCount: Int,
  val difficultyRating: Int, // 1 to 5
  val notes: String,
  val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "skill_progress")
data class SkillProgressEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val petId: Long,
  val guideId: String,
  val completedStepsCount: Int = 0,
  val totalStepsCount: Int = 4,
  val isMastered: Boolean = false,
  val lastPracticedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "article_progress")
data class ArticleProgressEntity(
  @PrimaryKey
  val articleId: String,
  val isBookmarked: Boolean = false,
  val isRead: Boolean = false,
  val quizScore: Int = -1, // -1 means unattempted
  val lastReadTimestamp: Long = 0
)

@Entity(tableName = "milestones")
data class MilestoneEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val petId: Long,
  val title: String,
  val description: String,
  val category: String, // "Mentality", "Training", "Enrichment", "Bonding"
  val isUnlocked: Boolean = false,
  val unlockedDate: Long = 0,
  val iconType: String = "STAR"
)
