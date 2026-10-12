package com.example.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.ai.GeminiModelProvider
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
  object Loading : ChatUiState
  data class Error(val message: String) : ChatUiState
}

class ChatViewModel(apiKeyOverride: String? = null) : ViewModel() {

  companion object {
    private const val TAG = "ChatViewModel"
  }

  private val apiKey = (apiKeyOverride?.ifBlank { null } ?: getApiKeySafely()).trim()
  private var generativeModel: GenerativeModel? = null
  private var chatSession: Chat? = null

  private val _messages = MutableStateFlow<List<ChatMessageItem>>(emptyList())
  val messages: StateFlow<List<ChatMessageItem>> = _messages.asStateFlow()

  private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Idle)
  val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

  init {
    if (apiKey.isBlank() || (apiKey == "MY_GEMINI_API_KEY")) {
      Log.w(TAG, "GEMINI_API_KEY is missing or set to placeholder 'MY_GEMINI_API_KEY'. Live AI generation is disabled.")
    } else {
      try {
        val model = GeminiModelProvider.createModel(apiKey)
        generativeModel = model
        chatSession = model?.startChat()
        Log.d(TAG, "Gemini Chat session initialized successfully.")
      } catch (e: Exception) {
        Log.e(TAG, "Failed to initialize Gemini model: ${e.message}", e)
      }
    }

    _messages.value = listOf(
      ChatMessageItem(
        text = if (apiKey.isBlank() || (apiKey == "MY_GEMINI_API_KEY")) {
          "Hello! I'm your AI Pet Companion. Please configure a valid GEMINI_API_KEY in local.properties or .env to start chatting."
        } else {
          "Hello! I'm your AI Pet Companion. How can I assist you today?"
        },
        sender = ChatMessageSender.MODEL,
      ),
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

  fun sendMessage(userPrompt: String) {
    val trimmedPrompt = userPrompt.trim()
    if (trimmedPrompt.isBlank() || (_uiState.value is ChatUiState.Loading)) return

    // 1. Append User Message
    val userMessage = ChatMessageItem(text = trimmedPrompt, sender = ChatMessageSender.USER)
    _messages.value += userMessage
    _uiState.value = ChatUiState.Loading

    // 2. Check if Chat session is available
    val activeSession = chatSession
    if (apiKey.isBlank() || (apiKey == "MY_GEMINI_API_KEY") || (activeSession == null)) {
      val fallbackResponse = ChatMessageItem(
        text = "API Key is missing or unconfigured. Please add your GEMINI_API_KEY to local.properties or .env.",
        sender = ChatMessageSender.MODEL,
      )
      _messages.value += fallbackResponse
      _uiState.value = ChatUiState.Idle
      return
    }

    // 3. Local Heuristic Pre-Validation for Key-Smashes / Gibberish
    if (isGibberishInput(trimmedPrompt)) {
      val fallbackResponse = ChatMessageItem(
        text = "I didn't quite catch that. Could you please rephrase or clarify your question?",
        sender = ChatMessageSender.MODEL,
      )
      _messages.value += fallbackResponse
      _uiState.value = ChatUiState.Idle
      return
    }

    // 4. Dispatch to Gemini API asynchronously on IO Dispatcher
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val response = activeSession.sendMessage(trimmedPrompt)
        val responseText = response.text

        val aiMessage = if (!responseText.isNullOrBlank()) {
          ChatMessageItem(text = responseText, sender = ChatMessageSender.MODEL)
        } else {
          ChatMessageItem(
            text = "I didn't quite catch that. Could you please rephrase your request?",
            sender = ChatMessageSender.MODEL,
          )
        }

        _messages.value += aiMessage
        _uiState.value = ChatUiState.Idle
      } catch (e: Exception) {
        _uiState.value = ChatUiState.Error(e.localizedMessage ?: "Failed to connect to AI server.")
        _messages.value += ChatMessageItem(
          text = "Sorry, I ran into an error processing your message. Please try again.",
          sender = ChatMessageSender.MODEL,
        )
      }
    }
  }

  /**
   * Fast local heuristic check for key-smashes and non-sensical inputs.
   */
  private fun isGibberishInput(input: String): Boolean {
    val trimmed = input.trim()
    if (trimmed.length < 3) return false

    // Check for repeated single character (e.g. "aaaaaaa", "ffffff")
    if (trimmed.all { it == trimmed[0] }) return true

    // Check for common key-smash row patterns without spaces (e.g. "asdfghjkl", "qwertyuiop")
    val lower = trimmed.lowercase(Locale.getDefault())
    val keySmashPatterns = listOf(
      "asdf", "sdfg", "dfgh", "fghj", "ghjk", "hjkl",
      "qwer", "wert", "erty", "rtyu", "tyui", "yuio", "uiop",
      "zxcv", "xcvb", "cvbn", "vbnm",
    )
    if (!lower.contains(" ") && (keySmashPatterns.any { lower.contains(it) }) && (lower.length > 5)) {
      return true
    }

    // Vowel ratio check for single unspaced word (>7 chars)
    if (!lower.contains(" ") && (lower.length >= 8) && (lower.all { it.isLetter() })) {
      val vowels = lower.count { it in "aeiouy" }
      val vowelRatio = vowels.toDouble() / lower.length
      if (vowelRatio < 0.12) return true
    }

    return false
  }
}
