package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

private data class ConfettiParticle(
  val startX: Float,
  val startY: Float,
  val vx: Float,
  val vy: Float,
  val size: Float,
  val color: Color,
  val rotationSpeed: Float,
  val isCircle: Boolean
)

@Composable
fun ConfettiCanvas(
  trigger: Long,
  modifier: Modifier = Modifier
) {
  if (trigger == 0L) return

  val progress = remember(trigger) { Animatable(0f) }

  val particles = remember(trigger) {
    val palette = listOf(
      Color(0xFF10B981), // Emerald
      Color(0xFF34D399),
      Color(0xFF6366F1), // Indigo
      Color(0xFF06B6D4), // Cyan
      Color(0xFFF59E0B), // Amber
      Color(0xFFEC4899), // Pink
      Color(0xFF8B5CF6)  // Purple
    )
    List(80) {
      val angle = Random.nextFloat() * Math.PI.toFloat() + Math.PI.toFloat() // upwards burst
      val speed = Random.nextFloat() * 1200f + 600f
      ConfettiParticle(
        startX = 0.5f,
        startY = 0.6f,
        vx = (Math.cos(angle.toDouble()) * speed).toFloat(),
        vy = (Math.sin(angle.toDouble()) * speed).toFloat(),
        size = Random.nextFloat() * 14f + 8f,
        color = palette[Random.nextInt(palette.size)],
        rotationSpeed = Random.nextFloat() * 720f - 360f,
        isCircle = Random.nextBoolean()
      )
    }
  }

  LaunchedEffect(trigger) {
    progress.snapTo(0f)
    progress.animateTo(
      targetValue = 1f,
      animationSpec = tween(durationMillis = 2200, easing = LinearEasing)
    )
  }

  if (progress.value in 0.001f..0.999f) {
    Canvas(modifier = modifier.fillMaxSize()) {
      val t = progress.value
      val gravity = 1800f * t * t

      particles.forEach { p ->
        val x = size.width * p.startX + p.vx * t
        val y = size.height * p.startY + p.vy * t + 0.5f * gravity
        val alpha = (1f - t).coerceIn(0f, 1f)
        val particleColor = p.color.copy(alpha = alpha)

        if (x in 0f..size.width && y in 0f..size.height) {
          rotate(degrees = p.rotationSpeed * t, pivot = Offset(x, y)) {
            if (p.isCircle) {
              drawCircle(
                color = particleColor,
                radius = p.size / 2f,
                center = Offset(x, y)
              )
            } else {
              drawRect(
                color = particleColor,
                topLeft = Offset(x - p.size / 2f, y - p.size / 3f),
                size = Size(p.size, p.size * 0.7f)
              )
            }
          }
        }
      }
    }
  }
}
