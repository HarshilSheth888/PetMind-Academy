package com.example.ai

import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.FunctionDeclaration
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.HarmBlockThreshold
import com.google.firebase.ai.type.HarmCategory
import com.google.firebase.ai.type.SafetySetting
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig

object GeminiModelProvider {

  /**
   * Centralized single model constant for the text/chat AI agent.
   * Can be swapped or configured via Remote Config without code release.
   */
  const val GEMINI_MODEL_ID = "gemini-3.5-flash"

  const val SYSTEM_INSTRUCTION = """
You are Pawsy, a direct, helpful, and empathetic companion for pet care, behavior, and training.

CRITICAL BOUNDARY & NO-HALLUCINATION RULES:
1. STRICT DATA BOUNDARY: Answer ONLY using provided app context, database records, or tool results. Never invent features, screens, pets, or data.
2. UNCERTAINTY: If you are unsure or if required information is missing, explicitly say "I'm not sure" and ask a clarifying question.
3. TOOL USE: Use available function calling tools (getUserData, openScreen, searchItems) when asked about user pets, training guides, articles, or navigation.
4. GIBBERISH / INCOHERENT INPUT: If the user inputs random keystrokes or gibberish, do NOT invent scenarios. Reply: "I didn't quite catch that. Could you please rephrase or clarify your question?"
5. SCOPE: Focus strictly on pet behavior, health, training, and care. For non-pet topics, politely decline.
6. CONCISE TONE: Keep responses warm, structured, and concise (1-3 short paragraphs).
"""

  // Function Calling Tool: getUserData
  val getUserDataTool = FunctionDeclaration(
    "getUserData",
    "Retrieves stored user data including active pet profiles, species, breed, age, and milestones.",
    mapOf(
      "petId" to Schema.string("Optional pet ID to filter profile details, or 'all' for all pets.")
    )
  )

  // Function Calling Tool: openScreen
  val openScreenTool = FunctionDeclaration(
    "openScreen",
    "Navigates the user to a specific screen in the PetMind app.",
    mapOf(
      "screenRoute" to Schema.string("The route to open. Supported: 'home', 'training', 'decoder', 'progress', 'panic'"),
      "reason" to Schema.string("Brief explanation for navigating to this screen.")
    )
  )

  // Function Calling Tool: searchItems
  val searchItemsTool = FunctionDeclaration(
    "searchItems",
    "Searches in-app articles, behavior decoder signals, and training guides.",
    mapOf(
      "query" to Schema.string("Search query term (e.g., 'tail wagging', 'leash pulling', 'separation anxiety')"),
      "category" to Schema.string("Category to filter by: 'articles', 'training', 'decoder', or 'all'")
    )
  )

  fun createModel(): GenerativeModel {
    val config = generationConfig {
      temperature = 0.2f
      topK = 16
      topP = 0.8f
      maxOutputTokens = 800
    }

    val safetySettings = listOf(
      SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.MEDIUM_AND_ABOVE),
      SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.MEDIUM_AND_ABOVE),
      SafetySetting(HarmCategory.DANGEROUS_CONTENT, HarmBlockThreshold.MEDIUM_AND_ABOVE),
    )

    val tools = listOf(
      Tool.functionDeclarations(listOf(getUserDataTool, openScreenTool, searchItemsTool)),
      Tool.googleSearch()
    )

    return Firebase.ai(backend = GenerativeBackend.googleAI()).generativeModel(
      modelName = GEMINI_MODEL_ID,
      generationConfig = config,
      safetySettings = safetySettings,
      systemInstruction = content { text(SYSTEM_INSTRUCTION) },
      tools = tools
    )
  }
}
