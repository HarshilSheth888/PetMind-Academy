package com.example.util.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.NoiseSuppressor
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Captures raw PCM 16-bit mono audio at 16,000 Hz for streaming to Gemini Multimodal Live API.
 * Includes background noise suppression and echo cancellation when supported by device hardware.
 */
class AudioRecorder {

  companion object {
    private const val TAG = "AudioRecorder"
    const val SAMPLE_RATE = 16000
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private const val CHUNK_SIZE_BYTES = 2048 // ~64ms chunk
  }

  private var audioRecord: AudioRecord? = null
  private var noiseSuppressor: NoiseSuppressor? = null
  private var echoCanceler: AcousticEchoCanceler? = null

  private val isRecording = AtomicBoolean(false)
  private var recordJob: Job? = null

  @SuppressLint("MissingPermission")
  fun start(
    scope: CoroutineScope,
    onAudioChunk: (data: ByteArray, rmsLevel: Float) -> Unit
  ) {
    if (isRecording.getAndSet(true)) return

    val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
    val bufferSize = maxOf(minBufferSize, CHUNK_SIZE_BYTES * 4)

    try {
      audioRecord = AudioRecord(
        MediaRecorder.AudioSource.VOICE_COMMUNICATION,
        SAMPLE_RATE,
        CHANNEL_CONFIG,
        AUDIO_FORMAT,
        bufferSize
      )

      val sessionId = audioRecord?.audioSessionId ?: 0
      if (sessionId != 0) {
        if (NoiseSuppressor.isAvailable()) {
          try {
            noiseSuppressor = NoiseSuppressor.create(sessionId)?.apply { enabled = true }
          } catch (e: Exception) {
            Log.e(TAG, "Failed to enable NoiseSuppressor", e)
          }
        }

        if (AcousticEchoCanceler.isAvailable()) {
          try {
            echoCanceler = AcousticEchoCanceler.create(sessionId)?.apply { enabled = true }
          } catch (e: Exception) {
            Log.e(TAG, "Failed to enable AcousticEchoCanceler", e)
          }
        }
      }

      audioRecord?.startRecording()

      recordJob = scope.launch(Dispatchers.IO) {
        val buffer = ByteArray(CHUNK_SIZE_BYTES)
        while (isActive && isRecording.get()) {
          val readBytes = audioRecord?.read(buffer, 0, buffer.size) ?: -1
          if (readBytes > 0) {
            val pcmData = buffer.copyOf(readBytes)
            val rms = AudioUtils.calculateRmsLevel(pcmData)
            onAudioChunk(pcmData, rms)
          } else if (readBytes < 0) {
            break
          }
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Failed to initialize AudioRecord", e)
      stop()
    }
  }

  fun stop() {
    if (!isRecording.getAndSet(false)) return

    recordJob?.cancel()
    recordJob = null

    try {
      noiseSuppressor?.release()
      noiseSuppressor = null
      echoCanceler?.release()
      echoCanceler = null

      audioRecord?.apply {
        if (state == AudioRecord.STATE_INITIALIZED) {
          stop()
        }
        release()
      }
      audioRecord = null
    } catch (e: Exception) {
      Log.e(TAG, "Error stopping AudioRecord", e)
    }
  }
}
