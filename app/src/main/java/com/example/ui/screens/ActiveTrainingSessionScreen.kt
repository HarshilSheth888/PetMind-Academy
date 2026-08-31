package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.TrainingGuidesData
import com.example.ui.components.LogSessionDialog
import com.example.ui.components.PetAvatarBadge
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.BadgeGreenBg
import com.example.ui.theme.BadgeGreenText
import com.example.ui.theme.BadgeRoseBg
import com.example.ui.theme.BadgeRoseText
import com.example.ui.theme.TealPrimary
import com.example.util.ClickerAudioHelper
import com.example.viewmodel.PetMindViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveTrainingSessionScreen(
  guideId: String,
  viewModel: PetMindViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val guide = remember(guideId) {
    TrainingGuidesData.guides.firstOrNull { it.id == guideId } ?: TrainingGuidesData.guides.first()
  }
  val activePet by viewModel.activePet.collectAsStateWithLifecycle()

  var currentStepIndex by remember { mutableIntStateOf(0) }
  val currentStep = guide.steps.getOrNull(currentStepIndex) ?: guide.steps.first()

  // Stopwatch state
  var isTimerRunning by remember { mutableStateOf(true) }
  var secondsElapsed by remember { mutableIntStateOf(0) }

  LaunchedEffect(isTimerRunning) {
    while (isTimerRunning) {
      delay(1000L)
      secondsElapsed++
    }
  }

  // Repetition counters
  var successCount by remember { mutableIntStateOf(0) }
  var repeatCount by remember { mutableIntStateOf(0) }
  val totalReps = successCount + repeatCount

  var showLogDialog by remember { mutableStateOf(false) }

  // Animated ripple on clicker
  var clickTrigger by remember { mutableIntStateOf(0) }
  val clickerScale by androidx.compose.animation.core.animateFloatAsState(
    targetValue = if (clickTrigger % 2 == 1) 0.92f else 1f,
    animationSpec = androidx.compose.animation.core.spring(dampingRatio = 0.4f),
    label = "clicker_scale"
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = guide.title,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              maxLines = 1
            )
            if (activePet != null) {
              Text(
                text = "Training with ${activePet?.name}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("session_back_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Exit Session")
          }
        },
        actions = {
          Button(
            onClick = {
              isTimerRunning = false
              showLogDialog = true
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
              .padding(end = 8.dp)
              .testTag("finish_session_top_button"),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Finish", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    modifier = modifier.testTag("active_training_session_screen")
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Step Navigator Header
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          IconButton(
            onClick = {
              if (currentStepIndex > 0) currentStepIndex--
            },
            enabled = currentStepIndex > 0
          ) {
            Icon(Icons.Default.NavigateBefore, contentDescription = "Previous Step")
          }

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "STEP ${currentStep.stepNumber} OF ${guide.steps.size}",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = currentStep.title,
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
          }

          IconButton(
            onClick = {
              if (currentStepIndex < guide.steps.size - 1) currentStepIndex++
            },
            enabled = currentStepIndex < guide.steps.size - 1
          ) {
            Icon(Icons.Default.NavigateNext, contentDescription = "Next Step")
          }
        }
      }

      // Step Instruction & Cue Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          if (currentStep.cueWord != null) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = "Target Cue Word:",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
              )
              Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "\"${currentStep.cueWord}\"",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSecondaryContainer,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                )
              }
            }
          }
          Text(
            text = currentStep.instruction,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp
          )
          Text(
            text = "Criteria: ${currentStep.successCriteria}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
          )
        }
      }

      // Timer & Stats Ribbon
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Timer Card
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(
                text = String.format("%02d:%02d", secondsElapsed / 60, secondsElapsed % 60),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
              )
              Text("Elapsed", style = MaterialTheme.typography.labelSmall)
            }
            IconButton(
              onClick = { isTimerRunning = !isTimerRunning },
              modifier = Modifier.testTag("toggle_timer_button")
            ) {
              Icon(
                imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isTimerRunning) "Pause" else "Play",
                tint = MaterialTheme.colorScheme.primary
              )
            }
          }
        }

        // Success Rate Card
        Card(
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            val rate = if (totalReps > 0) ((successCount.toFloat() / totalReps) * 100).toInt() else 100
            Text(
              text = "$rate%",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = TealPrimary
            )
            Text(
              text = "$successCount / $totalReps Reps",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // BIG INTERACTIVE CLICKER BUTTON
      Text(
        text = "TAP CLICKER THE EXACT MILLISECOND PET SUCCEEDS",
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.outline,
        textAlign = TextAlign.Center
      )

      Box(
        modifier = Modifier
          .size(190.dp)
          .scale(clickerScale)
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              colors = listOf(AmberSecondary, Color(0xFFB55818))
            )
          )
          .clickable {
            ClickerAudioHelper.playClick(context)
            successCount++
            clickTrigger++
          }
          .testTag("big_clicker_button"),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.TouchApp,
            contentDescription = "Clicker Marker",
            tint = Color.White,
            modifier = Modifier.size(54.dp)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "CLICK!",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Black
          )
          Text(
            text = "+1 Mark Success",
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.85f)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      // Manual Rep Counter Buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Button(
          onClick = { repeatCount++ },
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .testTag("needs_repeat_button")
        ) {
          Text(
            text = "Needs Practice ($repeatCount)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
          )
        }

        Button(
          onClick = {
            ClickerAudioHelper.playClick(context)
            successCount++
          },
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
          modifier = Modifier
            .weight(1f)
            .height(52.dp)
            .testTag("success_rep_button")
        ) {
          Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Success ($successCount)",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    if (showLogDialog) {
      LogSessionDialog(
        guideTitle = guide.title,
        initialDurationSeconds = secondsElapsed,
        repetitions = totalReps,
        successCount = successCount,
        totalSteps = guide.steps.size,
        currentStepIndex = currentStepIndex,
        onDismiss = {
          showLogDialog = false
          isTimerRunning = true
        },
        onSave = { rating, notes, stepCompleted ->
          viewModel.logSession(
            guideId = guide.id,
            guideTitle = guide.title,
            durationSeconds = secondsElapsed,
            repetitions = totalReps,
            successCount = successCount,
            difficultyRating = rating,
            notes = notes,
            currentStepCompleted = stepCompleted,
            totalSteps = guide.steps.size
          )
          showLogDialog = false
          onNavigateBack()
        }
      )
    }
  }
}
