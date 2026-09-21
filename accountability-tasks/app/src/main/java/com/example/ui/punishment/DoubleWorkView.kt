package com.example.ui.punishment

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.TaskItem
import com.example.ui.theme.CrimsonFlash
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@Composable
fun DoubleWorkView(
  originalTask: TaskItem,
  duplicateTask: TaskItem?,
  secondsRemaining: Int,
  isCrimsonAlarmActive: Boolean,
  onToggleTask: (TaskItem) -> Unit,
  onFastForward: (Int) -> Unit,
  onForceComplete: () -> Unit
) {
  val bgColor by animateColorAsState(
    targetValue = if (isCrimsonAlarmActive) CrimsonFlash else Slate950,
    label = "double_work_bg"
  )

  val minutes = secondsRemaining / 60
  val seconds = secondsRemaining % 60
  val timeFormatted = String.format("%02d:%02d", minutes, seconds)

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(bgColor)
      .padding(16.dp)
      .testTag("double_work_overlay")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(24.dp))

      // Header Tag
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(if (isCrimsonAlarmActive) NeonRed else NeonPurple.copy(alpha = 0.25f))
          .border(
            1.dp,
            if (isCrimsonAlarmActive) Color.White else NeonPurple,
            RoundedCornerShape(20.dp)
          )
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (isCrimsonAlarmActive) Icons.Default.Alarm else Icons.Default.Warning,
            contentDescription = null,
            tint = if (isCrimsonAlarmActive) Slate950 else NeonPurple,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isCrimsonAlarmActive) "ALARM TRIGGERED!" else "PUNISHMENT: DOUBLE WORK MODE",
            color = if (isCrimsonAlarmActive) Slate950 else Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            letterSpacing = 1.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = if (isCrimsonAlarmActive)
          "TIME IS UP! COMPLETE BOTH TO SILENCE ALARM"
        else
          "Complete BOTH Tasks Before Time Runs Out!",
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
        color = if (isCrimsonAlarmActive) Color.White else NeonPurple,
        textAlign = TextAlign.Center
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Your missed task has been duplicated. You must finish both the original task and the duplicate penalty task to unlock your task manager.",
        fontSize = 13.sp,
        color = Slate400,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 16.dp)
      )

      Spacer(modifier = Modifier.height(20.dp))

      // Digital Timer Display
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(
            2.dp,
            if (isCrimsonAlarmActive) NeonRed else NeonAmber,
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
          Text(
            text = "COUNTDOWN TIMER",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Slate400,
            letterSpacing = 1.5.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = timeFormatted,
            fontSize = 48.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Monospace,
            color = if (isCrimsonAlarmActive) NeonRed else NeonAmber
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Fast forward affordance button for testing
          Button(
            onClick = { onFastForward(5) },
            colors = ButtonDefaults.buttonColors(
              containerColor = Slate800,
              contentColor = NeonAmber
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
              horizontal = 12.dp,
              vertical = 6.dp
            ),
            modifier = Modifier.testTag("fast_forward_timer_btn")
          ) {
            Icon(
              imageVector = Icons.Default.FastForward,
              contentDescription = null,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Simulate Expiry (5s left)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Tasks to complete
      Text(
        text = "REQUIRED COMPLETIONS:",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Slate400,
        letterSpacing = 1.sp,
        modifier = Modifier.align(Alignment.Start)
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Original Task Item Card
      DoubleWorkItemCard(
        label = "ORIGINAL TASK",
        task = originalTask,
        onToggle = { onToggleTask(originalTask) }
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Duplicate Task Item Card
      duplicateTask?.let { dup ->
        DoubleWorkItemCard(
          label = "DUPLICATE PENALTY TASK",
          task = dup,
          onToggle = { onToggleTask(dup) }
        )
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Unlock button once completed
      val isReady = originalTask.isCompleted && (duplicateTask == null || duplicateTask.isCompleted)
      Button(
        onClick = onForceComplete,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isReady) NeonEmerald else Slate800,
          contentColor = if (isReady) Slate950 else Slate400
        ),
        enabled = isReady,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("double_work_unlock_btn")
      ) {
        Icon(imageVector = Icons.Default.Check, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isReady) "I Have Completed Both! Unlock App" else "Complete Both Tasks to Unlock",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}

@Composable
private fun DoubleWorkItemCard(
  label: String,
  task: TaskItem,
  onToggle: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(
        1.dp,
        if (task.isCompleted) NeonEmerald else Slate700,
        RoundedCornerShape(14.dp)
      ),
    colors = CardDefaults.cardColors(containerColor = Slate900),
    shape = RoundedCornerShape(14.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(32.dp)
          .clip(CircleShape)
          .background(if (task.isCompleted) NeonEmerald else Slate800)
          .border(
            2.dp,
            if (task.isCompleted) NeonEmerald else Slate700,
            CircleShape
          ),
        contentAlignment = Alignment.Center
      ) {
        if (task.isCompleted) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Slate950,
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = label,
          fontSize = 10.sp,
          fontWeight = FontWeight.Black,
          color = if (task.isCompleted) NeonEmerald else NeonPurple,
          letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = task.title,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }

      Button(
        onClick = onToggle,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (task.isCompleted) Slate800 else NeonEmerald,
          contentColor = if (task.isCompleted) Slate400 else Slate950
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
          horizontal = 10.dp,
          vertical = 6.dp
        )
      ) {
        Text(
          text = if (task.isCompleted) "Mark Incomplete" else "Mark Done",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
