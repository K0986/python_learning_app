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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Challenge
import com.example.data.model.ChallengesData
import com.example.data.model.Difficulty
import com.example.ui.components.CodeEditorView
import com.example.ui.components.CodeSnippetCard
import com.example.ui.components.QuickSymbolBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.PyLearnViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengesScreen(
  viewModel: PyLearnViewModel,
  modifier: Modifier = Modifier
) {
  val userProgress by viewModel.userProgress.collectAsState()
  val selectedChallenge by viewModel.selectedChallenge.collectAsState()
  val difficultyFilter by viewModel.challengeDifficultyFilter.collectAsState()

  val completedChallengeIds = userProgress.getCompletedChallenges()
  val allChallenges = ChallengesData.challenges

  val filteredChallenges = remember(difficultyFilter) {
    if (difficultyFilter == null) allChallenges else allChallenges.filter { it.difficulty == difficultyFilter }
  }

  if (selectedChallenge != null) {
    ChallengeSolverView(
      challenge = selectedChallenge!!,
      isSolved = completedChallengeIds.contains(selectedChallenge!!.id),
      onBack = { viewModel.selectChallenge(null) },
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
      // Header Info Card
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("challenges_header_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SurfaceCardBorder))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Python Code Challenges",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryDark
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Solve real programming challenges with automated unit test verification.",
                fontSize = 13.sp,
                color = TextSecondaryDark
              )
            }

            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(PythonYellow.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = null,
                tint = PythonYellow,
                modifier = Modifier.size(24.dp)
              )
            }
          }
        }
      }

      // Difficulty Filter Bar
      item {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          item {
            FilterChip(
              selected = difficultyFilter == null,
              onClick = { viewModel.setChallengeDifficultyFilter(null) },
              label = { Text("All (${allChallenges.size})") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = PythonBlue,
                selectedLabelColor = BackgroundDark
              ),
              modifier = Modifier.testTag("filter_all_challenges")
            )
          }

          items(Difficulty.values()) { diff ->
            val count = allChallenges.count { it.difficulty == diff }
            val color = when (diff) {
              Difficulty.EASY -> PythonGreen
              Difficulty.MEDIUM -> PythonYellow
              Difficulty.HARD -> StatusError
            }
            FilterChip(
              selected = difficultyFilter == diff,
              onClick = { viewModel.setChallengeDifficultyFilter(diff) },
              label = { Text("${diff.label} ($count)") },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = color,
                selectedLabelColor = BackgroundDark
              ),
              modifier = Modifier.testTag("filter_diff_${diff.name}")
            )
          }
        }
      }

      // Challenges list
      items(filteredChallenges) { challenge ->
        val isSolved = completedChallengeIds.contains(challenge.id)
        ChallengeItemCard(
          challenge = challenge,
          isSolved = isSolved,
          onClick = { viewModel.selectChallenge(challenge) }
        )
      }
    }
  }
}

@Composable
fun ChallengeItemCard(
  challenge: Challenge,
  isSolved: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val diffColor = when (challenge.difficulty) {
    Difficulty.EASY -> PythonGreen
    Difficulty.MEDIUM -> PythonYellow
    Difficulty.HARD -> StatusError
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("challenge_card_${challenge.id}"),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = SurfaceDark),
    border = CardDefaults.outlinedCardBorder().copy(
      brush = SolidColor(if (isSolved) StatusSuccess.copy(alpha = 0.6f) else SurfaceCardBorder)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(CircleShape)
          .background(if (isSolved) StatusSuccess.copy(alpha = 0.15f) else SurfaceCardDark),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isSolved) Icons.Default.CheckCircle else Icons.Default.SportsScore,
          contentDescription = null,
          tint = if (isSolved) StatusSuccess else PythonBlue,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = challenge.title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = challenge.description,
          fontSize = 12.sp,
          color = TextSecondaryDark,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(diffColor.copy(alpha = 0.15f))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = challenge.difficulty.label,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = diffColor
            )
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(4.dp))
              .background(SurfaceCardDark)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = challenge.category,
              fontSize = 10.sp,
              color = TextSecondaryDark
            )
          }

          Text(
            text = "+${challenge.xpReward} XP",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = PythonYellow
          )

          if (isSolved) {
            Text(
              text = "• Solved",
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              color = StatusSuccess
            )
          }
        }
      }

      Icon(
        imageVector = Icons.Default.ChevronRight,
        contentDescription = null,
        tint = TextMutedDark,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeSolverView(
  challenge: Challenge,
  isSolved: Boolean,
  onBack: () -> Unit,
  viewModel: PyLearnViewModel
) {
  val code by viewModel.challengeCode.collectAsState()
  val isRunning by viewModel.isRunningTests.collectAsState()
  val testResults by viewModel.testResults.collectAsState()
  val allPassed by viewModel.allTestsPassed.collectAsState()
  val showHint by viewModel.showChallengeHint.collectAsState()
  val showSolution by viewModel.showChallengeSolution.collectAsState()

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
          text = challenge.title,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = TextPrimaryDark,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      },
      navigationIcon = {
        IconButton(onClick = onBack, modifier = Modifier.testTag("btn_back_to_challenges")) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = TextPrimaryDark
          )
        }
      },
      actions = {
        val diffColor = when (challenge.difficulty) {
          Difficulty.EASY -> PythonGreen
          Difficulty.MEDIUM -> PythonYellow
          Difficulty.HARD -> StatusError
        }
        Box(
          modifier = Modifier
            .padding(end = 12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(diffColor.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
          Text(
            text = challenge.difficulty.label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = diffColor
          )
        }
      },
      colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
    )

    Column(
      modifier = Modifier
        .weight(1f)
        .verticalScroll(scrollState)
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // Problem Description Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SurfaceCardBorder))
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Problem Statement",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = PythonBlue
          )

          Text(
            text = challenge.description,
            fontSize = 13.sp,
            color = TextPrimaryDark,
            lineHeight = 20.sp
          )

          if (challenge.examples.isNotEmpty()) {
            Text(
              text = "Examples:",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = PythonYellow
            )
            challenge.examples.forEach { (input, output) ->
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(6.dp))
                  .background(CodeEditorBg)
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = "Input: $input  ➜  Output: $output",
                  fontSize = 12.sp,
                  fontFamily = FontFamily.Monospace,
                  color = TextSecondaryDark
                )
              }
            }
          }
        }
      }

      // Code Editor
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Your Solution (Python 3):",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )

          Text(
            text = "Reward: +${challenge.xpReward} XP",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = PythonYellow
          )
        }

        CodeEditorView(
          code = code,
          onCodeChange = { viewModel.updateChallengeCode(it) },
          minHeight = 180.dp
        )

        QuickSymbolBar(
          onInsert = { sym -> viewModel.updateChallengeCode(code + sym) },
          onIndent = { viewModel.updateChallengeCode(code + "    ") },
          onUnindent = {
            if (code.endsWith("    ")) {
              viewModel.updateChallengeCode(code.dropLast(4))
            } else if (code.isNotEmpty()) {
              viewModel.updateChallengeCode(code.dropLast(1))
            }
          }
        )
      }

      // Run Tests Action Button
      Button(
        onClick = { viewModel.runChallengeTests() },
        enabled = !isRunning,
        colors = ButtonDefaults.buttonColors(containerColor = PythonBlue),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("run_tests_button")
      ) {
        if (isRunning) {
          CircularProgressIndicator(
            modifier = Modifier.size(20.dp),
            color = BackgroundDark,
            strokeWidth = 2.dp
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text("Testing Solution...", color = BackgroundDark, fontWeight = FontWeight.Bold)
        } else {
          Icon(
            imageVector = Icons.Default.PlayArrow,
            contentDescription = null,
            tint = BackgroundDark
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Run Test Cases ▶", color = BackgroundDark, fontWeight = FontWeight.Bold)
        }
      }

      // Success Banner if All Tests Passed
      if (allPassed) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("challenge_success_banner"),
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = StatusSuccess.copy(alpha = 0.15f)),
          border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(StatusSuccess))
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess)
            Column {
              Text(
                text = "All Tests Passed! Outstanding!",
                fontWeight = FontWeight.Bold,
                color = StatusSuccess,
                fontSize = 14.sp
              )
              Text(
                text = "You earned +${challenge.xpReward} XP. Challenge marked as solved.",
                color = TextSecondaryDark,
                fontSize = 12.sp
              )
            }
          }
        }
      }

      // Test Cases Results Breakdown
      if (testResults.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Test Results (${testResults.count { it.passed }}/${testResults.size} Passed):",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimaryDark
          )

          testResults.forEachIndexed { idx, result ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(8.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (result.passed) SurfaceDark else StatusError.copy(alpha = 0.1f)
              ),
              border = CardDefaults.outlinedCardBorder().copy(
                brush = SolidColor(if (result.passed) StatusSuccess.copy(alpha = 0.4f) else StatusError.copy(alpha = 0.5f))
              )
            ) {
              Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Test Case ${idx + 1}: ${result.testCase.description}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimaryDark
                  )
                  Text(
                    text = if (result.passed) "PASSED ✓" else "FAILED ✗",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (result.passed) StatusSuccess else StatusError
                  )
                }

                Text(
                  text = "Input: ${result.testCase.input}",
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  color = TextSecondaryDark
                )
                Text(
                  text = "Expected: ${result.testCase.expectedOutput}",
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  color = PythonGreen
                )
                Text(
                  text = "Output:   ${result.actualOutput}",
                  fontSize = 11.sp,
                  fontFamily = FontFamily.Monospace,
                  color = if (result.passed) PythonGreen else StatusError
                )
                if (result.errorMessage != null) {
                  Text(
                    text = "Error: ${result.errorMessage}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = StatusError
                  )
                }
              }
            }
          }
        }
      }

      // Hints Accordion
      OutlinedButton(
        onClick = { viewModel.toggleChallengeHint() },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().testTag("toggle_hints_btn")
      ) {
        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = PythonYellow)
        Spacer(modifier = Modifier.width(6.dp))
        Text(if (showHint) "Hide Hints" else "Need a Hint?", color = TextPrimaryDark)
      }

      AnimatedVisibility(visible = showHint) {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(SurfaceCardBorder))
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            challenge.hints.forEachIndexed { i, hint ->
              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("${i + 1}.", color = PythonYellow, fontWeight = FontWeight.Bold)
                Text(text = hint, fontSize = 12.sp, color = TextSecondaryDark, lineHeight = 18.sp)
              }
            }
          }
        }
      }

      // Solution Accordion
      OutlinedButton(
        onClick = { viewModel.toggleChallengeSolution() },
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth().testTag("toggle_solution_btn")
      ) {
        Icon(imageVector = Icons.Default.Visibility, contentDescription = null, tint = PythonBlue)
        Spacer(modifier = Modifier.width(6.dp))
        Text(if (showSolution) "Hide Official Solution" else "View Official Solution", color = TextPrimaryDark)
      }

      AnimatedVisibility(visible = showSolution) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          CodeSnippetCard(
            code = challenge.solutionCode,
            title = "Reference Python Solution"
          )
          Text(
            text = challenge.solutionExplanation,
            fontSize = 12.sp,
            color = TextSecondaryDark,
            lineHeight = 18.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(36.dp))
    }
  }
}
