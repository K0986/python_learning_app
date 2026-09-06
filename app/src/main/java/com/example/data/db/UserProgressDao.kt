package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {
  @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
  fun getUserProgress(): Flow<UserProgressEntity?>

  @Query("SELECT * FROM user_progress WHERE id = 1 LIMIT 1")
  suspend fun getUserProgressOnce(): UserProgressEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(progress: UserProgressEntity)
}
