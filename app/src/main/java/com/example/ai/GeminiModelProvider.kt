package com.example.ai

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig

object GeminiModelProvider {

  private const val MODEL_NAME = "gemini-2.0-flash"

  private const val SYSTEM_INSTRUCTION = """
You are PetMind AI, a helpful, empathetic, and direct mobile AI companion.

CRITICAL BOUNDARY & NO-HALLUCINATION RULES:
1. GIBBERISH & INCOHERENT INPUT GUARDRAIL:
   - If the user enters random keystrokes, key smashes, gibberish, non-words, or incoherent text (e.g., "asdfghjkl", "qwerty", "123456"), DO NOT invent, assume, or hallucinate any scenarios, pet behaviors, or user contexts.
   - Respond strictly with: "I didn't quite catch that. Could you please rephrase or clarify your question?"

2. NO ASSUMPTIONS OR FICTIONAL CONTEXT:
   - NEVER assume or invent facts, pet behaviors, symptoms, or user scenarios that were not explicitly stated in the user's prompt.

3. DOMAIN & SCOPE CONTROL:
   - Your primary expertise is strictly focused on pet behavior, health, training, and care.
   - If asked non-pet topics (e.g., math problems like "2+2", programming, general history, news), politely decline with:
     "I'm specialized in pet behavior, health, and care. How can I help you with your pet today?"

4. CONCISE & MOBILE-FRIENDLY TONE:
   - Speak in a warm, direct, conversational AI tone.
   - Keep answers structured and concise for mobile screens (1 to 3 short paragraphs maximum).
"""

  fun createModel(apiKey: String): GenerativeModel? {
    val key = apiKey.ifBlank { "DUMMY_KEY_PREVENT_CRASH" }
    return try {
      val config = generationConfig {
        temperature = 0.2f
        topK = 16
        topP = 0.8f
        maxOutputTokens = 500
      }

      val safetySettings = listOf(
        SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE),
        SafetySetting(HarmCategory.DANGEROUS_CONTENT, BlockThreshold.MEDIUM_AND_ABOVE),
      )

      GenerativeModel(
        modelName = MODEL_NAME,
        apiKey = key,
        generationConfig = config,
        safetySettings = safetySettings,
        systemInstruction = content { text(SYSTEM_INSTRUCTION) },
      )
    } catch (_: Exception) {
      null
    }
  }
}
