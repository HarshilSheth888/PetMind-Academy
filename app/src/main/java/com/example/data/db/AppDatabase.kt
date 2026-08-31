package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ArticleDao
import com.example.data.dao.MilestoneDao
import com.example.data.dao.PetDao
import com.example.data.dao.TrainingDao
import com.example.data.model.ArticleProgressEntity
import com.example.data.model.MilestoneEntity
import com.example.data.model.PetEntity
import com.example.data.model.SkillProgressEntity
import com.example.data.model.TrainingLogEntity

@Database(
  entities = [
    PetEntity::class,
    TrainingLogEntity::class,
    SkillProgressEntity::class,
    ArticleProgressEntity::class,
    MilestoneEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun petDao(): PetDao
  abstract fun trainingDao(): TrainingDao
  abstract fun articleDao(): ArticleDao
  abstract fun milestoneDao(): MilestoneDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "petmind_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
