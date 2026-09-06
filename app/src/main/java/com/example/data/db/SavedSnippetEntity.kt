package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_snippets")
data class SavedSnippetEntity(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  val title: String,
  val code: String,
  val createdAt: Long = System.currentTimeMillis()
)
