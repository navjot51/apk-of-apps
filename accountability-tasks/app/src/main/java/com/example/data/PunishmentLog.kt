package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PunishmentType(val title: String, val description: String) {
  DOUBLE_WORK(
    "Double Work Mode",
    "Task duplicated! Strict 20-minute countdown to finish both, or face crimson flashing and alarm!"
  ),
  SCREEN_FREEZE(
    "Screen Freeze & Focus Lock",
    "Dashboard locked! Sit still and focus on a 5-minute mindfulness breathing loop."
  ),
  APOLOGY_DRAFT(
    "The Apology Draft",
    "Type out a humiliating apology letter character-for-character to unlock the app."
  ),
  FITNESS_CHALLENGE(
    "Wall Sit / Squat Challenge",
    "Complete a physical exercise challenge before earning your workspace back."
  )
}

@Entity(tableName = "punishment_logs")
data class PunishmentLog(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val taskId: Long,
  val taskTitle: String,
  val punishmentType: PunishmentType,
  val timestamp: Long = System.currentTimeMillis(),
  val isCompleted: Boolean = false,
  val notes: String = ""
)
