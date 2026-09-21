package com.example.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundEffectsHelper(private val context: Context) {
  private val scope = CoroutineScope(Dispatchers.Default)

  private val vibrator: Vibrator? by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
      vibratorManager?.defaultVibrator
    } else {
      @Suppress("DEPRECATION")
      context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
  }

  fun playDing() {
    vibratePattern(longArrayOf(0, 40), intArrayOf(0, 180))
    scope.launch {
      playToneSequence(
        listOf(
          Tone(880.0, 120, 0.6),
          Tone(1320.0, 200, 0.7)
        )
      )
    }
  }

  fun playBuzzer() {
    vibratePattern(longArrayOf(0, 120, 60, 150), intArrayOf(0, 255, 0, 255))
    scope.launch {
      playBuzzerTone(140.0, 380, 0.85)
    }
  }

  fun playAlarmPulse() {
    vibratePattern(longArrayOf(0, 80, 40, 80), intArrayOf(0, 220, 0, 220))
    scope.launch {
      playToneSequence(
        listOf(
          Tone(900.0, 100, 0.8),
          Tone(650.0, 100, 0.8)
        )
      )
    }
  }

  private fun vibratePattern(timings: LongArray, amplitudes: IntArray) {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
      } else {
        @Suppress("DEPRECATION")
        vibrator?.vibrate(timings, -1)
      }
    } catch (_: Exception) {
      // Ignore vibration exceptions on devices without vibrator hardware
    }
  }

  private data class Tone(val freq: Double, val durationMs: Int, val maxVolume: Double)

  private fun playToneSequence(tones: List<Tone>) {
    try {
      val sampleRate = 22050
      var totalSamples = 0
      tones.forEach { totalSamples += (sampleRate * it.durationMs) / 1000 }

      val buffer = ShortArray(totalSamples)
      var currentIdx = 0

      for (tone in tones) {
        val numSamples = (sampleRate * tone.durationMs) / 1000
        for (i in 0 until numSamples) {
          val t = i.toDouble() / sampleRate
          val envelope = exp(-3.5 * (i.toDouble() / numSamples))
          val sample = (sin(2.0 * PI * tone.freq * t) * tone.maxVolume * envelope * Short.MAX_VALUE).toInt()
          buffer[currentIdx++] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
      }

      playAudioBuffer(buffer, sampleRate)
    } catch (_: Exception) {
      // Audio playback fallback
    }
  }

  private fun playBuzzerTone(freq: Double, durationMs: Int, volume: Double) {
    try {
      val sampleRate = 22050
      val numSamples = (sampleRate * durationMs) / 1000
      val buffer = ShortArray(numSamples)

      for (i in 0 until numSamples) {
        val t = i.toDouble() / sampleRate
        // Harsh sawtooth-like square wave mix for buzzer feel
        val sine1 = sin(2.0 * PI * freq * t)
        val sine3 = sin(2.0 * PI * (freq * 3) * t) * 0.4
        val sine5 = sin(2.0 * PI * (freq * 5) * t) * 0.2
        val raw = (sine1 + sine3 + sine5) / 1.6
        val sample = (raw * volume * Short.MAX_VALUE).toInt()
        buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
      }

      playAudioBuffer(buffer, sampleRate)
    } catch (_: Exception) {
      // Audio playback fallback
    }
  }

  private fun playAudioBuffer(buffer: ShortArray, sampleRate: Int) {
    var audioTrack: AudioTrack? = null
    try {
      val bufferSize = buffer.size * 2
      audioTrack = AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        )
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()
        )
        .setBufferSizeInBytes(bufferSize)
        .setTransferMode(AudioTrack.MODE_STATIC)
        .build()

      audioTrack.write(buffer, 0, buffer.size)
      audioTrack.play()
      // Let it play out then release
      Thread.sleep((buffer.size * 1000L / sampleRate) + 50L)
    } catch (_: Exception) {
    } finally {
      try {
        audioTrack?.stop()
        audioTrack?.release()
      } catch (_: Exception) {
      }
    }
  }
}
