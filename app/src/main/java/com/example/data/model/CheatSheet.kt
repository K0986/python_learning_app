package com.example.data.model

data class CheatSheetItem(
  val id: String,
  val category: String,
  val title: String,
  val syntax: String,
  val example: String,
  val explanation: String
)

data class CodeTemplate(
  val id: String,
  val title: String,
  val category: String,
  val description: String,
  val code: String
)

data class Achievement(
  val id: String,
  val title: String,
  val description: String,
  val iconName: String,
  val xpReward: Int,
  val isUnlocked: Boolean = false
)
