package com.example.data.model

enum class Difficulty(val label: String, val xp: Int) {
  EASY("Easy", 100),
  MEDIUM("Medium", 200),
  HARD("Hard", 350)
}

data class TestCase(
  val input: String,
  val expectedOutput: String,
  val description: String = ""
)

data class TestResult(
  val testCase: TestCase,
  val actualOutput: String,
  val passed: Boolean,
  val errorMessage: String? = null
)

data class Challenge(
  val id: String,
  val title: String,
  val difficulty: Difficulty,
  val category: String,
  val description: String,
  val examples: List<Pair<String, String>>, // Input, Output
  val starterCode: String,
  val functionName: String,
  val testCases: List<TestCase>,
  val hints: List<String>,
  val solutionCode: String,
  val solutionExplanation: String,
  val xpReward: Int = difficulty.xp
)
