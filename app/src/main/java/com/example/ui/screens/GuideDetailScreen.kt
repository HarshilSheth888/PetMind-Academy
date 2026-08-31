package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.data.TrainingGuidesData
import com.example.model.TrainingStep
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.PsychologyCalloutCard
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.BadgeGreenBg
import com.example.ui.theme.BadgeGreenText
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.PetMindViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideDetailScreen(
  guideId: String,
  viewModel: PetMindViewModel,
  onNavigateBack: () -> Unit,
  onStartSession: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val guide = remember(guideId) {
    TrainingGuidesData.guides.firstOrNull { it.id == guideId } ?: TrainingGuidesData.guides.first()
  }

  val activePet by viewModel.activePet.collectAsStateWithLifecycle()
  val currentSkillProgress by viewModel.currentPetSkillProgress.collectAsStateWithLifecycle()
  val progress = currentSkillProgress.firstOrNull { it.guideId == guide.id }
  val completedStepsCount = progress?.completedStepsCount ?: 0

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = guide.category,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("guide_detail_back_btn")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    bottomBar = {
      Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column {
            Text(
              text = "Ready to train ${activePet?.name ?: "your pet"}?",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "${guide.steps.size} steps in program",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
          }

          Button(
            onClick = { onStartSession(guide.id) },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
            modifier = Modifier.testTag("bottom_start_training_btn"),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Start Session", fontWeight = FontWeight.Bold)
          }
        }
      }
    },
    modifier = modifier.testTag("guide_detail_screen")
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(bottom = 80.dp)
    ) {
      // Header Details
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            DifficultyBadge(difficulty = guide.difficulty)
            Text(
              text = "Target: ${guide.speciesTarget}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.SemiBold
            )
          }

          Text(
            text = guide.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
          )

          Text(
            text = guide.shortDescription,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
          )

          Text(
            text = "⏱️ Estimated Sessions: ${guide.estimatedSessions}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Psychology Mechanics Card
      item {
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)) {
          PsychologyCalloutCard(
            insight = guide.whyItWorksPsychologically,
            title = "The Animal Psychology Behind This"
          )
        }
      }

      // Gear Needed Checklist
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Text(
              text = "🎒 Gear & Setup Needed",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            guide.gearNeeded.forEach { gear ->
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = gear,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }

      // Steps Header
      item {
        Text(
          text = "Step-by-Step Training Protocol",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
      }

      // Steps List
      itemsIndexed(guide.steps) { index, step ->
        val isCompleted = index < completedStepsCount
        TrainingStepCard(step = step, isCompleted = isCompleted)
      }

      // Troubleshooting Tips
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = AmberSecondary, modifier = Modifier.size(20.dp))
              Text(
                text = "Troubleshooting & Pro Tips",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
            }
            guide.troubleshootingTips.forEach { tip ->
              Text(
                text = "• $tip",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 19.sp
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun TrainingStepCard(step: TrainingStep, isCompleted: Boolean) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .testTag("step_card_${step.stepNumber}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier.padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(if (isCompleted) TealPrimary else MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            if (isCompleted) {
              Icon(Icons.Default.Check, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(16.dp))
            } else {
              Text(
                text = "${step.stepNumber}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }

          Text(
            text = step.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
        }

        if (step.cueWord != null) {
          Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = "Cue: \"${step.cueWord}\"",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }

      Text(
        text = step.instruction,
        style = MaterialTheme.typography.bodyMedium,
        lineHeight = 22.sp
      )

      // Pet Mindset Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
          .padding(10.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(
            text = "🧠 Pet's Mindset at This Stage:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = "\"${step.petMindset}\"",
            style = MaterialTheme.typography.bodySmall,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Success Criteria & Mistake to avoid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "✓ Success Criteria",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF137333)
          )
          Text(
            text = step.successCriteria,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "⚠️ Watch Out",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFC5221F)
          )
          Text(
            text = step.commonMistake,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
