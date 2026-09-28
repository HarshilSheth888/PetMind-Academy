package com.example.model

enum class EmotionalState(val label: String, val icon: String) {
  CALM_CONTENT("Calm & Content", "😊"),
  PLAYFUL_EXCITED("Playful & Engaged", "🎾"),
  ALERT_FOCUSED("Alert & Assessing", "👀"),
  ANXIOUS_STRESSED("Stressed / Subtle Fear", "⚠️"),
  DEFENSIVE_FEARFUL("Defensive / Fear Threat", "🛑"),
  OVERSTIMULATED("Overstimulated", "⚡")
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
  val whatNOTToDo: List<String>,
)
