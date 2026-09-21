package com.example.ui.punishment

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
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.PunishmentType
import com.example.data.TaskItem
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@Composable
fun ChoosePunishmentDialog(
  failedTask: TaskItem,
  onSelectType: (PunishmentType) -> Unit,
  onRandomAssign: () -> Unit
) {
  Dialog(
    onDismissRequest = { /* Dashboard locked - cannot dismiss without punishment! */ },
    properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .border(2.dp, NeonRed, RoundedCornerShape(24.dp))
        .testTag("choose_punishment_dialog"),
      colors = CardDefaults.cardColors(containerColor = Slate900),
      shape = RoundedCornerShape(24.dp)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Warning Icon & Header
        Box(
          modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(NeonRed.copy(alpha = 0.2f))
            .border(2.dp, NeonRed, CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = NeonRed,
            modifier = Modifier.size(30.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "DEADLINE FAILED!",
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          color = NeonRed,
          letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = "\"${failedTask.title}\"",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Your dashboard is locked. Strict accountability requires facing a penalty. Choose your punishment to proceed:",
          fontSize = 12.sp,
          color = Slate400,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Punishment Options
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          PunishmentOptionCard(
            title = "Double Work Mode",
            subtitle = "Task is duplicated! 20-min countdown to finish both, or alarm strikes!",
            icon = Icons.Default.ContentCopy,
            iconColor = NeonPurple,
            tag = "punishment_double_work_btn",
            onClick = { onSelectType(PunishmentType.DOUBLE_WORK) }
          )

          PunishmentOptionCard(
            title = "Screen Freeze & Focus Lock",
            subtitle = "Dashboard freezes for a mandatory 5-minute mindfulness breathing loop.",
            icon = Icons.Default.LockClock,
            iconColor = NeonCyan,
            tag = "punishment_screen_freeze_btn",
            onClick = { onSelectType(PunishmentType.SCREEN_FREEZE) }
          )

          PunishmentOptionCard(
            title = "The Apology Draft",
            subtitle = "Type out an embarrassing apology letter character-for-character.",
            icon = Icons.Default.RecordVoiceOver,
            iconColor = NeonAmber,
            tag = "punishment_apology_draft_btn",
            onClick = { onSelectType(PunishmentType.APOLOGY_DRAFT) }
          )

          PunishmentOptionCard(
            title = "Wall Sit / Squat Challenge",
            subtitle = "Complete 60s of wall sits or 20 squats to unlock your workspace.",
            icon = Icons.Default.FitnessCenter,
            iconColor = NeonEmerald,
            tag = "punishment_fitness_btn",
            onClick = { onSelectType(PunishmentType.FITNESS_CHALLENGE) }
          )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Assign Random Punishment Button
        Button(
          onClick = onRandomAssign,
          colors = ButtonDefaults.buttonColors(
            containerColor = NeonRed,
            contentColor = Slate950
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("random_punishment_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Casino,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Assign Punishment Randomly",
            fontWeight = FontWeight.Black,
            fontSize = 14.sp
          )
        }
      }
    }
  }
}

@Composable
private fun PunishmentOptionCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  iconColor: Color,
  tag: String,
  onClick: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(Slate800)
      .border(1.dp, Slate700, RoundedCornerShape(14.dp))
      .clickable { onClick() }
      .padding(12.dp)
      .testTag(tag)
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(40.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(iconColor.copy(alpha = 0.2f)),
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
        Text(
          text = title,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = subtitle,
          fontSize = 11.sp,
          color = Slate400,
          lineHeight = 14.sp
        )
      }
    }
  }
}
