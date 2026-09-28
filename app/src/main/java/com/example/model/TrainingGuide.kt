package com.example.model

import com.example.data.model.PetEntity

enum class GuideDifficulty(val label: String) {
  FOUNDATION("Foundation"),
  INTERMEDIATE("Intermediate"),
  ADVANCED("Advanced"),
  BEHAVIOR_MOD("Behavioral Fix")
}

data class TrainingStep(
  val stepNumber: Int,
  val title: String,
  val instruction: String,
  val cueWord: String?,
  val petMindset: String, // What the pet is experiencing/learning
  val successCriteria: String,
  val commonMistake: String,
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
) {
  fun matchesSpecies(selectedSpecies: Set<String>, activePet: PetEntity?): Boolean {
    if (selectedSpecies.isNotEmpty()) {
      return speciesTarget.equals("Universal", ignoreCase = true) ||
        selectedSpecies.any { species ->
          speciesTarget.contains(species, ignoreCase = true)
        }
    }
    if (activePet != null) {
      val petSpecies = activePet.species
      if (petSpecies.equals("OTHER", ignoreCase = true)) {
        return speciesTarget.equals("Universal", ignoreCase = true)
      }
      return speciesTarget.equals("Universal", ignoreCase = true) ||
        speciesTarget.contains(petSpecies, ignoreCase = true) ||
        (petSpecies.equals("DOG", ignoreCase = true) && speciesTarget.contains("Dog", ignoreCase = true)) ||
        (petSpecies.equals("CAT", ignoreCase = true) && speciesTarget.contains("Cat", ignoreCase = true))
    }
    return true
  }
}
