package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MilestoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MilestoneDao {
  @Query("SELECT * FROM milestones WHERE petId = :petId ORDER BY id ASC")
  fun getMilestonesForPet(petId: Long): Flow<List<MilestoneEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMilestones(milestones: List<MilestoneEntity>)

  @Update
  suspend fun updateMilestone(milestone: MilestoneEntity)

  @Query("SELECT COUNT(*) FROM milestones WHERE petId = :petId AND isUnlocked = 1")
  fun getUnlockedMilestonesCount(petId: Long): Flow<Int>
}
