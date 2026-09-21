package com.example.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
  val allTasks: Flow<List<TaskItem>> = taskDao.getAllTasks()
  val allPunishmentLogs: Flow<List<PunishmentLog>> = taskDao.getAllPunishmentLogs()

  suspend fun getTaskById(id: Long): TaskItem? = taskDao.getTaskById(id)

  suspend fun insertTask(task: TaskItem): Long = taskDao.insertTask(task)

  suspend fun updateTask(task: TaskItem) = taskDao.updateTask(task)

  suspend fun deleteTask(task: TaskItem) = taskDao.deleteTask(task)

  suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

  suspend fun insertPunishmentLog(log: PunishmentLog): Long = taskDao.insertPunishmentLog(log)

  suspend fun updatePunishmentLog(log: PunishmentLog) = taskDao.updatePunishmentLog(log)

  suspend fun clearPunishmentLogs() = taskDao.clearPunishmentLogs()
}
