package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TaskItem
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonRed
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@Composable
fun UrgentReminderBanner(
  urgentTasks: List<TaskItem>,
  onCompleteTask: (TaskItem) -> Unit,
  modifier: Modifier = Modifier
) {
  AnimatedVisibility(
    visible = urgentTasks.isNotEmpty(),
    enter = expandVertically(),
    exit = shrinkVertically()
  ) {
    val mostUrgent = urgentTasks.firstOrNull() ?: return@AnimatedVisibility

    val infiniteTransition = rememberInfiniteTransition(label = "urgent_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
      initialValue = 0.4f,
      targetValue = 0.95f,
      animationSpec = infiniteRepeatable(
        animation = tween(900, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      ),
      label = "pulse_alpha"
    )

    val now = System.currentTimeMillis()
    val diffMillis = mostUrgent.dueDateTime - now
    val minutesLeft = (diffMillis / 60_000).coerceAtLeast(1)

    Box(
      modifier = modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(
          Brush.horizontalGradient(
            listOf(
              Slate900,
              Color(0xFF2E1A05)
            )
          )
        )
        .border(
          width = 1.5.dp,
          color = NeonAmber.copy(alpha = pulseAlpha),
          shape = RoundedCornerShape(14.dp)
        )
        .padding(14.dp)
        .testTag("urgent_reminder_banner")
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(NeonAmber.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = "Urgent alert",
              tint = NeonAmber,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "DUE IN ${minutesLeft}M",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = NeonAmber,
                letterSpacing = 1.sp
              )
              if (urgentTasks.size > 1) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "(+${urgentTasks.size - 1} more)",
                  fontSize = 11.sp,
                  color = Color.White.copy(alpha = 0.6f)
                )
              }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = mostUrgent.title,
              color = Color.White,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Button(
          onClick = { onCompleteTask(mostUrgent) },
          colors = ButtonDefaults.buttonColors(
            containerColor = NeonEmerald,
            contentColor = Slate950
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 10.dp,
            vertical = 6.dp
          ),
          modifier = Modifier.testTag("urgent_complete_btn")
        ) {
          Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text("Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
