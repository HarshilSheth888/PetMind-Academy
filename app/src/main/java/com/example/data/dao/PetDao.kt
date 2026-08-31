package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PetDao {
  @Query("SELECT * FROM pets ORDER BY createdAt ASC")
  fun getAllPets(): Flow<List<PetEntity>>

  @Query("SELECT * FROM pets WHERE id = :petId LIMIT 1")
  suspend fun getPetById(petId: Long): PetEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPet(pet: PetEntity): Long

  @Update
  suspend fun updatePet(pet: PetEntity)

  @Delete
  suspend fun deletePet(pet: PetEntity)

  @Query("SELECT COUNT(*) FROM pets")
  suspend fun getPetCount(): Int
}
