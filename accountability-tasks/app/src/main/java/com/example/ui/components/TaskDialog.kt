package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Priority
import com.example.data.TaskItem
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigoLight
import com.example.ui.theme.NeonRed
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@Composable
fun TaskDialog(
  task: TaskItem?,
  onDismiss: () -> Unit,
  onSave: (title: String, description: String, dueDateTime: Long, priority: Priority) -> Unit
) {
  if (task == null) return

  var title by remember(task) { mutableStateOf(task.title) }
  var description by remember(task) { mutableStateOf(task.description) }
  var priority by remember(task) { mutableStateOf(task.priority) }

  // Initial date/time extraction
  val initialZoned = remember(task) {
    Instant.ofEpochMilli(task.dueDateTime).atZone(ZoneId.systemDefault())
  }
  var selectedDate by remember(task) { mutableStateOf(initialZoned.toLocalDate()) }
  var selectedHour by remember(task) { mutableStateOf(initialZoned.hour) }
  var selectedMinute by remember(task) { mutableStateOf(initialZoned.minute) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Slate900,
    shape = RoundedCornerShape(20.dp),
    modifier = Modifier.testTag("task_edit_dialog"),
    title = {
      Text(
        text = if (task.id == 0L) "Create New Task" else "Edit Task",
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Task Title *") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("task_title_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonIndigoLight,
            unfocusedBorderColor = Slate700,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = NeonIndigoLight,
            unfocusedLabelColor = Slate400
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description (optional)") },
          maxLines = 3,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("task_description_input"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonIndigoLight,
            unfocusedBorderColor = Slate700,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = NeonIndigoLight,
            unfocusedLabelColor = Slate400
          )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Priority Selection
        Text(
          text = "Priority Level",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Slate400
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Priority.values().forEach { p ->
            val isSelected = priority == p
            val color = when (p) {
              Priority.LOW -> NeonCyan
              Priority.MEDIUM -> NeonAmber
              Priority.HIGH -> NeonRed
            }
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) color.copy(alpha = 0.25f) else Slate800)
                .border(
                  width = if (isSelected) 1.5.dp else 1.dp,
                  color = if (isSelected) color else Slate700,
                  shape = RoundedCornerShape(8.dp)
                )
                .clickable { priority = p }
                .padding(vertical = 8.dp)
                .testTag("priority_select_${p.name}"),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = p.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) color else Slate400
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Due Date Selection
        Text(
          text = "Due Date",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Slate400
        )
        Spacer(modifier = Modifier.height(6.dp))
        val today = LocalDate.now()
        val quickDates = listOf(
          Pair("Today", today),
          Pair("Tomorrow", today.plusDays(1)),
          Pair("+2 Days", today.plusDays(2)),
          Pair("+1 Week", today.plusWeeks(1))
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          quickDates.forEach { (label, date) ->
            val isSelected = selectedDate == date
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) NeonIndigoLight.copy(alpha = 0.25f) else Slate800)
                .border(
                  width = if (isSelected) 1.5.dp else 1.dp,
                  color = if (isSelected) NeonIndigoLight else Slate700,
                  shape = RoundedCornerShape(8.dp)
                )
                .clickable { selectedDate = date }
                .padding(vertical = 7.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) NeonIndigoLight else Slate400
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Due Time Selection
        Text(
          text = "Due Time",
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold,
          color = Slate400
        )
        Spacer(modifier = Modifier.height(6.dp))
        val quickTimes = listOf(
          Pair("Morning (9 AM)", 9 to 0),
          Pair("Noon (12 PM)", 12 to 0),
          Pair("EOD (5 PM)", 17 to 0),
          Pair("Night (9 PM)", 21 to 0)
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          quickTimes.forEach { (label, time) ->
            val isSelected = selectedHour == time.first && selectedMinute == time.second
            Box(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) NeonIndigoLight.copy(alpha = 0.25f) else Slate800)
                .border(
                  width = if (isSelected) 1.5.dp else 1.dp,
                  color = if (isSelected) NeonIndigoLight else Slate700,
                  shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                  selectedHour = time.first
                  selectedMinute = time.second
                }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = label.substringBefore(" "),
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) NeonIndigoLight else Slate400
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            val dueEpoch = selectedDate.atTime(LocalTime.of(selectedHour, selectedMinute))
              .atZone(ZoneId.systemDefault())
              .toInstant()
              .toEpochMilli()
            onSave(title, description, dueEpoch, priority)
          }
        },
        enabled = title.isNotBlank(),
        colors = ButtonDefaults.buttonColors(
          containerColor = NeonIndigoLight,
          contentColor = Slate950
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.testTag("save_task_btn")
      ) {
        Text("Save Task", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(
        onClick = onDismiss,
        colors = ButtonDefaults.textButtonColors(contentColor = Slate400)
      ) {
        Text("Cancel")
      }
    }
  )
}
