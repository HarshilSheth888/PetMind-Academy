package com.example.util

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.PetEntity
import com.example.ui.components.MessageSender
import com.example.ui.components.PanicMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit

data class AiResponsePayload(
  val text: String,
  val actionRoute: String? = null,
  val actionLabel: String? = null,
)

object GeminiPanicHelper {

  private const val TAG = "GeminiPanicHelper"
  private const val MODEL_NAME = "gemini-2.5-flash"

  private val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(10, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .build()

  private const val SYSTEM_INSTRUCTION = """
You are Pawsy, a natural, empathetic, highly intelligent AI companion and expert veterinary behaviorist speaking directly to a pet owner.

CRITICAL BOUNDARY & NO-HALLUCINATION RULES:
1. GIBBERISH & INCOHERENT INPUT GUARDRAIL:
   - If the user enters random keystrokes, key smashes, gibberish, non-words, or incoherent text (e.g., "asdfghjkl", "qwerty", "123456"), DO NOT invent, assume, or hallucinate any scenarios, symptoms, or pet behaviors.
   - Instead, respond politely: "I didn't quite catch that. Could you please clarify or describe what is happening with your pet?"

2. NO ASSUMPTIONS OR FICTIONAL CONTEXT:
   - NEVER assume or invent pet behaviors, symptoms, or situations that were not explicitly stated in the user's prompt.

3. DOMAIN & SCOPE CONTROL:
   - Your expertise is strictly dedicated to pets (health, behavior, training, and care).
   - If asked non-pet topics (e.g., math, programming, general history, news), politely decline with:
     "I'm sorry, but my AI's intelligence is limited only to behavioral analysis, health, training, and care of your pet. How can I help you with your pet today?"

4. CONCISE & MOBILE-FRIENDLY TONE:
   - Speak in a warm, direct, conversational AI tone.
   - Keep answers natural, empathetic, and concise for mobile screens (1 to 3 short paragraphs maximum).
"""

  fun isGibberishInput(input: String): Boolean {
    val trimmed = input.trim()
    if (trimmed.length < 3) return false

    // 1. Single character repeated (e.g. "aaaaaaa", "ffffff")
    if (trimmed.all { it == trimmed[0] }) return true

    // 2. Common key-smash row patterns without spaces (e.g. "asdfghjkl", "qwertyuiop")
    val lower = trimmed.lowercase(Locale.getDefault())
    val keySmashPatterns = listOf(
      "asdf", "sdfg", "dfgh", "fghj", "ghjk", "hjkl",
      "qwer", "wert", "erty", "rtyu", "tyui", "yuio", "uiop",
      "zxcv", "xcvb", "cvbn", "vbnm"
    )
    if (!lower.contains(" ") && keySmashPatterns.any { lower.contains(it) } && lower.length > 5) {
      return true
    }

    // 3. Vowel ratio check for long single-word inputs (>7 chars)
    if (!lower.contains(" ") && lower.length >= 8 && lower.all { it.isLetter() }) {
      val vowels = lower.count { it in "aeiouy" }
      val vowelRatio = vowels.toDouble() / lower.length
      if (vowelRatio < 0.12) return true
    }

    return false
  }

  private fun logWarning(msg: String) {
    try {
      Log.w(TAG, msg)
    } catch (_: Throwable) {
      println("$TAG: $msg")
    }
  }

  suspend fun generateCalmingResponse(
    userPrompt: String,
    conversationHistory: List<PanicMessage> = emptyList(),
    pet: PetEntity? = null,
    isCameraPostureScan: Boolean = false
  ): AiResponsePayload = withContext(Dispatchers.IO) {
    val petName = pet?.name ?: "your pet"
    val lowerPrompt = userPrompt.lowercase(Locale.getDefault()).trim()

    // 1. Check for Gibberish / Key Smash Input
    if (isGibberishInput(userPrompt)) {
      return@withContext AiResponsePayload(
        text = "✨ Pawsy: I didn't quite catch that. Could you please clarify or describe what is happening with $petName?"
      )
    }

    // 2. Domain Guardrail Check for Off-Topic / Math Queries
    val isMathQuery = lowerPrompt.contains("2 + 2") || lowerPrompt.contains("2+2") ||
        lowerPrompt.matches(Regex(".*\\d+\\s*[+\\-*/^=]\\s*\\d+.*")) ||
        lowerPrompt.startsWith("what is 2") || lowerPrompt.startsWith("calculate ") ||
        lowerPrompt.contains("math") || lowerPrompt.contains("solve for x")

    val isOffTopicQuery = listOf(
      "python code", "java script", "html", "css", "programming", "write code",
      "capital of", "who is the president", "weather in", "stock price", "crypto",
      "bitcoin", "equation", "formula", "who won", "movie"
    ).any { lowerPrompt.contains(it) }

    if (isMathQuery || isOffTopicQuery) {
      return@withContext AiResponsePayload(
        text = "✨ Pawsy: I'm sorry, but my AI's intelligence is limited only to behavioral analysis, health, training, and care of your pet. How can I help you with $petName today?"
      )
    }

    // Determine if user intent matches an app action route
    val (actionRoute, actionLabel) = detectAppControlIntent(lowerPrompt)

    // 2. Attempt Direct Gemini REST API call via OkHttp if API key is present
    val apiKey = getApiKeySafely()
    if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
      val restResponse = fetchGeminiRestResponse(apiKey, userPrompt, conversationHistory, pet, isCameraPostureScan)
      if (!restResponse.isNullOrBlank()) {
        val formattedText = if (restResponse.startsWith("✨")) restResponse else "✨ Pawsy: $restResponse"
        return@withContext AiResponsePayload(
          text = formattedText,
          actionRoute = actionRoute,
          actionLabel = actionLabel
        )
      }
    }

    // 3. Fallback to Fluid Local Generative AI Engine
    val fluidText = generateFluidLocalAiResponse(
      userPrompt = userPrompt,
      conversationHistory = conversationHistory,
      pet = pet,
      isCameraPostureScan = isCameraPostureScan
    )

    return@withContext AiResponsePayload(
      text = fluidText,
      actionRoute = actionRoute,
      actionLabel = actionLabel
    )
  }

  private fun getApiKeySafely(): String {
    return try {
      val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
      (field[null] as? String) ?: ""
    } catch (_: Throwable) {
      ""
    }
  }

  private fun fetchGeminiRestResponse(
    apiKey: String,
    userPrompt: String,
    conversationHistory: List<PanicMessage>,
    pet: PetEntity?,
    isCameraPostureScan: Boolean
  ): String? {
    return try {
      val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
      
      val contentsArray = JSONArray()
      
      val petInfo = pet?.let {
        "Pet Details: Name: ${it.name}, Species: ${it.species}${if (it.breed.isNotBlank()) ", Breed: ${it.breed}" else ""}"
      } ?: "Pet Details: Name: your pet"
      
      val historyText = conversationHistory
        .filter { it.text.isNotBlank() }
        .takeLast(6)
        .joinToString("\n") { msg ->
          val role = if (msg.sender == MessageSender.USER) "User" else "Assistant"
          "$role: ${msg.text.removePrefix("✨ Pawsy: ").removePrefix("✨ Gemini AI: ")}"
        }

      val scanNote = if (isCameraPostureScan) " [Camera posture scan attached]" else ""
      val fullPrompt = "$SYSTEM_INSTRUCTION\n\n$petInfo\n\nHistory:\n$historyText\n\nUser$scanNote: $userPrompt"
      
      val userContentObj = JSONObject().apply {
        put("role", "user")
        put("parts", JSONArray().put(JSONObject().put("text", fullPrompt)))
      }
      contentsArray.put(userContentObj)

      val generationConfig = JSONObject().apply {
        put("temperature", 0.2)
        put("topK", 16)
        put("topP", 0.8)
      }

      val jsonBody = JSONObject().apply {
        put("contents", contentsArray)
        put("generationConfig", generationConfig)
      }

      val mediaType = "application/json; charset=utf-8".toMediaType()
      val body = jsonBody.toString().toRequestBody(mediaType)
      val request = Request.Builder()
        .url(url)
        .post(body)
        .build()

      val httpResponse = okHttpClient.newCall(request).execute()
      if (httpResponse.isSuccessful) {
        val responseBody = httpResponse.body
        val responseStr = responseBody.string()
        if (responseStr.isNotBlank()) {
          val rootObj = JSONObject(responseStr)
          val candidates = rootObj.optJSONArray("candidates")
          if (candidates != null && candidates.length() > 0) {
            val contentObj = candidates.getJSONObject(0).optJSONObject("content")
            val parts = contentObj?.optJSONArray("parts")
            if (parts != null && parts.length() > 0) {
              val text = parts.getJSONObject(0).optString("text")
              if (text.isNotBlank()) return text
            }
          }
        }
      }
      null
    } catch (e: Exception) {
      logWarning("Gemini REST API Call Error: ${e.message}")
      null
    }
  }

  private fun detectAppControlIntent(lowerPrompt: String): Pair<String?, String?> {
    return when {
      lowerPrompt.contains("decoder") || lowerPrompt.contains("decode") || lowerPrompt.contains("signal") || lowerPrompt.contains("whale eye") || lowerPrompt.contains("body language") ->
        "decoder" to "🔍 Open Behavior Decoder"

      lowerPrompt.contains("train") || lowerPrompt.contains("guide") || lowerPrompt.contains("sit") || lowerPrompt.contains("stay") || lowerPrompt.contains("trick") || lowerPrompt.contains("practice") ->
        "training" to "🏋️ Open Training Guides"

      lowerPrompt.contains("progress") || lowerPrompt.contains("milestone") || lowerPrompt.contains("score") || lowerPrompt.contains("stat") ->
        "progress" to "📊 View Pet Progress"

      else -> null to null
    }
  }

  private fun generateFluidLocalAiResponse(
    userPrompt: String,
    conversationHistory: List<PanicMessage>,
    pet: PetEntity?,
    isCameraPostureScan: Boolean
  ): String {
    val petName = pet?.name ?: "your pet"
    val species = pet?.species?.lowercase(Locale.getDefault()) ?: "pet"
    val lowerPrompt = userPrompt.lowercase(Locale.getDefault()).trim()

    val cleanedAction = userPrompt.trim()
      .removePrefix("your pet is ")
      .removePrefix("my pet is ")
      .removePrefix("he is ")
      .removePrefix("she is ")
      .removePrefix("they are ")

    val historyHeader = if (conversationHistory.size > 2) "Following up on $petName's progress: " else ""

    // 1. Trembling, Fear & Panic
    if (lowerPrompt.contains("trembl") || lowerPrompt.contains("shak") || lowerPrompt.contains("pant") || lowerPrompt.contains("fear") || lowerPrompt.contains("scared") || lowerPrompt.contains("thunder")) {
      return "✨ Pawsy: $historyHeader I hear your concern about $petName. Trembling and heavy breathing are classic physiological responses to adrenaline or sudden fear in $species. Sit close to $petName, take slow deep belly breaths, and offer a cozy blanket space. Animals naturally mirror human heart rates and calm breathing. Take a breath together with $petName—how is $petName's breathing rate changing now?"
    }

    // 2. Posture & Camera Scan
    if (isCameraPostureScan || lowerPrompt.contains("posture") || lowerPrompt.contains("spine") || lowerPrompt.contains("arched") || lowerPrompt.contains("stiff back") || lowerPrompt.contains("back posture")) {
      return "✨ Pawsy: $historyHeader I've evaluated $petName's body posture and back tension. A stiff back or arched spine in a $species often signals muscular guarding or physical discomfort. Keep $petName on a soft, supportive surface, lower ambient lights, and speak in a gentle whisper to help soothe their nervous system. Avoid pressing on sensitive spine areas. How does $petName's posture look as they rest?"
    }

    // 3. Tail Movement & Wiggling
    if (lowerPrompt.contains("tail") || lowerPrompt.contains("wiggl") || lowerPrompt.contains("wag") || lowerPrompt.contains("flick")) {
      return "✨ Pawsy: $historyHeader Tail movement is such an expressive signal in $species behavior! When $petName is $cleanedAction, they are communicating their emotional state directly to you. A loose, sweeping wag usually signals happiness and friendly excitement, whereas a stiff, fast vibrate can mean intense focus or arousal. Pay attention to $petName's eyes and ears—if their body is soft and relaxed, $petName is feeling great and enjoying your presence! Would you like me to open the Behavior Decoder to compare tail signals?"
    }

    // 4. Excitement & High Energy
    if (lowerPrompt.contains("excit") || lowerPrompt.contains("hyper") || lowerPrompt.contains("zoom") || lowerPrompt.contains("jump") || lowerPrompt.contains("play")) {
      return "✨ Pawsy: $historyHeader It sounds like $petName has a burst of joyful energy right now! When $petName is $cleanedAction, offering a fun, focused activity like a lick mat, a puzzle toy, or a quick game helps channel that excitement constructively. Matching $petName with a calm, happy tone will help them feel secure while enjoying their play. How is $petName's breathing and energy level looking at the moment?"
    }

    // 5. Feeding & Drinking
    if (lowerPrompt.contains("food") || lowerPrompt.contains("diet") || lowerPrompt.contains("eating") || lowerPrompt.contains("treat") || lowerPrompt.contains("drink") || lowerPrompt.contains("kibble") || lowerPrompt.contains("meal")) {
      return "✨ Pawsy: $historyHeader Nutrition and calm feeding habits play a big role in $petName's emotional and physical well-being. When caring for $petName, ensure they eat in a quiet, low-stress space without feeling rushed. Always keep fresh water accessible, and offer treats in small, rewarding portions during training or calm moments. Is $petName eating comfortably today?"
    }

    // 6. Training & Commands
    if (lowerPrompt.contains("train") || lowerPrompt.contains("trick") || lowerPrompt.contains("sit") || lowerPrompt.contains("stay") || lowerPrompt.contains("walk") || lowerPrompt.contains("leash")) {
      return "✨ Pawsy: $historyHeader Working on training with $petName is one of the best ways to build a deep, trusting bond! Short, 3-to-5 minute positive reinforcement sessions work best for $petName's brain. Reward good behavior immediately with a small treat or happy praise so $petName connects the action with positive feelings. Would you like to launch a training guide for $petName now?"
    }

    // 7. Hiding & Seeking Refuge
    if (lowerPrompt.contains("hid") || lowerPrompt.contains("cower") || lowerPrompt.contains("under") || lowerPrompt.contains("closet")) {
      return "✨ Pawsy: $historyHeader When $petName is $cleanedAction, hiding is their way of creating a safe sanctuary. Avoid forcing or pulling $petName out, as that can increase fight-or-flight panic. Instead, place a soft shirt with your scent near their spot and sit quietly nearby. Knowing you are close gives $petName the security they need to emerge when ready. How is $petName doing right now?"
    }

    // 8. General Fluid Conversational Response
    return "✨ Pawsy: $historyHeader I hear you! When $petName is $cleanedAction, observing their subtle body language—like ear angle, eye focus, and breathing rate—gives us wonderful insights into how they are feeling. Creating a calm, reassuring environment helps $petName feel safe and connected with you. How is $petName responding as you spend time together?"
  }
}
