package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [UserProgressEntity::class, SavedSnippetEntity::class],
  version = 1,
  exportSchema = false
)
abstract class PyLearnDatabase : RoomDatabase() {
  abstract fun userProgressDao(): UserProgressDao
  abstract fun savedSnippetDao(): SavedSnippetDao

  companion object {
    @Volatile
    private var INSTANCE: PyLearnDatabase? = null

    fun getDatabase(context: Context): PyLearnDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          PyLearnDatabase::class.java,
          "pylearn_database"
        ).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
