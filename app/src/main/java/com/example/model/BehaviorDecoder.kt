package com.example.model

enum class EmotionalState(val label: String, val badgeColor: String, val icon: String) {
  CALM_CONTENT("Calm & Content", "Green", "😊"),
  PLAYFUL_EXCITED("Playful & Engaged", "Blue", "🎾"),
  ALERT_FOCUSED("Alert & Assessing", "Amber", "👀"),
  ANXIOUS_STRESSED("Stressed / Subtle Fear", "Rose", "⚠️"),
  DEFENSIVE_FEARFUL("Defensive / Fear Threat", "Purple", "🛑"),
  OVERSTIMULATED("Overstimulated", "Rose", "⚡")
}

data class BehaviorSignal(
  val id: String,
  val species: String, // "Dog", "Cat"
  val bodyPart: String, // "Eyes", "Ears", "Tail", "Mouth/Face", "Body Posture", "Vocalization"
  val observationTitle: String,
  val description: String,
  val emotionalState: EmotionalState,
  val whatItMeans: String,
  val scientificExplanation: String,
  val whatOwnerShouldDo: List<String>,
  val whatNOTToDo: List<String>
)
