package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.TaskItem
import com.example.ui.components.ConfettiCanvas
import com.example.ui.components.MonthlyCalendarView
import com.example.ui.components.TaskCard
import com.example.ui.components.TaskDialog
import com.example.ui.components.UrgentReminderBanner
import com.example.ui.punishment.PunishmentOverlay
import com.example.ui.screens.PunishmentHistoryScreen
import com.example.ui.theme.MyApplicationTheme
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
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.TaskFilter
import com.example.ui.viewmodel.TaskViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class MainActivity : ComponentActivity() {
  private val viewModel: TaskViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppScreen(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun MainAppScreen(viewModel: TaskViewModel) {
  val tasks by viewModel.tasks.collectAsStateWithLifecycle()
  val punishmentLogs by viewModel.punishmentLogs.collectAsStateWithLifecycle()
  val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
  val taskFilter by viewModel.taskFilter.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedCalendarDate by viewModel.selectedCalendarDate.collectAsStateWithLifecycle()
  val currentCalendarMonth by viewModel.currentCalendarMonth.collectAsStateWithLifecycle()
  val activePunishment by viewModel.activePunishment.collectAsStateWithLifecycle()
  val screenShakeTrigger by viewModel.screenShakeTrigger.collectAsStateWithLifecycle()
  val confettiTrigger by viewModel.confettiTrigger.collectAsStateWithLifecycle()
  val taskDialogItem by viewModel.taskDialogItem.collectAsStateWithLifecycle()
  val isTaskDialogOpen by viewModel.isTaskDialogOpen.collectAsStateWithLifecycle()

  // Screen shake animation
  val shakeOffset = remember { Animatable(0f) }
  LaunchedEffect(screenShakeTrigger) {
    if (screenShakeTrigger > 0L) {
      for (i in 0 until 6) {
        shakeOffset.animateTo(if (i % 2 == 0) -12f else 12f, tween(40))
      }
      shakeOffset.animateTo(0f, tween(40))
    }
  }

  // Calculate urgent tasks (due within 1 hour)
  val now = System.currentTimeMillis()
  val urgentTasks = remember(tasks, now) {
    tasks.filter { !it.isCompleted && it.dueDateTime > now && it.dueDateTime <= (now + 3600_000) }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate950)
      .offset(x = shakeOffset.value.dp)
  ) {
    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .windowInsetsPadding(WindowInsets.statusBars),
      containerColor = Slate950,
      bottomBar = {
        NavigationBar(
          containerColor = Slate900,
          modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .border(1.dp, Slate800, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        ) {
          NavigationBarItem(
            selected = selectedTab == AppTab.TASKS,
            onClick = { viewModel.setTab(AppTab.TASKS) },
            icon = {
              Icon(
                imageVector = Icons.Default.Checklist,
                contentDescription = "Tasks"
              )
            },
            label = { Text("Tasks", fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Slate950,
              selectedTextColor = NeonIndigoLight,
              indicatorColor = NeonIndigoLight,
              unselectedIconColor = Slate400,
              unselectedTextColor = Slate400
            ),
            modifier = Modifier.testTag("nav_tab_tasks")
          )

          NavigationBarItem(
            selected = selectedTab == AppTab.CALENDAR,
            onClick = { viewModel.setTab(AppTab.CALENDAR) },
            icon = {
              Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Planner"
              )
            },
            label = { Text("Planner", fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Slate950,
              selectedTextColor = NeonIndigoLight,
              indicatorColor = NeonIndigoLight,
              unselectedIconColor = Slate400,
              unselectedTextColor = Slate400
            ),
            modifier = Modifier.testTag("nav_tab_calendar")
          )

          NavigationBarItem(
            selected = selectedTab == AppTab.PUNISHMENTS,
            onClick = { viewModel.setTab(AppTab.PUNISHMENTS) },
            icon = {
              Icon(
                imageVector = Icons.Default.Gavel,
                contentDescription = "Penalties"
              )
            },
            label = { Text("Penalties", fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = Slate950,
              selectedTextColor = NeonRed,
              indicatorColor = NeonRed,
              unselectedIconColor = Slate400,
              unselectedTextColor = Slate400
            ),
            modifier = Modifier.testTag("nav_tab_punishments")
          )
        }
      },
      floatingActionButton = {
        if (selectedTab == AppTab.TASKS) {
          FloatingActionButton(
            onClick = { viewModel.openAddTaskDialog() },
            containerColor = NeonIndigoLight,
            contentColor = Slate950,
            shape = CircleShape,
            modifier = Modifier.testTag("fab_add_task")
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task")
          }
        }
      }
    ) { innerPadding ->
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        // Top App Header
        AppHeader(
          onTriggerSimulatePenalty = { viewModel.triggerManualPenaltySimulation() }
        )

        // Urgent Reminder Banner (Due in < 1h)
        UrgentReminderBanner(
          urgentTasks = urgentTasks,
          onCompleteTask = { viewModel.toggleTaskCompletion(it) }
        )

        // Screen Content
        when (selectedTab) {
          AppTab.TASKS -> {
            TasksScreen(
              tasks = tasks,
              taskFilter = taskFilter,
              searchQuery = searchQuery,
              onFilterChange = { viewModel.setFilter(it) },
              onSearchChange = { viewModel.setSearchQuery(it) },
              onToggleComplete = { viewModel.toggleTaskCompletion(it) },
              onEdit = { viewModel.openEditTaskDialog(it) },
              onDelete = { viewModel.deleteTask(it) },
              onTriggerPenalty = { viewModel.triggerManualPenaltySimulation(it) },
              onAddTask = { viewModel.openAddTaskDialog() }
            )
          }

          AppTab.CALENDAR -> {
            MonthlyCalendarView(
              currentMonth = currentCalendarMonth,
              selectedDate = selectedCalendarDate,
              tasks = tasks,
              onDateSelected = { viewModel.setSelectedCalendarDate(it) },
              onPrevMonth = { viewModel.prevCalendarMonth() },
              onNextMonth = { viewModel.nextCalendarMonth() },
              onTodayMonth = { viewModel.setTodayCalendarMonth() },
              onAddTaskOnDate = { date -> viewModel.openAddTaskDialog(date) },
              onRescheduleTask = { task, date -> viewModel.rescheduleTask(task, date) },
              onToggleComplete = { viewModel.toggleTaskCompletion(it) },
              onEditTask = { viewModel.openEditTaskDialog(it) },
              onDeleteTask = { viewModel.deleteTask(it) },
              onTriggerPenalty = { viewModel.triggerManualPenaltySimulation(it) }
            )
          }

          AppTab.PUNISHMENTS -> {
            PunishmentHistoryScreen(
              punishmentLogs = punishmentLogs,
              onTriggerSimulation = { viewModel.triggerManualPenaltySimulation() },
              onClearLogs = { viewModel.clearAllPunishmentLogs() }
            )
          }
        }
      }
    }

    // Confetti Canvas Animation Overlay
    ConfettiCanvas(trigger = confettiTrigger)

    // Task Creation / Edit Dialog
    if (isTaskDialogOpen) {
      TaskDialog(
        task = taskDialogItem,
        onDismiss = { viewModel.closeTaskDialog() },
        onSave = { title, desc, dueEpoch, priority ->
          viewModel.saveTask(title, desc, dueEpoch, priority)
        }
      )
    }

    // Full Screen Punishment Overlay (Dashboard Lock when overdue penalty occurs!)
    activePunishment?.let { punishment ->
      PunishmentOverlay(
        activePunishment = punishment,
        onSelectType = { viewModel.selectPunishment(it) },
        onRandomAssign = { viewModel.assignRandomPunishment() },
        onToggleTask = { viewModel.toggleTaskCompletion(it) },
        onFastForward = { viewModel.fastForwardTimerForDemo(it) },
        onApologyInputChange = { viewModel.updateApologyDraftInput(it) },
        onSubmitApology = { viewModel.submitApologyDraft() },
        onIncrementFitnessRep = { viewModel.incrementFitnessReps() },
        onCompletePunishment = { viewModel.completeFitnessChallenge() }
      )
    }
  }
}

@Composable
fun AppHeader(onTriggerSimulatePenalty: () -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(34.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(NeonIndigoLight.copy(alpha = 0.2f))
          .border(1.dp, NeonIndigoLight, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.Checklist,
          contentDescription = null,
          tint = NeonIndigoLight,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = "ACCOUNTABILITY",
          fontSize = 15.sp,
          fontWeight = FontWeight.Black,
          color = Color.White,
          letterSpacing = 1.sp
        )
        Text(
          text = "Strict Deadline Enforcement",
          fontSize = 11.sp,
          color = Slate400
        )
      }
    }

    // Quick Test Penalty simulation affordance
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(NeonRed.copy(alpha = 0.15f))
        .border(1.dp, NeonRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
        .clickable { onTriggerSimulatePenalty() }
        .padding(horizontal = 10.dp, vertical = 6.dp)
        .testTag("header_simulate_penalty_btn"),
      contentAlignment = Alignment.Center
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Warning,
          contentDescription = null,
          tint = NeonRed,
          modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "Test Penalty",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = NeonRed
        )
      }
    }
  }
}

@Composable
fun TasksScreen(
  tasks: List<TaskItem>,
  taskFilter: TaskFilter,
  searchQuery: String,
  onFilterChange: (TaskFilter) -> Unit,
  onSearchChange: (String) -> Unit,
  onToggleComplete: (TaskItem) -> Unit,
  onEdit: (TaskItem) -> Unit,
  onDelete: (TaskItem) -> Unit,
  onTriggerPenalty: (TaskItem) -> Unit,
  onAddTask: () -> Unit
) {
  val now = System.currentTimeMillis()
  val todayDate = LocalDate.now()

  // Filter by search query
  val filteredTasks = tasks.filter {
    searchQuery.isBlank() ||
      it.title.contains(searchQuery, ignoreCase = true) ||
      it.description.contains(searchQuery, ignoreCase = true)
  }

  // Segregate into Overdue, Today, Upcoming, and Completed
  val overdueTasks = filteredTasks.filter { !it.isCompleted && it.dueDateTime < now }
  val todayTasks = filteredTasks.filter { task ->
    if (task.isCompleted || task.dueDateTime < now) return@filter false
    val taskDate = Instant.ofEpochMilli(task.dueDateTime).atZone(ZoneId.systemDefault()).toLocalDate()
    taskDate == todayDate
  }
  val upcomingTasks = filteredTasks.filter { task ->
    if (task.isCompleted || task.dueDateTime < now) return@filter false
    val taskDate = Instant.ofEpochMilli(task.dueDateTime).atZone(ZoneId.systemDefault()).toLocalDate()
    taskDate.isAfter(todayDate)
  }
  val completedTasks = filteredTasks.filter { it.isCompleted }

  Column(modifier = Modifier.fillMaxSize().testTag("tasks_screen")) {
    // Search Bar
    OutlinedTextField(
      value = searchQuery,
      onValueChange = onSearchChange,
      placeholder = { Text("Search tasks...", color = Slate400, fontSize = 13.sp) },
      leadingIcon = {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search",
          tint = Slate400,
          modifier = Modifier.size(18.dp)
        )
      },
      singleLine = true,
      shape = RoundedCornerShape(12.dp),
      colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NeonIndigoLight,
        unfocusedBorderColor = Slate800,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White
      ),
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 4.dp)
        .testTag("tasks_search_input")
    )

    // Filter Chips
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp),
      contentPadding = PaddingValues(horizontal = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(TaskFilter.values()) { filter ->
        val isSelected = taskFilter == filter
        val count = when (filter) {
          TaskFilter.ALL -> filteredTasks.size
          TaskFilter.TODAY -> todayTasks.size
          TaskFilter.UPCOMING -> upcomingTasks.size
          TaskFilter.OVERDUE -> overdueTasks.size
          TaskFilter.COMPLETED -> completedTasks.size
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) NeonIndigoLight else Slate900)
            .border(
              1.dp,
              if (isSelected) NeonIndigoLight else Slate800,
              RoundedCornerShape(10.dp)
            )
            .clickable { onFilterChange(filter) }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("filter_chip_${filter.name}"),
          contentAlignment = Alignment.Center
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = filter.name.lowercase().replaceFirstChar { it.uppercase() },
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
              color = if (isSelected) Slate950 else Color.White
            )
            Spacer(modifier = Modifier.width(6.dp))
            Box(
              modifier = Modifier
                .clip(CircleShape)
                .background(if (isSelected) Slate950.copy(alpha = 0.2f) else Slate800)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = count.toString(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Slate950 else Slate400
              )
            }
          }
        }
      }
    }

    // Tasks List with visual segregation
    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(bottom = 8.dp)
    ) {
      // Overdue section
      if (taskFilter == TaskFilter.ALL || taskFilter == TaskFilter.OVERDUE) {
        if (overdueTasks.isNotEmpty()) {
          item {
            SectionHeader(
              title = "OVERDUE PENALTY ZONE",
              count = overdueTasks.size,
              color = NeonRed,
              icon = Icons.Default.Warning
            )
          }
          items(overdueTasks, key = { it.id }) { task ->
            TaskCard(
              task = task,
              onToggleComplete = onToggleComplete,
              onEdit = onEdit,
              onDelete = onDelete,
              onTriggerPenalty = onTriggerPenalty
            )
          }
        }
      }

      // Today section
      if (taskFilter == TaskFilter.ALL || taskFilter == TaskFilter.TODAY) {
        if (todayTasks.isNotEmpty()) {
          item {
            SectionHeader(
              title = "DUE TODAY",
              count = todayTasks.size,
              color = NeonAmber
            )
          }
          items(todayTasks, key = { it.id }) { task ->
            TaskCard(
              task = task,
              onToggleComplete = onToggleComplete,
              onEdit = onEdit,
              onDelete = onDelete,
              onTriggerPenalty = onTriggerPenalty
            )
          }
        }
      }

      // Upcoming section
      if (taskFilter == TaskFilter.ALL || taskFilter == TaskFilter.UPCOMING) {
        if (upcomingTasks.isNotEmpty()) {
          item {
            SectionHeader(
              title = "UPCOMING DEADLINES",
              count = upcomingTasks.size,
              color = NeonCyan
            )
          }
          items(upcomingTasks, key = { it.id }) { task ->
            TaskCard(
              task = task,
              onToggleComplete = onToggleComplete,
              onEdit = onEdit,
              onDelete = onDelete,
              onTriggerPenalty = onTriggerPenalty
            )
          }
        }
      }

      // Completed section
      if (taskFilter == TaskFilter.ALL || taskFilter == TaskFilter.COMPLETED) {
        if (completedTasks.isNotEmpty()) {
          item {
            SectionHeader(
              title = "COMPLETED",
              count = completedTasks.size,
              color = NeonEmerald
            )
          }
          items(completedTasks, key = { it.id }) { task ->
            TaskCard(
              task = task,
              onToggleComplete = onToggleComplete,
              onEdit = onEdit,
              onDelete = onDelete,
              onTriggerPenalty = onTriggerPenalty
            )
          }
        }
      }

      // Empty State
      val currentVisibleCount = when (taskFilter) {
        TaskFilter.ALL -> filteredTasks.size
        TaskFilter.TODAY -> todayTasks.size
        TaskFilter.UPCOMING -> upcomingTasks.size
        TaskFilter.OVERDUE -> overdueTasks.size
        TaskFilter.COMPLETED -> completedTasks.size
      }

      if (currentVisibleCount == 0) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(40.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Checklist,
                contentDescription = null,
                tint = Slate700,
                modifier = Modifier.size(54.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "No tasks in this view",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Slate400
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Tap the '+' button below to add your next commitment.",
                fontSize = 12.sp,
                color = Slate700,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun SectionHeader(
  title: String,
  count: Int,
  color: Color,
  icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 6.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (icon != null) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
    }
    Text(
      text = title,
      fontSize = 11.sp,
      fontWeight = FontWeight.Black,
      color = color,
      letterSpacing = 1.sp
    )
    Spacer(modifier = Modifier.width(8.dp))
    Box(
      modifier = Modifier
        .clip(CircleShape)
        .background(color.copy(alpha = 0.2f))
        .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
      Text(
        text = count.toString(),
        fontSize = 10.sp,
        fontWeight = FontWeight.Black,
        color = color
      )
    }
  }
}
