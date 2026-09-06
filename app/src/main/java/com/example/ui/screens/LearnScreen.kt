package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CourseLevel
import com.example.data.model.CurriculumData
import com.example.data.model.Lesson
import com.example.ui.components.CodeSnippetCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.PyLearnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearnScreen(
  viewModel: PyLearnViewModel,
  modifier: Modifier = Modifier
) {
  val userProgress by viewModel.userProgress.collectAsState()
  val selectedLesson by viewModel.selectedLesson.collectAsState()
  val levelFilter by viewModel.selectedLessonLevel.collectAsState()

  val completedIds = userProgress.getCompletedLessons()
  val bookmarkedIds = userProgress.getBookmarkedLessons()

  val allLessons = CurriculumData.lessons
  val filteredLessons = remember(levelFilter) {
    if (levelFilter == null) allLessons else allLessons.filter { it.level == levelFilter }
  }

  val totalLessons = allLessons.size
  val completedCount = completedIds.intersect(allLessons.map { it.id }.toSet()).size
  val progressPercent = if (totalLessons > 0) (completedCount.toFloat() / totalLessons) else 0f

  if (selectedLesson != null) {
    LessonDetailView(
      lesson = selectedLesson!!,
      isCompleted = completedIds.contains(selectedLesson!!.id),
      isBookmarked = bookmarkedIds.contains(selectedLesson!!.id),
      onBack = { viewModel.selectLesson(null) },
      onRunInCompiler = { viewModel.loadLessonCodeIntoCompiler(selectedLesson!!.codeExample) },
      onToggleBookmark = { viewModel.toggleBookmark(selectedLesson!!.id) },
      onMarkComplete = { viewModel.markLessonCompleted(selectedLesson!!.id) },
      viewModel = viewModel
    )
  } else {
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
      // Hero Journey Progress Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("learn_hero_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.linearGradient(listOf(PythonBlue.copy(alpha = 0.5f), PythonYellow.copy(alpha = 0.5f)))
          )
        ) {
          Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Python Mastery Journey",
                  fontSize = 18.sp,
                  fontWeight = FontWeight.Bold,
                  color = TextPrimaryDark
                )
                Text(
                  text = "$completedCount of $totalLessons lessons mastered",
                  fontSize = 13.sp,
                  color = TextSecondaryDark
                )
              }

              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(PythonBlue.copy(alpha = 0.15f))
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "${(progressPercent * 100).toInt()}%",
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold,
                  color = PythonBlue
                )
              }
            }

            LinearProgressIndicator(
              progress = { progressPercent },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = PythonBlue,
              trackColor = SurfaceCardDark
            )

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.LocalFireDepartment,
                  contentDescription = "Streak",
                  tint = PythonYellow,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "${userProgress.currentStreak} Day Streak",
                  fontSize = 12.sp,
                  color = PythonYellow,
                  fontWeight = FontWeight.SemiBold
                )
              }

              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Bolt,
                  contentDescription = "XP",
                  tint = PythonGreen,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "${userProgress.totalXp} XP",
                  fontSize = 12.sp,
                  color = PythonGreen,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }

      // Level Category Filter Chips
      item {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          item {
            FilterChip(
              selected = levelFilter == null,
              onClick = { viewModel.setLessonLevelFilter(null) },
              label = { Text("All (${allLessons.size})") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PythonBlue,
                selectedLabelColor = BackgroundDark
              ),
              modifier = Modifier.testTag("filter_all_lessons")
            )
          }

          items(CourseLevel.values()) { level ->
            val count = allLessons.count { it.level == level }
            FilterChip(
              selected = levelFilter == level,
              onClick = { viewModel.setLessonLevelFilter(level) },
              label = { Text("${level.title} ($count)") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = when (level) {
                  CourseLevel.BEGINNER -> PythonGreen
                  CourseLevel.INTERMEDIATE -> PythonBlue
                  CourseLevel.ADVANCED -> PythonYellow
                },
                selectedLabelColor = BackgroundDark
              ),
              modifier = Modifier.testTag("filter_level_${level.name}")
            )
          }
        }
      }

      // Lessons List
      items(filteredLessons) { lesson ->
        val isCompleted = completedIds.contains(lesson.id)
        val isBookmarked = bookmarkedIds.contains(lesson.id)

        LessonItemCard(
          lesson = lesson,
          isCompleted = isCompleted,
          isBookmarked = isBookmarked,
          onClick = { viewModel.selectLesson(lesson) },
          onBookmarkToggle = { viewModel.toggleBookmark(lesson.id) }
        )
      }
    }
  }
}

@Composable
fun LessonItemCard(
  lesson: Lesson,
  isCompleted: Boolean,
  isBookmarked: Boolean,
  onClick: () -> Unit,
  onBookmarkToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("lesson_card_${lesson.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = SolidColor(if (isCompleted) StatusSuccess.copy(alpha = 0.6f) else SurfaceCardBorder)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Completion indicator icon
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(if (isCompleted) StatusSuccess.copy(alpha = 0.2f) else SurfaceCardDark),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Code,
          contentDescription = null,
          tint = if (isCompleted) StatusSuccess else PythonBlue,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = lesson.title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = lesson.summary,
          fontSize = 12.sp,
          color = TextSecondaryDark,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Level badge
          val levelColor = when (lesson.level) {
            CourseLevel.BEGINNER -> PythonGreen
            CourseLevel.INTERMEDIATE -> PythonBlue
            CourseLevel.ADVANCED -> PythonYellow
          }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(levelColor.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = lesson.level.title,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = levelColor
            )
          }

          Text(
            text = "• ${lesson.estimatedMinutes} min",
            fontSize = 11.sp,
            color = TextMutedDark
          )

          Text(
            text = "• +50 XP",
            fontSize = 11.sp,
            color = PythonYellow,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      // Bookmark action
      IconButton(
        onClick = onBookmarkToggle,
        modifier = Modifier.size(36.dp).testTag("btn_bookmark_${lesson.id}")
      ) {
        Icon(
          imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
          contentDescription = "Bookmark",
          tint = if (isBookmarked) PythonYellow else TextMutedDark,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailView(
  lesson: Lesson,
  isCompleted: Boolean,
  isBookmarked: Boolean,
  onBack: () -> Unit,
  onRunInCompiler: () -> Unit,
  onToggleBookmark: () -> Unit,
  onMarkComplete: () -> Unit,
  viewModel: PyLearnViewModel
) {
  val quizSelectedOption by viewModel.quizSelectedOption.collectAsState()
  val isQuizSubmitted by viewModel.isQuizSubmitted.collectAsState()
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(BackgroundDark)
  ) {
    // Top Bar
    TopAppBar(
      title = {
        Text(
          text = lesson.title,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      },
      navigationIcon = {
        IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_to_lessons")) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = TextPrimaryDark
          )
        }
      },
      actions = {
        IconButton(onClick = onToggleBookmark, modifier = Modifier.testTag("btn_detail_bookmark")) {
          Icon(
            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = "Bookmark",
            tint = if (isBookmarked) PythonYellow else TextSecondaryDark
          )
        }
      },
      colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
    )

    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(scrollState)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header Metadata
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        val levelColor = when (lesson.level) {
          CourseLevel.BEGINNER -> PythonGreen
          CourseLevel.INTERMEDIATE -> PythonBlue
          CourseLevel.ADVANCED -> PythonYellow
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(levelColor.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = "${lesson.level.title} Module",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = levelColor
          )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            tint = TextMutedDark,
            modifier = Modifier.size(14.dp)
          )
          Text(
            text = "${lesson.estimatedMinutes} mins read",
            fontSize = 12.sp,
            color = TextSecondaryDark
          )
        }
      }

      // Main Markdown Content Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SurfaceCardBorder))
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = lesson.contentMarkdown,
            fontSize = 14.sp,
            color = TextPrimaryDark,
            lineHeight = 22.sp
          )
        }
      }

      // Code Example Section
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Interactive Code Example",
          fontSize = 15.sp,
          fontWeight = FontWeight.Bold,
          color = PythonBlue
        )
        CodeSnippetCard(
          code = lesson.codeExample,
          title = "Example Script",
          onRunClick = onRunInCompiler
        )
      }

      // Key Takeaways
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SurfaceCardBorder))
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Key Takeaways",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = PythonYellow
          )
          lesson.keyTakeaways.forEach { takeaway ->
            Row(
              verticalAlignment = Alignment.Top,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text("✓", color = PythonGreen, fontWeight = FontWeight.Bold)
              Text(
                text = takeaway,
                fontSize = 13.sp,
                color = TextSecondaryDark,
                lineHeight = 18.sp
              )
            }
          }
        }
      }

      // Quick Knowledge Check (Quiz)
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("quiz_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(
          brush = SolidColor(if (isQuizSubmitted && quizSelectedOption == lesson.quiz.correctIndex) StatusSuccess else PythonBlue.copy(alpha = 0.5f))
        )
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Icon(imageVector = Icons.Default.Quiz, contentDescription = null, tint = PythonBlue)
            Text(
              text = "Quick Knowledge Check",
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              color = TextPrimaryDark
            )
          }

          Text(
            text = lesson.quiz.question,
            fontSize = 14.sp,
            color = TextPrimaryDark,
            lineHeight = 20.sp
          )

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            lesson.quiz.options.forEachIndexed { idx, option ->
              val isSelected = quizSelectedOption == idx
              val isCorrect = idx == lesson.quiz.correctIndex

              val optionColor = when {
                !isQuizSubmitted -> if (isSelected) PythonBlue else SurfaceCardDark
                isCorrect -> StatusSuccess
                isSelected && !isCorrect -> StatusError
                else -> SurfaceCardDark
              }

              Surface(
                onClick = { viewModel.selectQuizOption(idx) },
                shape = RoundedCornerShape(8.dp),
                color = optionColor.copy(alpha = if (isSelected || (isQuizSubmitted && isCorrect)) 0.2f else 0.6f),
                border = if (isSelected || (isQuizSubmitted && isCorrect)) {
                  ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(optionColor))
                } else null,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("quiz_option_$idx")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "${('A' + idx)}. ",
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) optionColor else TextSecondaryDark
                  )
                  Text(
                    text = option,
                    fontSize = 13.sp,
                    color = TextPrimaryDark,
                    modifier = Modifier.weight(1f)
                  )
                  if (isQuizSubmitted) {
                    if (isCorrect) {
                      Icon(Icons.Default.Check, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
                    } else if (isSelected) {
                      Icon(Icons.Default.Close, contentDescription = null, tint = StatusError, modifier = Modifier.size(16.dp))
                    }
                  }
                }
              }
            }
          }

          if (!isQuizSubmitted) {
            Button(
              onClick = { viewModel.submitQuiz(lesson) },
              enabled = quizSelectedOption != null,
              colors = ButtonDefaults.buttonColors(containerColor = PythonBlue),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth().testTag("submit_quiz_btn")
            ) {
              Text("Check Answer", color = BackgroundDark, fontWeight = FontWeight.Bold)
            }
          } else {
            AnimatedVisibility(visible = true) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(SurfaceCardDark)
                  .padding(12.dp)
              ) {
                Text(
                  text = if (quizSelectedOption == lesson.quiz.correctIndex) "Correct! +50 XP" else "Explanation",
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (quizSelectedOption == lesson.quiz.correctIndex) StatusSuccess else PythonYellow
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = lesson.quiz.explanation,
                  fontSize = 12.sp,
                  color = TextSecondaryDark,
                  lineHeight = 18.sp
                )
              }
            }
          }
        }
      }

      // Mark Complete Action
      Button(
        onClick = onMarkComplete,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isCompleted) StatusSuccess else PythonYellow
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("mark_lesson_complete_btn")
      ) {
        Icon(
          imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Star,
          contentDescription = null,
          tint = BackgroundDark,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isCompleted) "Lesson Completed (✓)" else "Mark as Completed (+50 XP)",
          color = BackgroundDark,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}
