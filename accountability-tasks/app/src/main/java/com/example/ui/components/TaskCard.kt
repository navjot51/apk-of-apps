package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Priority
import com.example.data.TaskItem
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonRed
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun TaskCard(
  task: TaskItem,
  onToggleComplete: (TaskItem) -> Unit,
  onEdit: (TaskItem) -> Unit,
  onDelete: (TaskItem) -> Unit,
  onTriggerPenalty: (TaskItem) -> Unit,
  modifier: Modifier = Modifier
) {
  val now = System.currentTimeMillis()
  val isOverdue = !task.isCompleted && task.dueDateTime < now
  val isDueToday = !task.isCompleted && isDateToday(task.dueDateTime)

  val borderColor by animateColorAsState(
    targetValue = when {
      task.isCompleted -> NeonEmerald.copy(alpha = 0.4f)
      isOverdue -> NeonRed.copy(alpha = 0.7f)
      isDueToday -> NeonAmber.copy(alpha = 0.5f)
      else -> Slate700
    },
    label = "card_border"
  )

  val cardBg = when {
    task.isCompleted -> Slate900.copy(alpha = 0.6f)
    isOverdue -> Color(0xFF1E1015)
    task.isDuplicatedByPenalty -> Color(0xFF1D1429)
    else -> Slate800
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .border(1.dp, borderColor, RoundedCornerShape(16.dp))
      .testTag("task_card_${task.id}"),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Top status badges row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Priority badge
          val priorityColor = when (task.priority) {
            Priority.LOW -> NeonCyan
            Priority.MEDIUM -> NeonAmber
            Priority.HIGH -> NeonRed
          }
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(priorityColor.copy(alpha = 0.15f))
              .border(1.dp, priorityColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 3.dp)
          ) {
            Text(
              text = task.priority.name,
              color = priorityColor,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
          }

          if (task.isDuplicatedByPenalty) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF8B5CF6).copy(alpha = 0.25f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = "⚡ DOUBLE WORK",
                color = Color(0xFFC4B5FD),
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
              )
            }
          }

          if (isOverdue) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(NeonRed.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = "OVERDUE",
                color = NeonRed,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
              )
            }
          }
        }

        // Action icons
        Row {
          IconButton(
            onClick = { onEdit(task) },
            modifier = Modifier.size(32.dp).testTag("edit_task_${task.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit task",
              tint = Slate400,
              modifier = Modifier.size(16.dp)
            )
          }
          IconButton(
            onClick = { onDelete(task) },
            modifier = Modifier.size(32.dp).testTag("delete_task_${task.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete task",
              tint = Slate400,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Main content row with Checkbox
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Custom Checkbox
        Box(
          modifier = Modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(if (task.isCompleted) NeonEmerald else Color.Transparent)
            .border(
              width = 2.dp,
              color = if (task.isCompleted) NeonEmerald else Slate700,
              shape = CircleShape
            )
            .clickable { onToggleComplete(task) }
            .testTag("toggle_complete_${task.id}"),
          contentAlignment = Alignment.Center
        ) {
          if (task.isCompleted) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = "Completed",
              tint = Slate900,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = task.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (task.isCompleted) Slate400 else Color.White,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )

          if (task.description.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = task.description,
              fontSize = 13.sp,
              color = Slate400,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Bottom Row: Due date info & Penalty Trigger test button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AccessTime,
            contentDescription = null,
            tint = if (isOverdue) NeonRed else Slate400,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = formatDueDateTime(task.dueDateTime),
            fontSize = 12.sp,
            color = if (isOverdue) NeonRed else Slate400,
            fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
          )
        }

        if (!task.isCompleted) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(NeonRed.copy(alpha = 0.12f))
              .clickable { onTriggerPenalty(task) }
              .padding(horizontal = 8.dp, vertical = 4.dp)
              .testTag("trigger_penalty_btn_${task.id}"),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = NeonRed,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Trigger Penalty",
                fontSize = 11.sp,
                color = NeonRed,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}

private fun isDateToday(timestamp: Long): Boolean {
  val date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
  return date == LocalDate.now()
}

fun formatDueDateTime(timestamp: Long): String {
  val zonedDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault())
  val date = zonedDateTime.toLocalDate()
  val today = LocalDate.now()
  val tomorrow = today.plusDays(1)
  val yesterday = today.minusDays(1)

  val timeStr = zonedDateTime.format(DateTimeFormatter.ofPattern("h:mm a"))
  return when (date) {
    today -> "Today at $timeStr"
    tomorrow -> "Tomorrow at $timeStr"
    yesterday -> "Yesterday at $timeStr"
    else -> "${date.format(DateTimeFormatter.ofPattern("MMM d"))} at $timeStr"
  }
}
