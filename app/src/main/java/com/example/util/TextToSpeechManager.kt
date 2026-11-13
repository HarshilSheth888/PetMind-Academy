package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Manages Android's TextToSpeech engine lifecycle and provides
 * observable state for UI to react to speaking/stopped transitions.
 */
class TextToSpeechManager(context: Context) {

  private var tts: TextToSpeech? = null
  private var isInitialized = false

  private val _isSpeaking = MutableStateFlow(false)
  val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

  /** The ID of the content currently being spoken (e.g. signal ID or article ID). */
  private val _currentSpeakingId = MutableStateFlow<String?>(null)
  val currentSpeakingId: StateFlow<String?> = _currentSpeakingId.asStateFlow()

  init {
    tts = TextToSpeech(context.applicationContext) { status ->
      isInitialized = status == TextToSpeech.SUCCESS
      if (isInitialized) {
        tts?.language = Locale.US
        tts?.setSpeechRate(0.95f)
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
          override fun onStart(utteranceId: String?) {
            _isSpeaking.value = true
          }

          override fun onDone(utteranceId: String?) {
            _isSpeaking.value = false
            _currentSpeakingId.value = null
          }

          @Deprecated("Deprecated in Java")
          override fun onError(utteranceId: String?) {
            _isSpeaking.value = false
            _currentSpeakingId.value = null
          }

          override fun onError(utteranceId: String?, errorCode: Int) {
            _isSpeaking.value = false
            _currentSpeakingId.value = null
          }
        })
      }
    }
  }

  /**
   * Speaks the given [text] aloud. If something is already being spoken with the same
   * [contentId], it stops instead (toggle behavior).
   *
   * @param text The full text to read aloud.
   * @param contentId A unique identifier for the content (signal ID, article ID, etc.)
   */
  fun speakOrStop(text: String, contentId: String) {
    if (!isInitialized) return

    // Toggle: if already speaking this content, stop
    if (_isSpeaking.value && _currentSpeakingId.value == contentId) {
      stop()
      return
    }

    // Stop any current speech, then start new
    tts?.stop()
    _currentSpeakingId.value = contentId
    tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, contentId)
  }

  fun stop() {
    tts?.stop()
    _isSpeaking.value = false
    _currentSpeakingId.value = null
  }

  fun shutdown() {
    tts?.stop()
    tts?.shutdown()
    tts = null
    isInitialized = false
    _isSpeaking.value = false
    _currentSpeakingId.value = null
  }
}
