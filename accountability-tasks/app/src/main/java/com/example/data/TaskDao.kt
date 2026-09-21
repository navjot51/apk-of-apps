package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
  @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, dueDateTime ASC")
  fun getAllTasks(): Flow<List<TaskItem>>

  @Query("SELECT * FROM tasks WHERE id = :id")
  suspend fun getTaskById(id: Long): TaskItem?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskItem): Long

  @Update
  suspend fun updateTask(task: TaskItem)

  @Delete
  suspend fun deleteTask(task: TaskItem)

  @Query("DELETE FROM tasks WHERE id = :id")
  suspend fun deleteTaskById(id: Long)

  @Query("SELECT * FROM punishment_logs ORDER BY timestamp DESC")
  fun getAllPunishmentLogs(): Flow<List<PunishmentLog>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPunishmentLog(log: PunishmentLog): Long

  @Update
  suspend fun updatePunishmentLog(log: PunishmentLog)

  @Query("DELETE FROM punishment_logs")
  suspend fun clearPunishmentLogs()
}
