@file:Suppress("OPT_IN_USAGE", "OPT_IN_USAGE_ERROR")

package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiModelProvider
import com.example.util.audio.AudioPlayer
import com.example.util.audio.AudioRecorder
import com.example.util.audio.GeminiLiveWebSocketClient
import com.example.util.audio.GeminiLiveWebSocketListener
import com.google.firebase.Firebase
import com.google.firebase.ai.LiveGenerativeModel
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.Tool
import com.google.firebase.ai.type.liveGenerationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class LiveConversationState {
  IDLE,
  CONNECTING,
  LISTENING,
  THINKING,
  SPEAKING,
  ERROR
}

class GeminiLiveViewModel(application: Application) : AndroidViewModel(application) {

  companion object {
    private const val TAG = "GeminiLiveViewModel"

    /**
     * Centralized single model constant for Gemini Live Native Audio model.
     */
    const val GEMINI_LIVE_MODEL_ID = "gemini-2.5-flash-native-audio-preview-12-2025"
  }

  private val _uiState = MutableStateFlow(LiveConversationState.IDLE)
  val uiState: StateFlow<LiveConversationState> = _uiState.asStateFlow()

  private val _audioLevel = MutableStateFlow(0f)
  val audioLevel: StateFlow<Float> = _audioLevel.asStateFlow()

  private val _isMuted = MutableStateFlow(value = false)
  val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val audioRecorder = AudioRecorder()
  private val audioPlayer = AudioPlayer()
  private var liveModel: LiveGenerativeModel? = null
  private var webSocketClient: GeminiLiveWebSocketClient? = null
  private var sessionJob: Job? = null

  fun startConversation() {
    if ((_uiState.value != LiveConversationState.IDLE) && (_uiState.value != LiveConversationState.ERROR)) {
      return
    }

    _errorMessage.value = null
    _uiState.value = LiveConversationState.CONNECTING

    viewModelScope.launch(Dispatchers.IO) {
      try {
        // Initialize Audio Player
        audioPlayer.start(viewModelScope) { rmsLevel ->
          if (_uiState.value == LiveConversationState.SPEAKING) {
            _audioLevel.value = rmsLevel
          }
        }

        // Initialize Firebase AI Logic Live Model with tool definitions
        val model = Firebase.ai(backend = GenerativeBackend.googleAI()).liveModel(
          modelName = GEMINI_LIVE_MODEL_ID,
          generationConfig = liveGenerationConfig {
            responseModality = ResponseModality.AUDIO
          },
          tools = listOf(
            Tool.functionDeclarations(
              listOf(
                GeminiModelProvider.getUserDataTool,
                GeminiModelProvider.openScreenTool,
                GeminiModelProvider.searchItemsTool,
              )
            )
          )
        )
        liveModel = model

        // Connect WebSocket client for real-time audio streaming
        webSocketClient = GeminiLiveWebSocketClient(object : GeminiLiveWebSocketListener {
          override fun onConnected() {
            _uiState.value = LiveConversationState.LISTENING
            startRecordingAudio()
          }

          override fun onAudioDataReceived(pcmBytes: ByteArray) {
            if (_uiState.value != LiveConversationState.SPEAKING) {
              _uiState.value = LiveConversationState.SPEAKING
            }
            audioPlayer.playChunk(pcmBytes)
          }

          override fun onTurnComplete() {
            _uiState.value = LiveConversationState.LISTENING
            _audioLevel.value = 0f
          }

          override fun onInterrupted() {
            audioPlayer.flush()
            _uiState.value = LiveConversationState.LISTENING
            _audioLevel.value = 0f
          }

          override fun onError(message: String) {
            _errorMessage.value = message
            _uiState.value = LiveConversationState.ERROR
            stopAudioAndConnection()
          }

          override fun onClosed() {
            if (_uiState.value != LiveConversationState.ERROR) {
              _uiState.value = LiveConversationState.IDLE
            }
            stopAudioAndConnection()
          }
        }).apply {
          connect("")
        }

      } catch (e: Exception) {
        Log.e(TAG, "Failed to connect to Gemini Live session: ${e.message}", e)
        withContext(Dispatchers.Main) {
          _errorMessage.value = "Live voice connection failed. You can fallback to text chat."
          _uiState.value = LiveConversationState.ERROR
          stopAudioAndConnection()
        }
      }
    }
  }

  private fun startRecordingAudio() {
    audioRecorder.start(viewModelScope) { pcmData, rmsLevel ->
      if (!_isMuted.value) {
        if (_uiState.value == LiveConversationState.LISTENING) {
          _audioLevel.value = rmsLevel
          webSocketClient?.sendAudioChunk(pcmData)

          // Barge-in / interruption detection: If user starts speaking while AI is playing audio
          if (rmsLevel > 0.08f && (_uiState.value == LiveConversationState.SPEAKING)) {
            audioPlayer.flush()
            _uiState.value = LiveConversationState.LISTENING
          }
        }
      } else if (_uiState.value == LiveConversationState.LISTENING) {
        _audioLevel.value = 0f
      }
    }
  }

  fun toggleMute() {
    _isMuted.value = !_isMuted.value
  }

  fun endConversation() {
    stopAudioAndConnection()
    _uiState.value = LiveConversationState.IDLE
    _audioLevel.value = 0f
    _errorMessage.value = null
  }

  private fun stopAudioAndConnection() {
    sessionJob?.cancel()
    sessionJob = null
    audioRecorder.stop()
    audioPlayer.stop()
    webSocketClient?.disconnect()
    webSocketClient = null
    liveModel = null
  }

  override fun onCleared() {
    stopAudioAndConnection()
  }
}
