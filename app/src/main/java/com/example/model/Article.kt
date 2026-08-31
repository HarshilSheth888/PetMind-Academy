package com.example.model

data class QuizQuestion(
  val question: String,
  val options: List<String>,
  val correctOptionIndex: Int,
  val explanation: String
)

data class ArticleSection(
  val heading: String,
  val body: String,
  val takeaway: String? = null
)

data class Article(
  val id: String,
  val title: String,
  val subtitle: String,
  val category: ArticleCategory,
  val speciesTarget: String, // "Dog", "Cat", "All Pets"
  val readTimeMinutes: Int,
  val authorName: String,
  val authorCredentials: String,
  val heroImageResName: String,
  val summary: String,
  val corePsychologyInsight: String,
  val sections: List<ArticleSection>,
  val quiz: List<QuizQuestion>
)

enum class ArticleCategory(val title: String, val tagColor: String) {
  PSYCHOLOGY("Pet Psychology", "Blue"),
  COGNITION("Cognitive Development", "Purple"),
  BEHAVIOR_SOLUTIONS("Behavior & Fixes", "Rose"),
  ENRICHMENT("Mental Enrichment", "Amber"),
  BONDING("Bonding & Trust", "Green")
}
