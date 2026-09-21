package com.example.ui.punishment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.PunishmentType
import com.example.data.TaskItem
import com.example.ui.viewmodel.ActivePunishmentState

@Composable
fun PunishmentOverlay(
  activePunishment: ActivePunishmentState,
  onSelectType: (PunishmentType) -> Unit,
  onRandomAssign: () -> Unit,
  onToggleTask: (TaskItem) -> Unit,
  onFastForward: (Int) -> Unit,
  onApologyInputChange: (String) -> Unit,
  onSubmitApology: () -> Unit,
  onIncrementFitnessRep: () -> Unit,
  onCompletePunishment: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier.fillMaxSize()) {
    when (activePunishment.type) {
      null -> {
        // Choice selection dialog
        ChoosePunishmentDialog(
          failedTask = activePunishment.task,
          onSelectType = onSelectType,
          onRandomAssign = onRandomAssign
        )
      }

      PunishmentType.DOUBLE_WORK -> {
        DoubleWorkView(
          originalTask = activePunishment.task,
          duplicateTask = activePunishment.doubleWorkDuplicateTask,
          secondsRemaining = activePunishment.timerSecondsRemaining,
          isCrimsonAlarmActive = activePunishment.isCrimsonAlarmActive,
          onToggleTask = onToggleTask,
          onFastForward = onFastForward,
          onForceComplete = onCompletePunishment
        )
      }

      PunishmentType.SCREEN_FREEZE -> {
        ScreenFreezeView(
          secondsRemaining = activePunishment.timerSecondsRemaining,
          breathingPhase = activePunishment.breathingPhase,
          onFastForward = onFastForward,
          onComplete = onCompletePunishment
        )
      }

      PunishmentType.APOLOGY_DRAFT -> {
        ApologyDraftView(
          targetLetter = activePunishment.apologyDraftTarget,
          userInput = activePunishment.apologyDraftInput,
          onInputChange = onApologyInputChange,
          onSubmit = onSubmitApology
        )
      }

      PunishmentType.FITNESS_CHALLENGE -> {
        FitnessChallengeView(
          title = activePunishment.fitnessTitle,
          description = activePunishment.fitnessDescription,
          secondsRemaining = activePunishment.timerSecondsRemaining,
          repsDone = activePunishment.fitnessRepsDone,
          repsTarget = activePunishment.fitnessRepsTarget,
          onIncrementRep = onIncrementFitnessRep,
          onFastForward = onFastForward,
          onComplete = onCompletePunishment
        )
      }
    }
  }
}
