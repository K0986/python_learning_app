package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
  @PrimaryKey val id: Int = 1,
  val completedLessonIds: String = "", // Comma-separated IDs
  val completedChallengeIds: String = "", // Comma-separated IDs
  val bookmarkedLessonIds: String = "",
  val totalXp: Int = 0,
  val currentStreak: Int = 1,
  val lastActiveDateEpochDay: Long = 0L,
  val runsCount: Int = 0
) {
  fun getCompletedLessons(): Set<String> {
    return if (completedLessonIds.isBlank()) emptySet() else completedLessonIds.split(",").filter { it.isNotBlank() }.toSet()
  }

  fun getCompletedChallenges(): Set<String> {
    return if (completedChallengeIds.isBlank()) emptySet() else completedChallengeIds.split(",").filter { it.isNotBlank() }.toSet()
  }

  fun getBookmarkedLessons(): Set<String> {
    return if (bookmarkedLessonIds.isBlank()) emptySet() else bookmarkedLessonIds.split(",").filter { it.isNotBlank() }.toSet()
  }
}
