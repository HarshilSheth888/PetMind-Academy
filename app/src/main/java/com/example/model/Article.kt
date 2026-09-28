package com.example.model

import com.example.data.model.PetEntity

data class QuizQuestion(
  val question: String,
  val options: List<String>,
  val correctOptionIndex: Int,
  val explanation: String,
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
) {
  fun matchesSpecies(selectedSpecies: Set<String>, activePet: PetEntity?): Boolean {
    if (selectedSpecies.isNotEmpty()) {
      return speciesTarget.equals("All Pets", ignoreCase = true) ||
        selectedSpecies.any { species ->
          speciesTarget.contains(species, ignoreCase = true)
        }
    }
    if (activePet != null) {
      val petSpecies = activePet.species
      if (petSpecies.equals("OTHER", ignoreCase = true)) {
        return speciesTarget.equals("All Pets", ignoreCase = true)
      }
      return speciesTarget.equals("All Pets", ignoreCase = true) ||
        speciesTarget.contains(petSpecies, ignoreCase = true) ||
        (petSpecies.equals("DOG", ignoreCase = true) && speciesTarget.contains("Dog", ignoreCase = true)) ||
        (petSpecies.equals("CAT", ignoreCase = true) && speciesTarget.contains("Cat", ignoreCase = true))
    }
    return true
  }
}

enum class ArticleCategory(val title: String) {
  PSYCHOLOGY("Pet Psychology"),
  COGNITION("Cognitive Development"),
  BEHAVIOR_SOLUTIONS("Behavior & Fixes"),
  ENRICHMENT("Mental Enrichment"),
  BONDING("Bonding & Trust")
}
