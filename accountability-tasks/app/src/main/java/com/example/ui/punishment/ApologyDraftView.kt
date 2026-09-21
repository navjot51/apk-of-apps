package com.example.ui.punishment

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonRed
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@Composable
fun ApologyDraftView(
  targetLetter: String,
  userInput: String,
  onInputChange: (String) -> Unit,
  onSubmit: () -> Unit
) {
  val isExactMatch = userInput.trim() == targetLetter.trim()

  // Calculate matching characters
  var correctCount = 0
  for (i in 0 until minOf(userInput.length, targetLetter.length)) {
    if (userInput[i] == targetLetter[i]) {
      correctCount++
    } else {
      break
    }
  }

  val progress = if (targetLetter.isNotEmpty()) {
    (correctCount.toFloat() / targetLetter.length).coerceIn(0f, 1f)
  } else 0f

  val progressPercent = (progress * 100).toInt()

  // Visual annotated string showing live character matching
  val annotatedTarget = buildAnnotatedString {
    for (i in targetLetter.indices) {
      when {
        i < correctCount -> {
          pushStyle(SpanStyle(color = NeonEmerald, fontWeight = FontWeight.Bold))
          append(targetLetter[i])
          pop()
        }
        i < userInput.length -> {
          pushStyle(SpanStyle(color = NeonRed, background = NeonRed.copy(alpha = 0.2f)))
          append(targetLetter[i])
          pop()
        }
        else -> {
          pushStyle(SpanStyle(color = Slate400))
          append(targetLetter[i])
          pop()
        }
      }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Slate950)
      .padding(16.dp)
      .testTag("apology_draft_overlay")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(20.dp))

      // Header Badge
      Box(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(NeonAmber.copy(alpha = 0.2f))
          .border(1.dp, NeonAmber, RoundedCornerShape(20.dp))
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.EditNote,
            contentDescription = null,
            tint = NeonAmber,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "PUNISHMENT: THE APOLOGY DRAFT",
            color = NeonAmber,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            letterSpacing = 1.sp
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      Text(
        text = "Type Your Apology to Unlock",
        fontSize = 20.sp,
        fontWeight = FontWeight.Black,
        color = Color.White
      )

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = "Accountability check: You missed your deadline! You must manually type out the humiliating letter below character-for-character to unlock your workspace.",
        fontSize = 13.sp,
        color = Slate400,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Target Letter Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, Slate700, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        shape = RoundedCornerShape(16.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "TARGET APOLOGY TEXT",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = NeonAmber,
              letterSpacing = 1.sp
            )
            Text(
              text = "$correctCount / ${targetLetter.length} chars",
              fontSize = 11.sp,
              color = Slate400
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = annotatedTarget,
            fontSize = 14.sp,
            lineHeight = 22.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Progress bar
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = if (isExactMatch) NeonEmerald else NeonAmber,
        trackColor = Slate800,
      )

      Spacer(modifier = Modifier.height(4.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = if (userInput.length > correctCount) "Typo detected! Check red characters above." else "Typing accuracy",
          fontSize = 11.sp,
          color = if (userInput.length > correctCount) NeonRed else Slate400
        )
        Text(
          text = "$progressPercent%",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = if (isExactMatch) NeonEmerald else Color.White
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Typing Input Area
      OutlinedTextField(
        value = userInput,
        onValueChange = onInputChange,
        placeholder = { Text("Start typing the exact apology letter here...", color = Slate700) },
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .testTag("apology_draft_input"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = if (isExactMatch) NeonEmerald else NeonAmber,
          unfocusedBorderColor = Slate700,
          focusedTextColor = Color.White,
          unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Fast demo affordance
      Button(
        onClick = { onInputChange(targetLetter) },
        colors = ButtonDefaults.buttonColors(
          containerColor = Slate800,
          contentColor = NeonAmber
        ),
        shape = RoundedCornerShape(8.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
          horizontal = 12.dp,
          vertical = 6.dp
        ),
        modifier = Modifier.testTag("autofill_apology_btn")
      ) {
        Icon(imageVector = Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Auto-Fill for Quick Demo", fontSize = 11.sp)
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Submit / Unlock Button
      Button(
        onClick = onSubmit,
        enabled = isExactMatch,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (isExactMatch) NeonEmerald else Slate800,
          contentColor = if (isExactMatch) Slate950 else Slate400
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("submit_apology_btn")
      ) {
        Icon(
          imageVector = if (isExactMatch) Icons.Default.Check else Icons.Default.Lock,
          contentDescription = null,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (isExactMatch) "Apology Accepted - Unlock Dashboard" else "Type 100% Exact Letter to Unlock",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }

      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
