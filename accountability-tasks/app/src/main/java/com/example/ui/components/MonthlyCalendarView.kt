package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DriveFileMove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Priority
import com.example.data.TaskItem
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigoLight
import com.example.ui.theme.NeonRed
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@Composable
fun MonthlyCalendarView(
  currentMonth: YearMonth,
  selectedDate: LocalDate,
  tasks: List<TaskItem>,
  onDateSelected: (LocalDate) -> Unit,
  onPrevMonth: () -> Unit,
  onNextMonth: () -> Unit,
  onTodayMonth: () -> Unit,
  onAddTaskOnDate: (LocalDate) -> Unit,
  onRescheduleTask: (TaskItem, LocalDate) -> Unit,
  onToggleComplete: (TaskItem) -> Unit,
  onEditTask: (TaskItem) -> Unit,
  onDeleteTask: (TaskItem) -> Unit,
  onTriggerPenalty: (TaskItem) -> Unit,
  modifier: Modifier = Modifier
) {
  var taskToReschedule by remember { mutableStateOf<TaskItem?>(null) }

  // Group tasks by LocalDate
  val tasksByDate = remember(tasks) {
    tasks.groupBy { task ->
      Instant.ofEpochMilli(task.dueDateTime).atZone(ZoneId.systemDefault()).toLocalDate()
    }
  }

  val selectedDateTasks = tasksByDate[selectedDate] ?: emptyList()

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("monthly_calendar_view")
  ) {
    // Header with Month Name and Prev/Next/Today Controls
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          color = Color.White
        )
        Text(
          text = "Tap any date to schedule or inspect tasks",
          fontSize = 12.sp,
          color = Slate400
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onPrevMonth,
          modifier = Modifier.size(36.dp).testTag("prev_month_btn")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Previous Month",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Slate800)
            .clickable { onTodayMonth() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("today_month_btn"),
          contentAlignment = Alignment.Center
        ) {
          Text("Today", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NeonIndigoLight)
        }

        IconButton(
          onClick = onNextMonth,
          modifier = Modifier.size(36.dp).testTag("next_month_btn")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Next Month",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // Days of Week Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceAround
    ) {
      listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT").forEach { day ->
        Text(
          text = day,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = Slate400,
          textAlign = TextAlign.Center,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Calendar Grid
    val firstDayOfMonth = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()
    // Find Sunday of week for first day
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 for Sunday
    val totalCells = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7

    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 4.dp),
      colors = CardDefaults.cardColors(containerColor = Slate900),
      shape = RoundedCornerShape(16.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
    ) {
      Column(modifier = Modifier.padding(6.dp)) {
        var dayCounter = 1 - firstDayOfWeek

        for (row in 0 until (totalCells / 7)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            for (col in 0 until 7) {
              val currentDay = dayCounter
              dayCounter++

              if (currentDay in 1..daysInMonth) {
                val date = currentMonth.atDay(currentDay)
                val isSelected = date == selectedDate
                val isToday = date == LocalDate.now()
                val dateTasks = tasksByDate[date] ?: emptyList()

                Box(
                  modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(3.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                      when {
                        isSelected -> NeonIndigoLight.copy(alpha = 0.3f)
                        isToday -> Slate800
                        else -> Color.Transparent
                      }
                    )
                    .border(
                      width = if (isSelected) 1.5.dp else if (isToday) 1.dp else 0.dp,
                      color = if (isSelected) NeonIndigoLight else if (isToday) NeonAmber else Color.Transparent,
                      shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onDateSelected(date) }
                    .testTag("calendar_day_${date.dayOfMonth}"),
                  contentAlignment = Alignment.Center
                ) {
                  Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                  ) {
                    Text(
                      text = currentDay.toString(),
                      fontSize = 13.sp,
                      fontWeight = if (isSelected || isToday) FontWeight.Black else FontWeight.Normal,
                      color = when {
                        isSelected -> Color.White
                        isToday -> NeonAmber
                        else -> Slate400
                      }
                    )

                    // Task indicator dots
                    if (dateTasks.isNotEmpty()) {
                      Spacer(modifier = Modifier.height(2.dp))
                      Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        val hasOverdue = dateTasks.any { !it.isCompleted && it.dueDateTime < System.currentTimeMillis() }
                        val allCompleted = dateTasks.all { it.isCompleted }

                        val dotColor = when {
                          hasOverdue -> NeonRed
                          allCompleted -> NeonEmerald
                          dateTasks.any { it.priority == Priority.HIGH } -> NeonRed
                          dateTasks.any { it.priority == Priority.MEDIUM } -> NeonAmber
                          else -> NeonCyan
                        }

                        Box(
                          modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                        )
                      }
                    }
                  }
                }
              } else {
                // Empty cell for padding
                Box(
                  modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .padding(3.dp)
                )
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Selected Date Details Section
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.CalendarMonth,
          contentDescription = null,
          tint = NeonIndigoLight,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = selectedDate.format(DateTimeFormatter.ofPattern("EEE, MMMM d")),
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "(${selectedDateTasks.size} tasks)",
          fontSize = 13.sp,
          color = Slate400
        )
      }

      Button(
        onClick = { onAddTaskOnDate(selectedDate) },
        colors = ButtonDefaults.buttonColors(
          containerColor = NeonIndigoLight,
          contentColor = Slate950
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
        modifier = Modifier.testTag("add_task_on_date_btn")
      ) {
        Icon(
          imageVector = Icons.Default.Add,
          contentDescription = null,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text("Schedule", fontSize = 12.sp, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Task list for selected date
    if (selectedDateTasks.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(16.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = Slate700,
            modifier = Modifier.size(48.dp)
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "No tasks scheduled for this day",
            color = Slate400,
            fontSize = 14.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Tap 'Schedule' above to add a task.",
            color = Slate700,
            fontSize = 12.sp
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
      ) {
        items(selectedDateTasks, key = { it.id }) { task ->
          Column {
            TaskCard(
              task = task,
              onToggleComplete = onToggleComplete,
              onEdit = onEditTask,
              onDelete = onDeleteTask,
              onTriggerPenalty = onTriggerPenalty
            )

            // Reschedule affordance button under card
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 2.dp),
              horizontalArrangement = Arrangement.End
            ) {
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Slate800)
                  .clickable { taskToReschedule = task }
                  .padding(horizontal = 8.dp, vertical = 4.dp)
                  .testTag("reschedule_task_${task.id}"),
                contentAlignment = Alignment.Center
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.DriveFileMove,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(13.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Reschedule",
                    fontSize = 11.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  // Quick Reschedule Dialog
  taskToReschedule?.let { task ->
    RescheduleDialog(
      task = task,
      currentDate = selectedDate,
      onDismiss = { taskToReschedule = null },
      onConfirm = { newDate ->
        onRescheduleTask(task, newDate)
        taskToReschedule = null
      }
    )
  }
}

@Composable
fun RescheduleDialog(
  task: TaskItem,
  currentDate: LocalDate,
  onDismiss: () -> Unit,
  onConfirm: (LocalDate) -> Unit
) {
  var pickedDate by remember { mutableStateOf(currentDate) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Slate900,
    shape = RoundedCornerShape(16.dp),
    title = {
      Text(
        text = "Reschedule Task",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
    },
    text = {
      Column {
        Text(
          text = task.title,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          color = NeonIndigoLight
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = "Select New Target Date:",
          fontSize = 12.sp,
          color = Slate400
        )
        Spacer(modifier = Modifier.height(8.dp))

        val options = listOf(
          Pair("Today", LocalDate.now()),
          Pair("Tomorrow", LocalDate.now().plusDays(1)),
          Pair("Next Monday", LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY))),
          Pair("+1 Week", LocalDate.now().plusWeeks(1))
        )

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          options.forEach { (label, date) ->
            val isSelected = pickedDate == date
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else Slate800)
                .border(
                  width = 1.dp,
                  color = if (isSelected) NeonCyan else Slate700,
                  shape = RoundedCornerShape(8.dp)
                )
                .clickable { pickedDate = date }
                .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = label,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isSelected) Color.White else Slate400
                )
                Text(
                  text = date.format(DateTimeFormatter.ofPattern("MMM d")),
                  fontSize = 12.sp,
                  color = if (isSelected) NeonCyan else Slate400
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onConfirm(pickedDate) },
        colors = ButtonDefaults.buttonColors(
          containerColor = NeonCyan,
          contentColor = Slate950
        ),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("Move Task", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = Slate400)
      }
    }
  )
}
