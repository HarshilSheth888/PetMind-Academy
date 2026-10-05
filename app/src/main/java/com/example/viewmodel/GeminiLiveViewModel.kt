package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.util.audio.AudioPlayer
import com.example.util.audio.AudioRecorder
import com.example.util.audio.GeminiLiveWebSocketClient
import com.example.util.audio.GeminiLiveWebSocketListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class LiveConversationState {
  IDLE,
  CONNECTING,
  LISTENING,
  THINKING,
  SPEAKING,
  ERROR
}

class GeminiLiveViewModel(application: Application) : AndroidViewModel(application) {

  private val _uiState = MutableStateFlow(LiveConversationState.IDLE)
  val uiState: StateFlow<LiveConversationState> = _uiState.asStateFlow()

  private val _audioLevel = MutableStateFlow(0f)
  val audioLevel: StateFlow<Float> = _audioLevel.asStateFlow()

  private val _isMuted = MutableStateFlow(false)
  val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  private val audioRecorder = AudioRecorder()
  private val audioPlayer = AudioPlayer()
  private var webSocketClient: GeminiLiveWebSocketClient? = null

  private fun getApiKeySafely(): String {
    return try {
      val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
      (field.get(null) as? String) ?: ""
    } catch (_: Throwable) {
      ""
    }
  }

  fun startConversation(apiKeyOverride: String? = null) {
    if (_uiState.value != LiveConversationState.IDLE && _uiState.value != LiveConversationState.ERROR) {
      return
    }

    _errorMessage.value = null
    _uiState.value = LiveConversationState.CONNECTING

    val apiKey = apiKeyOverride?.ifBlank { null } ?: getApiKeySafely()

    if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
      _errorMessage.value = "Please configure a valid GEMINI_API_KEY in your local.properties or .env file."
      _uiState.value = LiveConversationState.ERROR
      return
    }

    // Initialize Audio Player
    audioPlayer.start(viewModelScope) { rmsLevel ->
      if (_uiState.value == LiveConversationState.SPEAKING) {
        _audioLevel.value = rmsLevel
      }
    }

    // Initialize WebSocket Client
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
      connect(apiKey)
    }
  }

  private fun startRecordingAudio() {
    audioRecorder.start(viewModelScope) { pcmData, rmsLevel ->
      if (!_isMuted.value) {
        if (_uiState.value == LiveConversationState.LISTENING) {
          _audioLevel.value = rmsLevel
          webSocketClient?.sendAudioChunk(pcmData)

          if (rmsLevel > 0.08f && _uiState.value == LiveConversationState.SPEAKING) {
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
    audioRecorder.stop()
    audioPlayer.stop()
    webSocketClient?.disconnect()
    webSocketClient = null
  }

  override fun onCleared() {
    super.onCleared()
    stopAudioAndConnection()
  }
}
