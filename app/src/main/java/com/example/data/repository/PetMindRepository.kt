package com.example.data.repository

import com.example.data.ArticlesData
import com.example.data.TrainingGuidesData
import com.example.data.dao.ArticleDao
import com.example.data.dao.MilestoneDao
import com.example.data.dao.PetDao
import com.example.data.dao.TrainingDao
import com.example.data.model.ArticleProgressEntity
import com.example.data.model.MilestoneEntity
import com.example.data.model.PetEntity
import com.example.data.model.SkillProgressEntity
import com.example.data.model.TrainingLogEntity
import kotlinx.coroutines.flow.Flow

class PetMindRepository(
  private val petDao: PetDao,
  private val trainingDao: TrainingDao,
  private val articleDao: ArticleDao,
  private val milestoneDao: MilestoneDao
) {
  val allPets: Flow<List<PetEntity>> = petDao.getAllPets()
  val allLogs: Flow<List<TrainingLogEntity>> = trainingDao.getAllLogs()
  val allArticleProgress: Flow<List<ArticleProgressEntity>> = articleDao.getAllArticleProgress()
  val readArticlesCount: Flow<Int> = articleDao.getReadArticlesCount()
  val bookmarkedArticlesCount: Flow<Int> = articleDao.getBookmarkedArticlesCount()

  fun getLogsForPet(petId: Long): Flow<List<TrainingLogEntity>> = trainingDao.getLogsForPet(petId)
  fun getSkillProgressForPet(petId: Long): Flow<List<SkillProgressEntity>> = trainingDao.getSkillProgressForPet(petId)
  fun getMilestonesForPet(petId: Long): Flow<List<MilestoneEntity>> = milestoneDao.getMilestonesForPet(petId)
  fun getTotalTrainingTimeSeconds(petId: Long): Flow<Int?> = trainingDao.getTotalTrainingTimeSeconds(petId)
  fun getTotalSessionCount(petId: Long): Flow<Int> = trainingDao.getTotalSessionCount(petId)
  fun getUnlockedMilestonesCount(petId: Long): Flow<Int> = milestoneDao.getUnlockedMilestonesCount(petId)

  suspend fun ensureDefaultDataSeeded() {
    val count = petDao.getPetCount()
    if (count == 0) {
      val defaultPetId = petDao.insertPet(
        PetEntity(
          name = "Milo",
          species = "DOG",
          breed = "Golden Retriever",
          ageMonths = 14,
          gender = "BOY",
          avatarIndex = 0,
          personalityTags = "Eager to please, Friendly, Food Motivated",
          notes = "Working on loose leash manners and impulse control around squirrels."
        )
      )

      // Seed initial milestones for default pet
      val initialMilestones = listOf(
        MilestoneEntity(
          petId = defaultPetId,
          title = "Marker Clicker Master",
          description = "Charged the clicker and completed first 10 reward associations",
          category = "Training",
          isUnlocked = true,
          unlockedDate = System.currentTimeMillis() - 86400000L * 3,
          iconType = "STAR"
        ),
        MilestoneEntity(
          petId = defaultPetId,
          title = "Zen Master",
          description = "Maintained eye contact and left a high-value treat on open palm",
          category = "Manners",
          isUnlocked = true,
          unlockedDate = System.currentTimeMillis() - 86400000L,
          iconType = "SHIELD"
        ),
        MilestoneEntity(
          petId = defaultPetId,
          title = "5-Minute Loose Leash",
          description = "Walked 50 meters on slack leash without stopping",
          category = "Training",
          isUnlocked = false,
          iconType = "TROPHY"
        ),
        MilestoneEntity(
          petId = defaultPetId,
          title = "Behavior Scholar",
          description = "Read 3 expert behavior articles and completed quizzes",
          category = "Mentality",
          isUnlocked = false,
          iconType = "ACADEMIC"
        ),
        MilestoneEntity(
          petId = defaultPetId,
          title = "Safe Den Haven",
          description = "Relaxed calmly in crate with frozen enrichment for 10 minutes",
          category = "Confidence",
          isUnlocked = false,
          iconType = "HEART"
        )
      )
      milestoneDao.insertMilestones(initialMilestones)

      // Seed initial sample training logs
      trainingDao.insertLog(
        TrainingLogEntity(
          petId = defaultPetId,
          guideId = "guide_impulse_control_zen",
          guideTitle = "Impulse Control: The 'Leave It' Zen Game",
          durationSeconds = 240,
          repetitions = 12,
          successCount = 10,
          difficultyRating = 4,
          notes = "Milo picked up the open palm game super fast! High food drive helped.",
          timestamp = System.currentTimeMillis() - 86400000L
        )
      )

      trainingDao.insertLog(
        TrainingLogEntity(
          petId = defaultPetId,
          guideId = "guide_loose_leash",
          guideTitle = "Loose Leash Walking: The Silky Leash Protocol",
          durationSeconds = 300,
          repetitions = 15,
          successCount = 11,
          difficultyRating = 3,
          notes = "Practiced indoor check-ins. Good focus near hip line.",
          timestamp = System.currentTimeMillis() - 86400000L * 2
        )
      )

      // Pre-seed initial skill progress
      trainingDao.upsertSkillProgress(
        SkillProgressEntity(
          petId = defaultPetId,
          guideId = "guide_impulse_control_zen",
          completedStepsCount = 2,
          totalStepsCount = 3,
          isMastered = false,
          lastPracticedTimestamp = System.currentTimeMillis() - 86400000L
        )
      )

      trainingDao.upsertSkillProgress(
        SkillProgressEntity(
          petId = defaultPetId,
          guideId = "guide_loose_leash",
          completedStepsCount = 1,
          totalStepsCount = 4,
          isMastered = false,
          lastPracticedTimestamp = System.currentTimeMillis() - 86400000L * 2
        )
      )
    }
  }

  suspend fun insertPet(pet: PetEntity): Long {
    val newPetId = petDao.insertPet(pet)
    val defaultMilestones = listOf(
      MilestoneEntity(
        petId = newPetId,
        title = "Marker Clicker Master",
        description = "Charged the clicker and completed first 10 reward associations",
        category = "Training",
        isUnlocked = false,
        iconType = "STAR"
      ),
      MilestoneEntity(
        petId = newPetId,
        title = "First Step Taken",
        description = "Completed your very first interactive training session",
        category = "Training",
        isUnlocked = false,
        iconType = "SHIELD"
      ),
      MilestoneEntity(
        petId = newPetId,
        title = "Behavior Scholar",
        description = "Read 3 expert articles and completed quizzes",
        category = "Mentality",
        isUnlocked = false,
        iconType = "ACADEMIC"
      ),
      MilestoneEntity(
        petId = newPetId,
        title = "Bond of Trust",
        description = "Consistent practice for 5 consecutive sessions",
        category = "Bonding",
        isUnlocked = false,
        iconType = "HEART"
      )
    )
    milestoneDao.insertMilestones(defaultMilestones)
    return newPetId
  }

  suspend fun updatePet(pet: PetEntity) = petDao.updatePet(pet)
  suspend fun deletePet(pet: PetEntity) = petDao.deletePet(pet)

  suspend fun logTrainingSession(
    petId: Long,
    guideId: String,
    guideTitle: String,
    durationSeconds: Int,
    repetitions: Int,
    successCount: Int,
    difficultyRating: Int,
    notes: String,
    currentStepCompleted: Int,
    totalSteps: Int
  ) {
    trainingDao.insertLog(
      TrainingLogEntity(
        petId = petId,
        guideId = guideId,
        guideTitle = guideTitle,
        durationSeconds = durationSeconds,
        repetitions = repetitions,
        successCount = successCount,
        difficultyRating = difficultyRating,
        notes = notes,
        timestamp = System.currentTimeMillis()
      )
    )

    val existingProgress = trainingDao.getSkillProgress(petId, guideId)
    val updatedCompleted = maxOf(existingProgress?.completedStepsCount ?: 0, currentStepCompleted)
    val isMastered = updatedCompleted >= totalSteps

    trainingDao.upsertSkillProgress(
      SkillProgressEntity(
        id = existingProgress?.id ?: 0,
        petId = petId,
        guideId = guideId,
        completedStepsCount = updatedCompleted,
        totalStepsCount = totalSteps,
        isMastered = isMastered,
        lastPracticedTimestamp = System.currentTimeMillis()
      )
    )
  }

  suspend fun toggleBookmark(articleId: String) {
    val existing = articleDao.getProgressForArticle(articleId)
    if (existing == null) {
      articleDao.upsertArticleProgress(
        ArticleProgressEntity(
          articleId = articleId,
          isBookmarked = true,
          isRead = false
        )
      )
    } else {
      articleDao.upsertArticleProgress(
        existing.copy(isBookmarked = !existing.isBookmarked)
      )
    }
  }

  suspend fun markArticleRead(articleId: String, quizScore: Int? = null) {
    val existing = articleDao.getProgressForArticle(articleId)
    if (existing == null) {
      articleDao.upsertArticleProgress(
        ArticleProgressEntity(
          articleId = articleId,
          isBookmarked = false,
          isRead = true,
          quizScore = quizScore ?: -1,
          lastReadTimestamp = System.currentTimeMillis()
        )
      )
    } else {
      articleDao.upsertArticleProgress(
        existing.copy(
          isRead = true,
          quizScore = quizScore ?: existing.quizScore,
          lastReadTimestamp = System.currentTimeMillis()
        )
      )
    }
  }

  suspend fun unlockMilestone(milestone: MilestoneEntity) {
    milestoneDao.updateMilestone(
      milestone.copy(
        isUnlocked = true,
        unlockedDate = System.currentTimeMillis()
      )
    )
  }
}
