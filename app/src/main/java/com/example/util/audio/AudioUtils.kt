package com.example.util.audio

import kotlin.math.sqrt

/**
 * Common audio utility functions for PCM calculations.
 */
object AudioUtils {

  /**
   * Calculates normalized RMS (Root Mean Square) volume level (0.0f .. 1.0f)
   * for 16-bit PCM mono audio bytes.
   */
  fun calculateRmsLevel(pcmBytes: ByteArray): Float {
    if (pcmBytes.size < 2) return 0f
    var sum = 0.0
    val sampleCount = pcmBytes.size / 2
    for (i in 0 until sampleCount) {
      val sample = (pcmBytes[i * 2].toInt() and 0xFF) or (pcmBytes[i * 2 + 1].toInt() shl 8)
      val shortSample = sample.toShort()
      sum += shortSample * shortSample
    }
    val rms = sqrt(sum / sampleCount)
    return (rms / 32767.0).toFloat().coerceIn(0f, 1f)
  }
}
