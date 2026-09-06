package com.example.data.model

enum class CourseLevel(val title: String, val description: String) {
  BEGINNER("Beginner", "Foundations of Python syntax, data types, and control flow"),
  INTERMEDIATE("Intermediate", "Functions, data structures, comprehensions, and errors"),
  ADVANCED("Advanced", "OOP, decorators, generators, dunder methods, and best practices")
}

data class QuizQuestion(
  val question: String,
  val options: List<String>,
  val correctIndex: Int,
  val explanation: String
)

data class Lesson(
  val id: String,
  val title: String,
  val level: CourseLevel,
  val estimatedMinutes: Int,
  val summary: String,
  val contentMarkdown: String,
  val codeExample: String,
  val quiz: QuizQuestion,
  val keyTakeaways: List<String>
)
