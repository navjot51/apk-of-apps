package com.example.ui.punishment

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@Composable
fun ScreenFreezeView(
  secondsRemaining: Int,
  breathingPhase: String,
  onFastForward: (Int) -> Unit,
  onComplete: () -> Unit
) {
  val minutes = secondsRemaining / 60
  val seconds = secondsRemaining % 60
  val timeFormatted = String.format("%02d:%02d", minutes, seconds)

  // Calming breathing scale animation
  val infiniteTransition = rememberInfiniteTransition(label = "breathing")
  val breathScale by infiniteTransition.animateFloat(
    initialValue = 0.75f,
    targetValue = 1.25f,
    animationSpec = infiniteRepeatable(
      animation = tween(4000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "breath_scale"
  )

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate950)
      .padding(20.dp)
      .testTag("screen_freeze_overlay")
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Status
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 24.dp)
      ) {
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(NeonCyan.copy(alpha = 0.15f))
            .border(1.dp, NeonCyan, RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Lock,
              contentDescription = null,
              tint = NeonCyan,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "SCREEN FREEZE & FOCUS LOCK",
              color = NeonCyan,
              fontWeight = FontWeight.Black,
              fontSize = 12.sp,
              letterSpacing = 1.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
          text = "You Failed Your Deadline.",
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          color = Color.White
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "To unlock your app, you must sit still and focus on this 5-minute mindfulness breathing timer right now.",
          fontSize = 13.sp,
          color = Slate400,
          textAlign = TextAlign.Center,
          modifier = Modifier.padding(horizontal = 16.dp)
        )
      }

      // Middle: Breathing Circle Animation
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
          modifier = Modifier
            .size(200.dp)
            .scale(breathScale),
          contentAlignment = Alignment.Center
        ) {
          // Outer ripple
          Box(
            modifier = Modifier
              .fillMaxSize()
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  listOf(
                    NeonCyan.copy(alpha = 0.35f),
                    NeonIndigo.copy(alpha = 0.15f),
                    Color.Transparent
                  )
                )
              )
          )

          // Inner solid circle
          Box(
            modifier = Modifier
              .size(130.dp)
              .clip(CircleShape)
              .background(
                Brush.linearGradient(
                  listOf(NeonCyan, NeonIndigo)
                )
              )
              .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.SelfImprovement,
              contentDescription = null,
              tint = Slate950,
              modifier = Modifier.size(54.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
          text = breathingPhase.uppercase(),
          fontSize = 18.sp,
          fontWeight = FontWeight.Black,
          color = NeonCyan,
          letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = timeFormatted,
          fontSize = 42.sp,
          fontWeight = FontWeight.Black,
          fontFamily = FontFamily.Monospace,
          color = Color.White
        )
      }

      // Bottom: Controls & Demo Skip
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        if (secondsRemaining <= 0) {
          Button(
            onClick = onComplete,
            colors = ButtonDefaults.buttonColors(
              containerColor = NeonEmerald,
              contentColor = Slate950
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("unlock_screen_freeze_btn")
          ) {
            Text("Focus Session Finished - Unlock Dashboard", fontWeight = FontWeight.Black)
          }
        } else {
          Button(
            onClick = { onFastForward(3) },
            colors = ButtonDefaults.buttonColors(
              containerColor = Slate800,
              contentColor = NeonCyan
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.testTag("fast_forward_freeze_btn")
          ) {
            Icon(
              imageVector = Icons.Default.FastForward,
              contentDescription = null,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("Quick Demo (3s remaining)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
