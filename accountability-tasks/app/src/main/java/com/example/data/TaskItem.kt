package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class Priority {
  LOW,
  MEDIUM,
  HIGH
}

@Entity(tableName = "tasks")
data class TaskItem(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val description: String = "",
  val dueDateTime: Long, // epoch millis
  val priority: Priority = Priority.MEDIUM,
  val isCompleted: Boolean = false,
  val completedAt: Long? = null,
  val penaltyTriggered: Boolean = false,
  val isDuplicatedByPenalty: Boolean = false
)
