package com.example.ui.punishment

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigoLight
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@Composable
fun FitnessChallengeView(
  title: String,
  description: String,
  secondsRemaining: Int,
  repsDone: Int,
  repsTarget: Int,
  onIncrementRep: () -> Unit,
  onFastForward: (Int) -> Unit,
  onComplete: () -> Unit
) {
  val minutes = secondsRemaining / 60
  val seconds = secondsRemaining % 60
  val timeFormatted = String.format("%02d:%02d", minutes, seconds)
  val isTimerFinished = secondsRemaining <= 0
  val isRepsFinished = repsDone >= repsTarget
  val canUnlock = isTimerFinished || isRepsFinished

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate950)
      .padding(16.dp)
      .testTag("fitness_challenge_overlay")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(20.dp))

      // Header Tag
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(NeonEmerald.copy(alpha = 0.2f))
          .border(1.dp, NeonEmerald, RoundedCornerShape(20.dp))
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.FitnessCenter,
            contentDescription = null,
            tint = NeonEmerald,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "PHYSICAL PUNISHMENT CHALLENGE",
            color = NeonEmerald,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            letterSpacing = 1.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = title,
        fontSize = 22.sp,
        fontWeight = FontWeight.Black,
        color = Color.White,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = description,
        fontSize = 14.sp,
        color = Slate400,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 12.dp)
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Dual Challenge Cards: Timer OR Rep Counter
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(
            2.dp,
            if (isTimerFinished) NeonEmerald else Slate700,
            RoundedCornerShape(20.dp)
          ),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(20.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Timer,
              contentDescription = null,
              tint = if (isTimerFinished) NeonEmerald else Slate400,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isTimerFinished) "TIMER COMPLETED!" else "WALL SIT TIMER",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = if (isTimerFinished) NeonEmerald else Slate400,
              letterSpacing = 1.sp
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = timeFormatted,
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = if (isTimerFinished) NeonEmerald else Color.White
          )

          if (!isTimerFinished) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
              onClick = { onFastForward(3) },
              colors = ButtonDefaults.buttonColors(
                containerColor = Slate800,
                contentColor = NeonEmerald
              ),
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("fast_forward_fitness_btn")
            ) {
              Icon(imageVector = Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Quick Demo (3s left)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "— OR COMPLETE REPETITIONS —",
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        color = Slate700,
        letterSpacing = 1.sp
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Squats Rep Counter Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(
            2.dp,
            if (isRepsFinished) NeonEmerald else Slate700,
            RoundedCornerShape(20.dp)
          ),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(20.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text(
            text = "BODYWEIGHT SQUAT COUNTER",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Slate400,
            letterSpacing = 1.sp
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "$repsDone / $repsTarget REPS",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = if (isRepsFinished) NeonEmerald else NeonIndigoLight
          )

          Spacer(modifier = Modifier.height(8.dp))

          LinearProgressIndicator(
            progress = { (repsDone.toFloat() / repsTarget).coerceIn(0f, 1f) },
            modifier = Modifier
              .fillMaxWidth(0.8f)
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = NeonEmerald,
            trackColor = Slate800
          )

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = onIncrementRep,
            colors = ButtonDefaults.buttonColors(
              containerColor = NeonIndigoLight,
              contentColor = Slate950
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("rep_increment_btn")
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("+1 Rep Completed", fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Complete & Unlock Button
      Button(
        onClick = onComplete,
        enabled = canUnlock,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (canUnlock) NeonEmerald else Slate800,
          contentColor = if (canUnlock) Slate950 else Slate400
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("complete_fitness_btn")
      ) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (canUnlock) "I Completed My Punishment - Unlock!" else "Wait Out Timer or Finish Reps",
          fontWeight = FontWeight.Black,
          fontSize = 14.sp
        )
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
