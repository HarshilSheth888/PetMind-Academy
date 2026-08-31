package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.SkillProgressEntity
import com.example.data.model.TrainingLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrainingDao {
  @Query("SELECT * FROM training_logs ORDER BY timestamp DESC")
  fun getAllLogs(): Flow<List<TrainingLogEntity>>

  @Query("SELECT * FROM training_logs WHERE petId = :petId ORDER BY timestamp DESC")
  fun getLogsForPet(petId: Long): Flow<List<TrainingLogEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertLog(log: TrainingLogEntity): Long

  @Query("SELECT * FROM skill_progress WHERE petId = :petId")
  fun getSkillProgressForPet(petId: Long): Flow<List<SkillProgressEntity>>

  @Query("SELECT * FROM skill_progress WHERE petId = :petId AND guideId = :guideId LIMIT 1")
  suspend fun getSkillProgress(petId: Long, guideId: String): SkillProgressEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsertSkillProgress(progress: SkillProgressEntity)

  @Query("SELECT SUM(durationSeconds) FROM training_logs WHERE petId = :petId")
  fun getTotalTrainingTimeSeconds(petId: Long): Flow<Int?>

  @Query("SELECT COUNT(*) FROM training_logs WHERE petId = :petId")
  fun getTotalSessionCount(petId: Long): Flow<Int>
}
