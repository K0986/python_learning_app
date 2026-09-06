package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedSnippetDao {
  @Query("SELECT * FROM saved_snippets ORDER BY createdAt DESC")
  fun getAllSnippets(): Flow<List<SavedSnippetEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(snippet: SavedSnippetEntity): Long

  @Query("DELETE FROM saved_snippets WHERE id = :id")
  suspend fun deleteById(id: Int)
}
