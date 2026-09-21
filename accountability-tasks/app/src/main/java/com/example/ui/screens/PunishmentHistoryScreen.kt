package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PunishmentLog
import com.example.data.PunishmentType
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldLight
import com.example.ui.theme.NeonIndigoLight
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun PunishmentHistoryScreen(
  punishmentLogs: List<PunishmentLog>,
  onTriggerSimulation: () -> Unit,
  onClearLogs: () -> Unit,
  modifier: Modifier = Modifier
) {
  val total = punishmentLogs.size
  val completed = punishmentLogs.count { it.isCompleted }
  val successRate = if (total > 0) ((completed.toFloat() / total) * 100).toInt() else 100

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("punishment_history_screen")
  ) {
    // Header & Actions
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "Accountability Ledger",
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          color = Color.White
        )
        Text(
          text = "Hall of shame & penalty enforcement logs",
          fontSize = 12.sp,
          color = Slate400
        )
      }

      Row {
        if (punishmentLogs.isNotEmpty()) {
          IconButton(
            onClick = onClearLogs,
            modifier = Modifier.testTag("clear_logs_btn")
          ) {
            Icon(
              imageVector = Icons.Default.ClearAll,
              contentDescription = "Clear logs",
              tint = Slate400
            )
          }
        }
      }
    }

    // Stats Grid
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      StatCard(
        title = "TOTAL PENALTIES",
        value = total.toString(),
        color = NeonRed,
        modifier = Modifier.weight(1f)
      )
      StatCard(
        title = "SERVED",
        value = completed.toString(),
        color = NeonEmerald,
        modifier = Modifier.weight(1f)
      )
      StatCard(
        title = "REDEMPTION RATE",
        value = "$successRate%",
        color = NeonIndigoLight,
        modifier = Modifier.weight(1f)
      )
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Simulation Trigger Banner
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp),
      colors = CardDefaults.cardColors(containerColor = Slate900),
      shape = RoundedCornerShape(14.dp),
      border = androidx.compose.foundation.BorderStroke(1.dp, NeonRed.copy(alpha = 0.4f))
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(NeonRed.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = NeonRed,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Test Penalty Engine",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = Color.White
            )
            Text(
              text = "Simulate a missed deadline now",
              fontSize = 11.sp,
              color = Slate400
            )
          }
        }

        Button(
          onClick = onTriggerSimulation,
          colors = ButtonDefaults.buttonColors(
            containerColor = NeonRed,
            contentColor = Slate950
          ),
          shape = RoundedCornerShape(8.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
          modifier = Modifier.testTag("simulate_penalty_btn")
        ) {
          Text("Simulate", fontSize = 12.sp, fontWeight = FontWeight.Black)
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Log list
    if (punishmentLogs.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = NeonEmerald.copy(alpha = 0.5f),
            modifier = Modifier.size(54.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Clean Accountability Record!",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "No overdue deadlines penalized yet. Keep up the high productivity!",
            fontSize = 12.sp,
            color = Slate400,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(punishmentLogs, key = { it.id }) { log ->
          PunishmentLogItem(log = log)
        }
      }
    }
  }
}

@Composable
private fun StatCard(
  title: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    colors = CardDefaults.cardColors(containerColor = Slate900),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = title,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = Slate400,
        letterSpacing = 0.5.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        fontSize = 18.sp,
        fontWeight = FontWeight.Black,
        color = color
      )
    }
  }
}

@Composable
private fun PunishmentLogItem(log: PunishmentLog) {
  val (icon, iconColor) = when (log.punishmentType) {
    PunishmentType.DOUBLE_WORK -> Icons.Default.ContentCopy to NeonPurple
    PunishmentType.SCREEN_FREEZE -> Icons.Default.LockClock to NeonCyan
    PunishmentType.APOLOGY_DRAFT -> Icons.Default.RecordVoiceOver to NeonAmber
    PunishmentType.FITNESS_CHALLENGE -> Icons.Default.FitnessCenter to NeonEmerald
  }

  val dateStr = Instant.ofEpochMilli(log.timestamp)
    .atZone(ZoneId.systemDefault())
    .format(DateTimeFormatter.ofPattern("MMM d, h:mm a"))

  Card(
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = Slate900),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(iconColor.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconColor,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = log.punishmentType.title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
          Text(
            text = dateStr,
            fontSize = 11.sp,
            color = Slate400
          )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = "Task: ${log.taskTitle}",
          fontSize = 12.sp,
          color = Slate400,
          maxLines = 1,
          overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )

        if (log.notes.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Resolution: ${log.notes}",
            fontSize = 11.sp,
            color = NeonEmeraldLight,
            maxLines = 1
          )
        }
      }

      Spacer(modifier = Modifier.width(8.dp))

      Icon(
        imageVector = if (log.isCompleted) Icons.Default.CheckCircle else Icons.Default.HourglassEmpty,
        contentDescription = null,
        tint = if (log.isCompleted) NeonEmerald else NeonRed,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}
