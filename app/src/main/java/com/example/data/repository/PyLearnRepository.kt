package com.example.data.repository

import com.example.data.db.SavedSnippetDao
import com.example.data.db.SavedSnippetEntity
import com.example.data.db.UserProgressDao
import com.example.data.db.UserProgressEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

class PyLearnRepository(
  private val userProgressDao: UserProgressDao,
  private val savedSnippetDao: SavedSnippetDao
) {

  val userProgress: Flow<UserProgressEntity> = userProgressDao.getUserProgress().map { entity ->
    entity ?: UserProgressEntity(
      id = 1,
      completedLessonIds = "",
      completedChallengeIds = "",
      bookmarkedLessonIds = "",
      totalXp = 0,
      currentStreak = 1,
      lastActiveDateEpochDay = getCurrentEpochDay(),
      runsCount = 0
    )
  }

  val savedSnippets: Flow<List<SavedSnippetEntity>> = savedSnippetDao.getAllSnippets()

  private fun getCurrentEpochDay(): Long {
    val cal = Calendar.getInstance()
    return (cal.timeInMillis / (1000 * 60 * 60 * 24))
  }

  private suspend fun getOrCreateProgress(): UserProgressEntity {
    val existing = userProgressDao.getUserProgressOnce()
    val today = getCurrentEpochDay()
    if (existing == null) {
      val initial = UserProgressEntity(
        id = 1,
        completedLessonIds = "",
        completedChallengeIds = "",
        bookmarkedLessonIds = "",
        totalXp = 0,
        currentStreak = 1,
        lastActiveDateEpochDay = today,
        runsCount = 0
      )
      userProgressDao.insertOrUpdate(initial)
      return initial
    }

    // Check streak
    val lastDay = existing.lastActiveDateEpochDay
    if (lastDay != today) {
      val diff = today - lastDay
      val newStreak = if (diff == 1L) existing.currentStreak + 1 else if (diff > 1L) 1 else existing.currentStreak
      val updated = existing.copy(
        currentStreak = newStreak,
        lastActiveDateEpochDay = today
      )
      userProgressDao.insertOrUpdate(updated)
      return updated
    }
    return existing
  }

  suspend fun completeLesson(lessonId: String, xp: Int = 50) {
    val current = getOrCreateProgress()
    val completed = current.getCompletedLessons().toMutableSet()
    val alreadyCompleted = completed.contains(lessonId)
    completed.add(lessonId)

    val updatedXp = if (!alreadyCompleted) current.totalXp + xp else current.totalXp
    userProgressDao.insertOrUpdate(
      current.copy(
        completedLessonIds = completed.joinToString(","),
        totalXp = updatedXp
      )
    )
  }

  suspend fun completeChallenge(challengeId: String, xp: Int) {
    val current = getOrCreateProgress()
    val completed = current.getCompletedChallenges().toMutableSet()
    val alreadyCompleted = completed.contains(challengeId)
    completed.add(challengeId)

    val updatedXp = if (!alreadyCompleted) current.totalXp + xp else current.totalXp
    userProgressDao.insertOrUpdate(
      current.copy(
        completedChallengeIds = completed.joinToString(","),
        totalXp = updatedXp
      )
    )
  }

  suspend fun toggleBookmark(lessonId: String) {
    val current = getOrCreateProgress()
    val bookmarks = current.getBookmarkedLessons().toMutableSet()
    if (bookmarks.contains(lessonId)) {
      bookmarks.remove(lessonId)
    } else {
      bookmarks.add(lessonId)
    }
    userProgressDao.insertOrUpdate(
      current.copy(
        bookmarkedLessonIds = bookmarks.joinToString(",")
      )
    )
  }

  suspend fun recordCodeExecution() {
    val current = getOrCreateProgress()
    userProgressDao.insertOrUpdate(
      current.copy(
        runsCount = current.runsCount + 1,
        totalXp = current.totalXp + 5
      )
    )
  }

  suspend fun saveSnippet(title: String, code: String): Long {
    return savedSnippetDao.insert(
      SavedSnippetEntity(
        title = title,
        code = code
      )
    )
  }

  suspend fun deleteSnippet(id: Int) {
    savedSnippetDao.deleteById(id)
  }
}
