package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiModelProvider
import com.example.data.ArticlesData
import com.example.data.BehaviorDecoderData
import com.example.data.TrainingGuidesData
import com.example.data.db.AppDatabase
import com.google.firebase.ai.Chat
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.FunctionCallPart
import com.google.firebase.ai.type.FunctionResponsePart
import com.google.firebase.ai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import java.net.UnknownHostException
import java.util.Locale
import java.util.UUID

enum class ChatMessageSender { USER, MODEL }

data class ChatMessageItem(
  val id: String = UUID.randomUUID().toString(),
  val text: String,
  val sender: ChatMessageSender,
  val timestamp: Long = System.currentTimeMillis(),
)

sealed interface ChatUiState {
  object Idle : ChatUiState
  object Thinking : ChatUiState
  data class Streaming(val partialText: String) : ChatUiState
  data class Error(val message: String, val isRetryable: Boolean = true) : ChatUiState
  object Done : ChatUiState
}

class ChatViewModel(application: Application) : AndroidViewModel(application) {

  companion object {
    private const val TAG = "ChatViewModel"
    private const val MAX_HISTORY_MESSAGES = 10
    private const val GENERATION_TIMEOUT_MS = 30_000L
  }

  private val context: Context get() = getApplication<Application>().applicationContext
  private var generativeModel: GenerativeModel? = null
  private var chatSession: Chat? = null
  private var activeGenerationJob: Job? = null
  private var lastUserPrompt: String? = null

  private val _messages = MutableStateFlow<List<ChatMessageItem>>(emptyList())
  val messages: StateFlow<List<ChatMessageItem>> = _messages.asStateFlow()

  private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
  val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

  private val _navigationEvent = MutableSharedFlow<String>()
  val navigationEvent: SharedFlow<String> = _navigationEvent.asSharedFlow()

  init {
    initializeModel()
  }

  private fun initializeModel() {
    try {
      val model = GeminiModelProvider.createModel()
      generativeModel = model
      chatSession = model.startChat()
      Log.d(TAG, "Firebase AI Logic Chat session initialized successfully with model ${GeminiModelProvider.GEMINI_MODEL_ID}.")
    } catch (e: Exception) {
      Log.e(TAG, "Failed to initialize Firebase AI Logic model: ${e.message}", e)
    }

    _messages.value = listOf(
      ChatMessageItem(
        text = "Hello! I'm your AI Pet Companion. How can I assist you with your pet today?",
        sender = ChatMessageSender.MODEL
      )
    )
  }

  fun sendMessage(userPrompt: String) {
    val trimmedPrompt = userPrompt.trim()
    if (trimmedPrompt.isBlank()) return

    val currentState = _uiState.value
    if (currentState is ChatUiState.Thinking || currentState is ChatUiState.Streaming) return

    lastUserPrompt = trimmedPrompt

    // 1. Append User Message & update state to Thinking
    val userMessage = ChatMessageItem(text = trimmedPrompt, sender = ChatMessageSender.USER)
    _messages.value = _messages.value + userMessage
    _uiState.value = ChatUiState.Thinking

    // 2. Check for Gibberish / Key smash local guardrail
    if (isGibberishInput(trimmedPrompt)) {
      val fallbackMsg = ChatMessageItem(
        text = "I didn't quite catch that. Could you please rephrase or clarify your question?",
        sender = ChatMessageSender.MODEL
      )
      _messages.value = _messages.value + fallbackMsg
      _uiState.value = ChatUiState.Done
      return
    }

    // 3. Trim Chat History to prevent context window overflow
    trimHistoryIfNeeded()

    // 4. Launch streaming generation off main thread
    executeStreamingGeneration(trimmedPrompt)
  }

  private fun executeStreamingGeneration(userPrompt: String) {
    activeGenerationJob?.cancel()

    activeGenerationJob = viewModelScope.launch(Dispatchers.IO) {
      try {
        val session = chatSession ?: generativeModel?.startChat()?.also { chatSession = it }
        if (session == null) {
          _uiState.value = ChatUiState.Error("AI Model session unavailable. Please try again.", isRetryable = true)
          return@launch
        }

        var fullResponseText = ""
        var streamingMessageId: String? = null

        val result = withTimeoutOrNull(GENERATION_TIMEOUT_MS) {
          val responseStream = session.sendMessageStream(userPrompt)
          
          responseStream.collect { response ->
            // Check for Function Call Tool request
            val functionCalls = response.functionCalls
            if (functionCalls.isNotEmpty()) {
              val toolResponsePart = handleFunctionCallTools(functionCalls)
              if (toolResponsePart != null) {
                // Send tool response back to model
                val followUpStream = session.sendMessageStream(
                  content("function") { part(toolResponsePart) }
                )
                followUpStream.collect { followUpResponse ->
                  val textChunk = followUpResponse.text
                  if (!textChunk.isNullOrEmpty()) {
                    fullResponseText += textChunk
                    streamingMessageId = updateOrAppendStreamingMessage(streamingMessageId, fullResponseText)
                  }
                }
              }
            } else {
              val textChunk = response.text
              if (!textChunk.isNullOrEmpty()) {
                fullResponseText += textChunk
                streamingMessageId = updateOrAppendStreamingMessage(streamingMessageId, fullResponseText)
              }
            }
          }
          true
        }

        if (result == null) {
          _uiState.value = ChatUiState.Error("Request timed out. Please try again.", isRetryable = true)
          if (fullResponseText.isEmpty()) {
            _messages.value = _messages.value + ChatMessageItem(
              text = "Sorry, the response timed out. Please tap retry.",
              sender = ChatMessageSender.MODEL
            )
          }
        } else {
          if (fullResponseText.isBlank() && streamingMessageId == null) {
            val emptyFallback = "I'm not sure. Could you please rephrase or ask a clarifying question?"
            _messages.value = _messages.value + ChatMessageItem(
              text = emptyFallback,
              sender = ChatMessageSender.MODEL
            )
          }
          _uiState.value = ChatUiState.Done
        }

      } catch (e: UnknownHostException) {
        Log.e(TAG, "Network connection error during streaming", e)
        _uiState.value = ChatUiState.Error("Network connection unavailable. Please check your connection and retry.", isRetryable = true)
      } catch (e: Exception) {
        if (e is kotlinx.coroutines.CancellationException) {
          Log.d(TAG, "Streaming generation cancelled by user.")
          _uiState.value = ChatUiState.Idle
        } else {
          Log.e(TAG, "Error in streaming response: ${e.message}", e)
          _uiState.value = ChatUiState.Error(e.localizedMessage ?: "Failed to generate AI response.", isRetryable = true)
        }
      }
    }
  }

  private fun updateOrAppendStreamingMessage(messageId: String?, text: String): String {
    val id = messageId ?: UUID.randomUUID().toString()
    val currentList = _messages.value.toMutableList()
    
    val existingIndex = currentList.indexOfFirst { it.id == id }
    val updatedItem = ChatMessageItem(id = id, text = text, sender = ChatMessageSender.MODEL)
    
    if (existingIndex >= 0) {
      currentList[existingIndex] = updatedItem
    } else {
      currentList.add(updatedItem)
    }

    _messages.value = currentList
    _uiState.value = ChatUiState.Streaming(text)
    return id
  }

  fun cancelCurrentGeneration() {
    activeGenerationJob?.cancel()
    activeGenerationJob = null
    _uiState.value = ChatUiState.Idle
  }

  fun retryLastMessage() {
    lastUserPrompt?.let { prompt ->
      sendMessage(prompt)
    }
  }

  private suspend fun handleFunctionCallTools(functionCalls: List<FunctionCallPart>): FunctionResponsePart? = withContext(Dispatchers.IO) {
    val call = functionCalls.firstOrNull() ?: return@withContext null
    Log.d(TAG, "Executing Function Call tool: ${call.name} with args: ${call.args}")

    val responseJsonObject: JsonObject = when (call.name) {
      "getUserData" -> {
        try {
          val db = AppDatabase.getDatabase(context)
          val petDao = db.petDao()
          val pets = petDao.getAllPets().firstOrNull() ?: emptyList()

          if (pets.isNotEmpty()) {
            buildJsonObject {
              put("status", "success")
              putJsonArray("pets") {
                pets.forEach { pet ->
                  add(buildJsonObject {
                    put("id", pet.id)
                    put("name", pet.name)
                    put("species", pet.species)
                    put("breed", pet.breed)
                    put("ageMonths", pet.ageMonths)
                    put("notes", pet.notes)
                  })
                }
              }
            }
          } else {
            buildJsonObject {
              put("status", "no_pets_found")
              put("message", "No pet profiles found in database.")
            }
          }
        } catch (e: Exception) {
          buildJsonObject {
            put("status", "error")
            put("error", e.localizedMessage ?: "Failed to fetch user data")
          }
        }
      }

      "openScreen" -> {
        val route = call.args["screenRoute"]?.toString()?.replace("\"", "") ?: "home"
        viewModelScope.launch {
          _navigationEvent.emit(route)
        }
        buildJsonObject {
          put("status", "navigated")
          put("targetScreen", route)
        }
      }

      "searchItems" -> {
        val query = call.args["query"]?.toString()?.replace("\"", "")?.lowercase(Locale.getDefault()) ?: ""
        val category = call.args["category"]?.toString()?.replace("\"", "") ?: "all"

        val matchingArticles = if (category == "all" || category == "articles") {
          ArticlesData.articles.filter { 
            it.title.lowercase(Locale.getDefault()).contains(query) || 
            it.summary.lowercase(Locale.getDefault()).contains(query) 
          }.take(3)
        } else emptyList()

        val matchingGuides = if (category == "all" || category == "training") {
          TrainingGuidesData.guides.filter { 
            it.title.lowercase(Locale.getDefault()).contains(query) || 
            it.shortDescription.lowercase(Locale.getDefault()).contains(query) 
          }.take(3)
        } else emptyList()

        val matchingSignals = if (category == "all" || category == "decoder") {
          BehaviorDecoderData.signals.filter { 
            it.observationTitle.lowercase(Locale.getDefault()).contains(query) || 
            it.whatItMeans.lowercase(Locale.getDefault()).contains(query) 
          }.take(3)
        } else emptyList()

        buildJsonObject {
          put("status", "success")
          put("query", query)
          putJsonArray("articles") {
            matchingArticles.forEach { article ->
              add(buildJsonObject {
                put("title", article.title)
                put("summary", article.summary)
              })
            }
          }
          putJsonArray("trainingGuides") {
            matchingGuides.forEach { guide ->
              add(buildJsonObject {
                put("title", guide.title)
                put("difficulty", guide.difficulty.name)
                put("summary", guide.shortDescription)
              })
            }
          }
          putJsonArray("behaviorSignals") {
            matchingSignals.forEach { signal ->
              add(buildJsonObject {
                put("signal", signal.observationTitle)
                put("meaning", signal.whatItMeans)
              })
            }
          }
        }
      }

      else -> {
        buildJsonObject {
          put("status", "unknown_function")
        }
      }
    }

    FunctionResponsePart(call.name, responseJsonObject)
  }

  private fun trimHistoryIfNeeded() {
    val currentMessages = _messages.value
    if (currentMessages.size > MAX_HISTORY_MESSAGES * 2) {
      val firstGreeting = currentMessages.firstOrNull()
      val recentMessages = currentMessages.takeLast(MAX_HISTORY_MESSAGES)
      
      val trimmedList = mutableListOf<ChatMessageItem>()
      if (firstGreeting != null && !recentMessages.contains(firstGreeting)) {
        trimmedList.add(firstGreeting)
      }
      trimmedList.addAll(recentMessages)
      _messages.value = trimmedList
    }
  }

  private fun isGibberishInput(input: String): Boolean {
    val trimmed = input.trim()
    if (trimmed.length < 3) return false

    if (trimmed.all { it == trimmed[0] }) return true

    val lower = trimmed.lowercase(Locale.getDefault())
    val keySmashPatterns = listOf(
      "asdf", "sdfg", "dfgh", "fghj", "ghjk", "hjkl",
      "qwer", "wert", "erty", "rtyu", "tyui", "yuio", "uiop",
      "zxcv", "xcvb", "cvbn", "vbnm"
    )
    if (!lower.contains(" ") && keySmashPatterns.any { lower.contains(it) } && lower.length > 5) {
      return true
    }

    if (!lower.contains(" ") && lower.length >= 8 && lower.all { it.isLetter() }) {
      val vowels = lower.count { it in "aeiouy" }
      val vowelRatio = vowels.toDouble() / lower.length
      if (vowelRatio < 0.12) return true
    }

    return false
  }
}
