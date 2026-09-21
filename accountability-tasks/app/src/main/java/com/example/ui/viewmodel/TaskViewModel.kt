package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.Priority
import com.example.data.PunishmentLog
import com.example.data.PunishmentType
import com.example.data.TaskItem
import com.example.data.TaskRepository
import com.example.util.SoundEffectsHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Calendar
import kotlin.random.Random

data class ActivePunishmentState(
  val punishmentLogId: Long,
  val task: TaskItem,
  val type: PunishmentType? = null, // null means user is in "Choose Your Punishment" selection modal
  val startedAt: Long = System.currentTimeMillis(),
  val timerSecondsRemaining: Int = 0,
  val timerTotalSeconds: Int = 0,
  val isTimerRunning: Boolean = false,
  val doubleWorkDuplicateTask: TaskItem? = null,
  val apologyDraftTarget: String = "",
  val apologyDraftInput: String = "",
  val fitnessTitle: String = "",
  val fitnessDescription: String = "",
  val fitnessRepsTarget: Int = 20,
  val fitnessRepsDone: Int = 0,
  val isCrimsonAlarmActive: Boolean = false,
  val isBreathingHold: Boolean = false,
  val breathingPhase: String = "Inhale" // Inhale, Hold, Exhale
)

enum class AppTab {
  TASKS,
  CALENDAR,
  PUNISHMENTS
}

enum class TaskFilter {
  ALL,
  TODAY,
  UPCOMING,
  OVERDUE,
  COMPLETED
}

class TaskViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: TaskRepository
  val soundHelper = SoundEffectsHelper(application)

  init {
    val database = AppDatabase.getDatabase(application)
    repository = TaskRepository(database.taskDao())
  }

  val tasks: StateFlow<List<TaskItem>> = repository.allTasks
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  val punishmentLogs: StateFlow<List<PunishmentLog>> = repository.allPunishmentLogs
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

  private val _selectedTab = MutableStateFlow(AppTab.TASKS)
  val selectedTab: StateFlow<AppTab> = _selectedTab.asStateFlow()

  private val _taskFilter = MutableStateFlow(TaskFilter.ALL)
  val taskFilter: StateFlow<TaskFilter> = _taskFilter.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedCalendarDate = MutableStateFlow(LocalDate.now())
  val selectedCalendarDate: StateFlow<LocalDate> = _selectedCalendarDate.asStateFlow()

  private val _currentCalendarMonth = MutableStateFlow(YearMonth.now())
  val currentCalendarMonth: StateFlow<YearMonth> = _currentCalendarMonth.asStateFlow()

  // Active punishment event state (locks dashboard when not null)
  private val _activePunishment = MutableStateFlow<ActivePunishmentState?>(null)
  val activePunishment: StateFlow<ActivePunishmentState?> = _activePunishment.asStateFlow()

  // Screen shake animation trigger (timestamp of latest trigger)
  private val _screenShakeTrigger = MutableStateFlow(0L)
  val screenShakeTrigger: StateFlow<Long> = _screenShakeTrigger.asStateFlow()

  // Confetti trigger (timestamp of latest completion)
  private val _confettiTrigger = MutableStateFlow(0L)
  val confettiTrigger: StateFlow<Long> = _confettiTrigger.asStateFlow()

  // Task dialog state (for create/edit)
  private val _taskDialogItem = MutableStateFlow<TaskItem?>(null) // null = closed, item with id=0 = create, item with id>0 = edit
  val taskDialogItem: StateFlow<TaskItem?> = _taskDialogItem.asStateFlow()
  private val _isTaskDialogOpen = MutableStateFlow(false)
  val isTaskDialogOpen: StateFlow<Boolean> = _isTaskDialogOpen.asStateFlow()

  private var punishmentTimerJob: Job? = null
  private var deadlineCheckerJob: Job? = null
  private var crimsonAlarmJob: Job? = null

  init {
    startDeadlineChecker()
    seedSampleTasksIfEmpty()
  }

  fun setTab(tab: AppTab) {
    _selectedTab.value = tab
  }

  fun setFilter(filter: TaskFilter) {
    _taskFilter.value = filter
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSelectedCalendarDate(date: LocalDate) {
    _selectedCalendarDate.value = date
  }

  fun nextCalendarMonth() {
    _currentCalendarMonth.value = _currentCalendarMonth.value.plusMonths(1)
  }

  fun prevCalendarMonth() {
    _currentCalendarMonth.value = _currentCalendarMonth.value.minusMonths(1)
  }

  fun setTodayCalendarMonth() {
    _currentCalendarMonth.value = YearMonth.now()
    _selectedCalendarDate.value = LocalDate.now()
  }

  fun openAddTaskDialog(defaultDate: LocalDate? = null) {
    val dueDate = defaultDate ?: LocalDate.now()
    val dueDateTime = dueDate.atTime(17, 0)
      .atZone(ZoneId.systemDefault())
      .toInstant()
      .toEpochMilli()

    _taskDialogItem.value = TaskItem(
      id = 0,
      title = "",
      description = "",
      dueDateTime = dueDateTime,
      priority = Priority.MEDIUM
    )
    _isTaskDialogOpen.value = true
  }

  fun openEditTaskDialog(task: TaskItem) {
    _taskDialogItem.value = task
    _isTaskDialogOpen.value = true
  }

  fun closeTaskDialog() {
    _isTaskDialogOpen.value = false
    _taskDialogItem.value = null
  }

  fun saveTask(title: String, description: String, dueDateTime: Long, priority: Priority) {
    val current = _taskDialogItem.value ?: return
    viewModelScope.launch {
      if (current.id == 0L) {
        val newTask = TaskItem(
          title = title.trim(),
          description = description.trim(),
          dueDateTime = dueDateTime,
          priority = priority
        )
        repository.insertTask(newTask)
      } else {
        val updated = current.copy(
          title = title.trim(),
          description = description.trim(),
          dueDateTime = dueDateTime,
          priority = priority
        )
        repository.updateTask(updated)
      }
      closeTaskDialog()
    }
  }

  fun toggleTaskCompletion(task: TaskItem) {
    viewModelScope.launch {
      val newCompleted = !task.isCompleted
      val updated = task.copy(
        isCompleted = newCompleted,
        completedAt = if (newCompleted) System.currentTimeMillis() else null
      )
      repository.updateTask(updated)

      if (newCompleted) {
        soundHelper.playDing()
        _confettiTrigger.value = System.currentTimeMillis()
      }

      // Check if this was a Double Work duplicate task or original
      val punishment = _activePunishment.value
      if (punishment != null && punishment.type == PunishmentType.DOUBLE_WORK) {
        checkDoubleWorkCompletion()
      }
    }
  }

  fun deleteTask(task: TaskItem) {
    viewModelScope.launch {
      repository.deleteTask(task)
    }
  }

  fun rescheduleTask(task: TaskItem, newDate: LocalDate) {
    viewModelScope.launch {
      val existingInstant = Instant.ofEpochMilli(task.dueDateTime)
      val existingLocalTime = existingInstant.atZone(ZoneId.systemDefault()).toLocalTime()
      val newEpochMillis = newDate.atTime(existingLocalTime)
        .atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

      val updated = task.copy(
        dueDateTime = newEpochMillis,
        penaltyTriggered = false // Reset penalty trigger if rescheduled to future
      )
      repository.updateTask(updated)
    }
  }

  // --- Accountability & Punishment Engine ---

  private fun startDeadlineChecker() {
    deadlineCheckerJob?.cancel()
    deadlineCheckerJob = viewModelScope.launch {
      while (isActive) {
        delay(10_000) // Check every 10 seconds
        val now = System.currentTimeMillis()
        val currentTasks = tasks.value
        val unpenalizedOverdue = currentTasks.firstOrNull { task ->
          !task.isCompleted && task.dueDateTime < now && !task.penaltyTriggered
        }
        if (unpenalizedOverdue != null && _activePunishment.value == null) {
          triggerPenaltyForTask(unpenalizedOverdue)
        }
      }
    }
  }

  fun triggerManualPenaltySimulation(task: TaskItem? = null) {
    val targetTask = task ?: tasks.value.firstOrNull { !it.isCompleted } ?: TaskItem(
      id = 9999,
      title = "Important Project Milestone",
      description = "Critical deadline missed!",
      dueDateTime = System.currentTimeMillis() - 60_000,
      priority = Priority.HIGH
    )
    triggerPenaltyForTask(targetTask)
  }

  private fun triggerPenaltyForTask(task: TaskItem) {
    viewModelScope.launch {
      soundHelper.playBuzzer()
      _screenShakeTrigger.value = System.currentTimeMillis()

      // Mark task as penalty triggered in DB if it's saved
      if (task.id > 0) {
        repository.updateTask(task.copy(penaltyTriggered = true))
      }

      // Record in PunishmentLog
      val initialLog = PunishmentLog(
        taskId = task.id,
        taskTitle = task.title,
        punishmentType = PunishmentType.DOUBLE_WORK, // placeholder until chosen
        timestamp = System.currentTimeMillis(),
        isCompleted = false
      )
      val logId = repository.insertPunishmentLog(initialLog)

      // Open "Choose Your Punishment" modal overlay (dashboard lock)
      _activePunishment.value = ActivePunishmentState(
        punishmentLogId = logId,
        task = task,
        type = null // triggers choice modal
      )
    }
  }

  fun selectPunishment(type: PunishmentType) {
    val current = _activePunishment.value ?: return
    val task = current.task

    viewModelScope.launch {
      // Update log with chosen type
      repository.updatePunishmentLog(
        PunishmentLog(
          id = current.punishmentLogId,
          taskId = task.id,
          taskTitle = task.title,
          punishmentType = type,
          timestamp = current.startedAt,
          isCompleted = false
        )
      )

      when (type) {
        PunishmentType.DOUBLE_WORK -> initDoubleWorkPunishment(current, task)
        PunishmentType.SCREEN_FREEZE -> initScreenFreezePunishment(current, task)
        PunishmentType.APOLOGY_DRAFT -> initApologyDraftPunishment(current, task)
        PunishmentType.FITNESS_CHALLENGE -> initFitnessChallengePunishment(current, task)
      }
    }
  }

  fun assignRandomPunishment() {
    val types = PunishmentType.values()
    val randomType = types[Random.nextInt(types.size)]
    selectPunishment(randomType)
  }

  // 1. Double Work Mode
  private suspend fun initDoubleWorkPunishment(current: ActivePunishmentState, task: TaskItem) {
    val duplicateTask = TaskItem(
      title = "DOUBLE WORK: ${task.title}",
      description = "Duplicated penalty task. Complete BOTH to survive the deadline failure!",
      dueDateTime = System.currentTimeMillis() + (20 * 60 * 1000),
      priority = Priority.HIGH,
      isDuplicatedByPenalty = true
    )
    val dupId = repository.insertTask(duplicateTask)
    val savedDup = duplicateTask.copy(id = dupId)

    val totalSeconds = 20 * 60 // 20 minutes
    _activePunishment.value = current.copy(
      type = PunishmentType.DOUBLE_WORK,
      doubleWorkDuplicateTask = savedDup,
      timerTotalSeconds = totalSeconds,
      timerSecondsRemaining = totalSeconds,
      isTimerRunning = true,
      isCrimsonAlarmActive = false
    )

    startDoubleWorkTimer()
  }

  private fun startDoubleWorkTimer() {
    punishmentTimerJob?.cancel()
    punishmentTimerJob = viewModelScope.launch {
      while (isActive) {
        delay(1000)
        val state = _activePunishment.value ?: break
        if (!state.isTimerRunning) continue

        val remaining = state.timerSecondsRemaining - 1
        if (remaining <= 0) {
          // Timer Expired! Crimson alarm mode activates!
          _activePunishment.value = state.copy(
            timerSecondsRemaining = 0,
            isTimerRunning = false,
            isCrimsonAlarmActive = true
          )
          startCrimsonAlarm()
          break
        } else {
          _activePunishment.value = state.copy(timerSecondsRemaining = remaining)
        }
      }
    }
  }

  private fun startCrimsonAlarm() {
    crimsonAlarmJob?.cancel()
    crimsonAlarmJob = viewModelScope.launch {
      while (isActive && _activePunishment.value?.isCrimsonAlarmActive == true) {
        soundHelper.playAlarmPulse()
        delay(1500)
      }
    }
  }

  fun fastForwardTimerForDemo(seconds: Int = 10) {
    val state = _activePunishment.value ?: return
    _activePunishment.value = state.copy(timerSecondsRemaining = seconds.coerceAtMost(state.timerSecondsRemaining))
  }

  private fun checkDoubleWorkCompletion() {
    val state = _activePunishment.value ?: return
    if (state.type != PunishmentType.DOUBLE_WORK) return

    viewModelScope.launch {
      val original = repository.getTaskById(state.task.id)
      val duplicate = state.doubleWorkDuplicateTask?.id?.let { repository.getTaskById(it) }

      if (original?.isCompleted == true && duplicate?.isCompleted == true) {
        // Both tasks completed! Punishment resolved!
        completeAndDismissPunishment("Conquered Double Work mode successfully!")
      }
    }
  }

  // 2. Screen Freeze & Focus Lock
  private fun initScreenFreezePunishment(current: ActivePunishmentState, task: TaskItem) {
    val totalSeconds = 300 // 5 minutes
    _activePunishment.value = current.copy(
      type = PunishmentType.SCREEN_FREEZE,
      timerTotalSeconds = totalSeconds,
      timerSecondsRemaining = totalSeconds,
      isTimerRunning = true,
      breathingPhase = "Inhale"
    )

    startScreenFreezeBreathingLoop()
  }

  private fun startScreenFreezeBreathingLoop() {
    punishmentTimerJob?.cancel()
    punishmentTimerJob = viewModelScope.launch {
      var cycleSec = 0
      while (isActive) {
        delay(1000)
        val state = _activePunishment.value ?: break
        val remaining = state.timerSecondsRemaining - 1
        cycleSec++

        // 4-4-4 Box breathing rhythm: 0-3 Inhale, 4-7 Hold, 8-11 Exhale, 12-15 Hold
        val phaseMod = cycleSec % 16
        val phase = when {
          phaseMod < 4 -> "Inhale Slowly"
          phaseMod < 8 -> "Hold & Reflect"
          phaseMod < 12 -> "Exhale Fully"
          else -> "Hold Steady"
        }

        if (remaining <= 0) {
          _activePunishment.value = state.copy(timerSecondsRemaining = 0, isTimerRunning = false)
          completeAndDismissPunishment("Completed 5-minute mindfulness focus freeze!")
          break
        } else {
          _activePunishment.value = state.copy(
            timerSecondsRemaining = remaining,
            breathingPhase = phase
          )
        }
      }
    }
  }

  // 3. The Apology Draft
  private val apologyDraftTemplates = listOf(
    "Dear Supreme Taskmaster, I am deeply ashamed of my catastrophic procrastination. My deadline sailed by while I was definitely not accomplishing anything worthwhile. I hereby promise on my keyboard and dignity to conquer my to-do list without excuses.",
    "To Whom It May Concern, I humbly confess that my time management skills have temporarily collapsed. I accept full responsibility for this missed deadline and promise to work with the urgency of someone whose battery is at one percent.",
    "Honorable Team, I publicly acknowledge that I dropped the productivity ball. Excuses are futile, so I pledge to finish my obligations immediately with unwavering focus."
  )

  private fun initApologyDraftPunishment(current: ActivePunishmentState, task: TaskItem) {
    val target = apologyDraftTemplates[Random.nextInt(apologyDraftTemplates.size)]
    _activePunishment.value = current.copy(
      type = PunishmentType.APOLOGY_DRAFT,
      apologyDraftTarget = target,
      apologyDraftInput = ""
    )
  }

  fun updateApologyDraftInput(input: String) {
    val state = _activePunishment.value ?: return
    _activePunishment.value = state.copy(apologyDraftInput = input)
  }

  fun submitApologyDraft() {
    val state = _activePunishment.value ?: return
    if (state.apologyDraftInput.trim() == state.apologyDraftTarget.trim()) {
      completeAndDismissPunishment("Apology letter successfully typed and acknowledged!")
    }
  }

  // 4. Wall Sit / Squat Challenge
  private val fitnessChallenges = listOf(
    Pair("Wall Sit Challenge", "Hold a 90-degree wall sit against the wall for 60 seconds to earn back your digital workspace."),
    Pair("20 Squats Challenge", "Perform 20 deep bodyweight squats right now before returning to your screen."),
    Pair("Pushup & Plank Burst", "Complete 15 pushups or a 45-second plank to redeem your productivity.")
  )

  private fun initFitnessChallengePunishment(current: ActivePunishmentState, task: TaskItem) {
    val challenge = fitnessChallenges[Random.nextInt(fitnessChallenges.size)]
    val totalSeconds = 60
    _activePunishment.value = current.copy(
      type = PunishmentType.FITNESS_CHALLENGE,
      fitnessTitle = challenge.first,
      fitnessDescription = challenge.second,
      timerTotalSeconds = totalSeconds,
      timerSecondsRemaining = totalSeconds,
      fitnessRepsTarget = 20,
      fitnessRepsDone = 0,
      isTimerRunning = true
    )

    startFitnessTimer()
  }

  private fun startFitnessTimer() {
    punishmentTimerJob?.cancel()
    punishmentTimerJob = viewModelScope.launch {
      while (isActive) {
        delay(1000)
        val state = _activePunishment.value ?: break
        if (!state.isTimerRunning) continue

        val remaining = state.timerSecondsRemaining - 1
        if (remaining <= 0) {
          _activePunishment.value = state.copy(timerSecondsRemaining = 0, isTimerRunning = false)
          break
        } else {
          _activePunishment.value = state.copy(timerSecondsRemaining = remaining)
        }
      }
    }
  }

  fun incrementFitnessReps() {
    val state = _activePunishment.value ?: return
    val newReps = (state.fitnessRepsDone + 1).coerceAtMost(state.fitnessRepsTarget)
    _activePunishment.value = state.copy(fitnessRepsDone = newReps)
  }

  fun completeFitnessChallenge() {
    completeAndDismissPunishment("Fitness punishment completed! Workspace restored.")
  }

  fun completeAndDismissPunishment(notes: String = "") {
    val current = _activePunishment.value ?: return
    viewModelScope.launch {
      punishmentTimerJob?.cancel()
      crimsonAlarmJob?.cancel()

      repository.updatePunishmentLog(
        PunishmentLog(
          id = current.punishmentLogId,
          taskId = current.task.id,
          taskTitle = current.task.title,
          punishmentType = current.type ?: PunishmentType.DOUBLE_WORK,
          timestamp = current.startedAt,
          isCompleted = true,
          notes = notes
        )
      )

      soundHelper.playDing()
      _confettiTrigger.value = System.currentTimeMillis()
      _activePunishment.value = null
    }
  }

  fun clearAllPunishmentLogs() {
    viewModelScope.launch {
      repository.clearPunishmentLogs()
    }
  }

  // Sample tasks initialization for crisp first experience
  private fun seedSampleTasksIfEmpty() {
    viewModelScope.launch {
      delay(300)
      if (tasks.value.isEmpty()) {
        val now = System.currentTimeMillis()
        val cal = Calendar.getInstance()

        // 1. Due today in 30 mins (Urgent reminder trigger!)
        val urgentTime = now + (35 * 60 * 1000)
        repository.insertTask(
          TaskItem(
            title = "Submit Quarterly Accountability Report",
            description = "Finalize KPI numbers and export the PDF draft.",
            dueDateTime = urgentTime,
            priority = Priority.HIGH
          )
        )

        // 2. Today evening task
        cal.timeInMillis = now
        cal.set(Calendar.HOUR_OF_DAY, 18)
        cal.set(Calendar.MINUTE, 0)
        repository.insertTask(
          TaskItem(
            title = "Review Weekly Budget & Receipts",
            description = "Reconcile recent business and personal expenses in spreadsheet.",
            dueDateTime = cal.timeInMillis,
            priority = Priority.MEDIUM
          )
        )

        // 3. Tomorrow task (Upcoming)
        val tomorrowTime = now + (26 * 3600 * 1000)
        repository.insertTask(
          TaskItem(
            title = "Prepare Pitch Deck Presentation",
            description = "Revise slide 4-8 with updated user retention charts.",
            dueDateTime = tomorrowTime,
            priority = Priority.HIGH
          )
        )

        // 4. Overdue task (Demo test for punishment engine!)
        val overdueTime = now - (45 * 60 * 1000) // 45 mins ago
        repository.insertTask(
          TaskItem(
            title = "Review Pull Request #42",
            description = "Missed deadline! Ready to test the punishment engine.",
            dueDateTime = overdueTime,
            priority = Priority.MEDIUM,
            penaltyTriggered = false
          )
        )
      }
    }
  }
}
