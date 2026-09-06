package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.SavedSnippetEntity
import com.example.data.model.CurriculumData
import com.example.data.model.TemplatesAndReferenceData
import com.example.ui.theme.*
import com.example.ui.viewmodel.PyLearnViewModel

@Composable
fun ProfileScreen(
  viewModel: PyLearnViewModel,
  modifier: Modifier = Modifier
) {
  val userProgress by viewModel.userProgress.collectAsState()
  val savedSnippets by viewModel.savedSnippets.collectAsState()

  val totalXp = userProgress.totalXp
  val level = (totalXp / 250) + 1
  val currentLevelXp = totalXp % 250
  val levelProgress = currentLevelXp / 250f

  val rankTitle = when {
    level >= 10 -> "Python Grandmaster"
    level >= 7 -> "Senior Pythonista"
    level >= 5 -> "Algorithm Ace"
    level >= 3 -> "Python Craftsman"
    level >= 2 -> "Code Apprentice"
    else -> "Python Novice"
  }

  val completedLessons = userProgress.getCompletedLessons()
  val completedChallenges = userProgress.getCompletedChallenges()
  val bookmarkedLessons = userProgress.getBookmarkedLessons()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
  ) {
    // User Profile Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("profile_user_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SurfaceCardBorder))
      ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Box(
              modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(PythonBlue.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Code,
                contentDescription = null,
                tint = PythonBlue,
                modifier = Modifier.size(32.dp)
              )
            }

            Column {
              Text(
                text = "Python Developer",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Text(
                text = rankTitle,
                fontSize = 13.sp,
                color = PythonYellow,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "Level $level • $totalXp Total XP",
                fontSize = 12.sp,
                color = TextSecondaryDark
              )
            }
          }

          // Level progress
          Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Level $level Progress",
                fontSize = 11.sp,
                color = TextSecondaryDark
              )
              Text(
                text = "$currentLevelXp / 250 XP to Level ${level + 1}",
                fontSize = 11.sp,
                color = PythonBlue,
                fontWeight = FontWeight.SemiBold
              )
            }
            LinearProgressIndicator(
              progress = { levelProgress },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = PythonBlue,
              trackColor = SurfaceCardDark
            )
          }
        }
      }
    }

    // Stats Grid
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        StatBox(
          title = "Streak",
          value = "${userProgress.currentStreak} Days",
          icon = Icons.Default.LocalFireDepartment,
          tint = PythonYellow,
          modifier = Modifier.weight(1f)
        )
        StatBox(
          title = "Lessons",
          value = "${completedLessons.size}",
          icon = Icons.Default.CheckCircle,
          tint = PythonGreen,
          modifier = Modifier.weight(1f)
        )
        StatBox(
          title = "Solved",
          value = "${completedChallenges.size}",
          icon = Icons.Default.SportsScore,
          tint = PythonBlue,
          modifier = Modifier.weight(1f)
        )
        StatBox(
          title = "Runs",
          value = "${userProgress.runsCount}",
          icon = Icons.Default.Terminal,
          tint = SyntaxKeyword,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Achievements Gallery
    item {
      Text(
        text = "Badges & Achievements",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimaryDark
      )
    }

    items(TemplatesAndReferenceData.achievements) { ach ->
      val isUnlocked = when (ach.id) {
        "ach_first_run" -> userProgress.runsCount > 0
        "ach_first_lesson" -> completedLessons.isNotEmpty()
        "ach_streak_3" -> userProgress.currentStreak >= 3
        "ach_beginner_complete" -> completedLessons.size >= 5
        "ach_first_challenge" -> completedChallenges.isNotEmpty()
        "ach_challenge_master" -> completedChallenges.size >= 3
        "ach_advanced_graduate" -> completedLessons.size >= 12
        else -> false
      }

      Card(
        modifier = Modifier.fillMaxWidth().testTag("ach_${ach.id}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isUnlocked) SurfaceDark else SurfaceDark.copy(alpha = 0.5f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = SolidColor(if (isUnlocked) PythonYellow.copy(alpha = 0.5f) else SurfaceCardBorder)
        )
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(38.dp)
              .clip(CircleShape)
              .background(if (isUnlocked) PythonYellow.copy(alpha = 0.2f) else SurfaceCardDark),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
              contentDescription = null,
              tint = if (isUnlocked) PythonYellow else TextMutedDark,
              modifier = Modifier.size(20.dp)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = ach.title,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              color = if (isUnlocked) TextPrimaryDark else TextSecondaryDark
            )
            Text(
              text = ach.description,
              fontSize = 11.sp,
              color = TextMutedDark
            )
          }

          Text(
            text = "+${ach.xpReward} XP",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (isUnlocked) PythonYellow else TextMutedDark
          )
        }
      }
    }

    // Saved Snippets
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Saved Python Snippets (${savedSnippets.size})",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
      }
    }

    if (savedSnippets.isEmpty()) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark)
        ) {
          Text(
            text = "No saved snippets yet. Use the Save button in the Compiler to store your code!",
            fontSize = 12.sp,
            color = TextSecondaryDark,
            modifier = Modifier.padding(16.dp)
          )
        }
      }
    } else {
      items(savedSnippets) { snippet ->
        SavedSnippetRow(
          snippet = snippet,
          onLoad = { viewModel.loadSnippetIntoCompiler(snippet.code) },
          onDelete = { viewModel.deleteSavedSnippet(snippet.id) }
        )
      }
    }

    // Bookmarked Lessons Quick Access
    if (bookmarkedLessons.isNotEmpty()) {
      item {
        Text(
          text = "Bookmarked Lessons (${bookmarkedLessons.size})",
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark
        )
      }

      val bookmarkedList = CurriculumData.lessons.filter { bookmarkedLessons.contains(it.id) }
      items(bookmarkedList) { lesson ->
        Card(
          onClick = {
            viewModel.selectLesson(lesson)
            viewModel.setTab(com.example.ui.viewmodel.AppTab.LEARN)
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SurfaceCardBorder))
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(lesson.title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimaryDark)
              Text(lesson.summary, fontSize = 11.sp, color = TextSecondaryDark, maxLines = 1)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondaryDark)
          }
        }
      }
    }
  }
}

@Composable
fun StatBox(
  title: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  tint: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SurfaceCardBorder))
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
      Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimaryDark)
      Text(text = title, fontSize = 10.sp, color = TextSecondaryDark)
    }
  }
}

@Composable
fun SavedSnippetRow(
  snippet: SavedSnippetEntity,
  onLoad: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.fillMaxWidth().testTag("snippet_${snippet.id}"),
    shape = RoundedCornerShape(10.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SurfaceCardBorder))
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = snippet.title,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = TextPrimaryDark
        )
        Text(
          text = "${snippet.code.lines().size} lines",
          fontSize = 11.sp,
          color = TextSecondaryDark,
          fontFamily = FontFamily.Monospace
        )
      }

      Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        IconButton(onClick = onLoad, modifier = Modifier.size(32.dp).testTag("load_snippet_${snippet.id}")) {
          Icon(Icons.Default.PlayArrow, contentDescription = "Load", tint = PythonBlue, modifier = Modifier.size(18.dp))
        }
        IconButton(onClick = onDelete, modifier = Modifier.size(32.dp).testTag("delete_snippet_${snippet.id}")) {
          Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = StatusError, modifier = Modifier.size(18.dp))
        }
      }
    }
  }
}
