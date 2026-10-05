package com.example.util.audio

import android.util.Base64
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

interface GeminiLiveWebSocketListener {
  fun onConnected()
  fun onAudioDataReceived(pcmBytes: ByteArray)
  fun onTurnComplete()
  fun onInterrupted()
  fun onError(message: String)
  fun onClosed()
}

/**
 * High-performance WebSocket client communicating with the Gemini Multimodal Live API
 * (v1alpha GenerativeService.BidiGenerateContent).
 */
class GeminiLiveWebSocketClient(
  private val listener: GeminiLiveWebSocketListener
) {

  companion object {
    private const val TAG = "GeminiLiveClient"
    private const val BASE_URL =
      "wss://generativelanguage.googleapis.com/ws/google.ai.generativelanguage.v1alpha.GenerativeService.BidiGenerateContent"
    private const val DEFAULT_MODEL = "models/gemini-2.0-flash-exp"
  }

  private val okHttpClient = OkHttpClient.Builder()
    .readTimeout(0, TimeUnit.MILLISECONDS)
    .pingInterval(15, TimeUnit.SECONDS)
    .build()

  private var webSocket: WebSocket? = null
  private var isConnected = false

  fun connect(apiKey: String, voiceName: String = "Puck") {
    if (apiKey.isBlank()) {
      listener.onError("Gemini API key is missing.")
      return
    }

    val url = "$BASE_URL?key=$apiKey"
    val request = Request.Builder().url(url).build()

    webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
      override fun onOpen(webSocket: WebSocket, response: Response) {
        isConnected = true
        sendSetupConfig(webSocket, voiceName)
        listener.onConnected()
      }

      override fun onMessage(webSocket: WebSocket, text: String) {
        handleServerMessage(text)
      }

      override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        isConnected = false
        listener.onError("Connection error: ${t.localizedMessage ?: "Network failed"}")
      }

      override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        isConnected = false
        listener.onClosed()
      }

      override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        isConnected = false
        listener.onClosed()
      }
    })
  }

  private fun sendSetupConfig(ws: WebSocket, voiceName: String) {
    try {
      val payload = JSONObject().apply {
        put("setup", JSONObject().apply {
          put("model", DEFAULT_MODEL)
          put("generationConfig", JSONObject().apply {
            put("responseModalities", JSONArray().apply { put("AUDIO") })
            put("speechConfig", JSONObject().apply {
              put("voiceConfig", JSONObject().apply {
                put("prebuiltVoiceConfig", JSONObject().apply {
                  put("voiceName", voiceName)
                })
              })
            })
          })
        })
      }
      ws.send(payload.toString())
    } catch (e: Exception) {
      listener.onError("Failed to send setup config: ${e.message}")
    }
  }

  /**
   * Fast, zero-allocation JSON streaming method for real-time audio input.
   */
  fun sendAudioChunk(pcmData: ByteArray) {
    val ws = webSocket ?: return
    if (!isConnected) return

    try {
      val base64Data = Base64.encodeToString(pcmData, Base64.NO_WRAP)
      val payload = "{\"realtimeInput\":{\"mediaChunks\":[{\"mimeType\":\"audio/pcm;rate=16000\",\"data\":\"$base64Data\"}]}}"
      ws.send(payload)
    } catch (e: Exception) {
      Log.e(TAG, "Error sending audio chunk over WebSocket", e)
    }
  }

  private fun handleServerMessage(jsonText: String) {
    try {
      val root = JSONObject(jsonText)
      val serverContent = root.optJSONObject("serverContent") ?: return

      if (serverContent.optBoolean("interrupted", false)) {
        listener.onInterrupted()
      }

      val modelTurn = serverContent.optJSONObject("modelTurn")
      if (modelTurn != null) {
        val parts = modelTurn.optJSONArray("parts")
        if (parts != null) {
          for (i in 0 until parts.length()) {
            val part = parts.optJSONObject(i) ?: continue
            val inlineData = part.optJSONObject("inlineData") ?: continue
            val mimeType = inlineData.optString("mimeType", "")
            val base64Data = inlineData.optString("data", "")

            if (mimeType.contains("audio/pcm") && base64Data.isNotBlank()) {
              val pcmBytes = Base64.decode(base64Data, Base64.DEFAULT)
              listener.onAudioDataReceived(pcmBytes)
            }
          }
        }
      }

      if (serverContent.optBoolean("turnComplete", false)) {
        listener.onTurnComplete()
      }
    } catch (e: Exception) {
      Log.e(TAG, "Error parsing server message JSON", e)
    }
  }

  fun disconnect() {
    isConnected = false
    try {
      webSocket?.close(1000, "User requested disconnect")
    } catch (_: Exception) {}
    webSocket = null
  }
}
