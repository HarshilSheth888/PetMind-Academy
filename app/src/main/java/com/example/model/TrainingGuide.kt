package com.example.model

enum class GuideDifficulty(val label: String, val levelNumber: Int) {
  FOUNDATION("Foundation", 1),
  INTERMEDIATE("Intermediate", 2),
  ADVANCED("Advanced", 3),
  BEHAVIOR_MOD("Behavioral Fix", 2)
}

data class TrainingStep(
  val stepNumber: Int,
  val title: String,
  val instruction: String,
  val cueWord: String?,
  val petMindset: String, // What the pet is experiencing/learning
  val successCriteria: String,
  val commonMistake: String
)

data class TrainingGuide(
  val id: String,
  val title: String,
  val shortDescription: String,
  val difficulty: GuideDifficulty,
  val speciesTarget: String, // "Dog", "Cat", "Universal"
  val category: String, // "Manners", "Focus", "Safety", "Confidence", "Tricks"
  val estimatedSessions: String,
  val gearNeeded: List<String>,
  val whyItWorksPsychologically: String,
  val steps: List<TrainingStep>,
  val troubleshootingTips: List<String>
)
