package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlin.math.exp
import kotlin.math.sin

object ClickerAudioHelper {
  private const val SAMPLE_RATE = 44100
  private const val DURATION_MS = 15
  private val numSamples = (SAMPLE_RATE * (DURATION_MS / 1000.0)).toInt()
  private val clickBuffer: ShortArray by lazy {
    val buffer = ShortArray(numSamples)
    val freq = 2800.0
    for (i in 0 until numSamples) {
      val t = i.toDouble() / SAMPLE_RATE
      val envelope = exp(-i.toDouble() / (SAMPLE_RATE * 0.003)) // Fast exponential decay
      val sample = (sin(2.0 * Math.PI * freq * t) * envelope * Short.MAX_VALUE * 0.9).toInt()
      buffer[i] = sample.toShort()
    }
    buffer
  }

  fun playClick(context: Context) {
    // 1. Play synthesized mechanical click sound
    try {
      val audioTrack = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build(),
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(SAMPLE_RATE)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build(),
        )
        .setBufferSizeInBytes(clickBuffer.size * 2)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      audioTrack.write(clickBuffer, 0, clickBuffer.size)
      audioTrack.play()
    } catch (_: Exception) {
      // Audio fallback
    }

    // 2. Trigger crisp tactile haptic feedback
    try {
      val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(20L)
      }
    } catch (_: Exception) {
      // Haptic fallback
    }
  }
}
