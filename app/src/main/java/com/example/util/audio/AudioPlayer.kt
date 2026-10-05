package com.example.util.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Plays raw PCM 16-bit mono audio chunks at 24,000 Hz returned by the Gemini Multimodal Live API in real-time.
 */
class AudioPlayer {

  companion object {
    private const val TAG = "AudioPlayer"
    const val SAMPLE_RATE = 24000
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
  }

  private var audioTrack: AudioTrack? = null
  private val isPlaying = AtomicBoolean(false)
  private val audioChannel = Channel<ByteArray>(64) // Bounded channel to prevent buffer bloat
  private var playJob: Job? = null

  fun start(scope: CoroutineScope, onAudioLevelChanged: (Float) -> Unit = {}) {
    if (isPlaying.getAndSet(true)) return

    val minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
    val bufferSize = maxOf(minBufferSize, 8192)

    val attributes = AudioAttributes.Builder()
      .setUsage(AudioAttributes.USAGE_MEDIA)
      .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
      .build()

    val format = AudioFormat.Builder()
      .setSampleRate(SAMPLE_RATE)
      .setChannelMask(CHANNEL_CONFIG)
      .setEncoding(AUDIO_FORMAT)
      .build()

    try {
      audioTrack = AudioTrack.Builder()
        .setAudioAttributes(attributes)
        .setAudioFormat(format)
        .setBufferSizeInBytes(bufferSize)
        .setTransferMode(AudioTrack.MODE_STREAM)
        .build()

      audioTrack?.play()

      playJob = scope.launch(Dispatchers.IO) {
        for (pcmData in audioChannel) {
          if (!isActive || !isPlaying.get()) break
          val track = audioTrack ?: break

          if (track.playState != AudioTrack.PLAYSTATE_PLAYING) {
            track.play()
          }

          val rms = AudioUtils.calculateRmsLevel(pcmData)
          onAudioLevelChanged(rms)

          var bytesWritten = 0
          while (bytesWritten < pcmData.size && isActive) {
            val written = track.write(pcmData, bytesWritten, pcmData.size - bytesWritten)
            if (written > 0) {
              bytesWritten += written
            } else {
              break
            }
          }
        }
        onAudioLevelChanged(0f)
      }
    } catch (e: Exception) {
      Log.e(TAG, "Failed to initialize AudioTrack", e)
      stop()
    }
  }

  fun playChunk(pcmBytes: ByteArray) {
    if (!isPlaying.get()) return
    audioChannel.trySend(pcmBytes)
  }

  fun flush() {
    try {
      audioTrack?.pause()
      audioTrack?.flush()
      while (audioChannel.tryReceive().isSuccess) { /* drain */ }
      audioTrack?.play()
    } catch (e: Exception) {
      Log.e(TAG, "Error flushing AudioTrack", e)
    }
  }

  fun stop() {
    if (!isPlaying.getAndSet(false)) return

    playJob?.cancel()
    playJob = null

    try {
      audioTrack?.apply {
        if (playState == AudioTrack.PLAYSTATE_PLAYING) {
          stop()
        }
        release()
      }
      audioTrack = null
      while (audioChannel.tryReceive().isSuccess) { /* drain */ }
    } catch (e: Exception) {
      Log.e(TAG, "Error stopping AudioTrack", e)
    }
  }
}
