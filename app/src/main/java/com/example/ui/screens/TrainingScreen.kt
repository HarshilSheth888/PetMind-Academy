package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.GuideDifficulty
import com.example.model.TrainingGuide
import com.example.ui.components.CategoryChip
import com.example.ui.components.DifficultyBadge
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.TealPrimary
import com.example.util.ClickerAudioHelper
import com.example.viewmodel.PetMindViewModel

@Composable
fun TrainingScreen(
  viewModel: PetMindViewModel,
  onGuideClick: (String) -> Unit,
  onStartSession: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val trainingState by viewModel.trainingState.collectAsStateWithLifecycle()
  val guides by viewModel.filteredGuides.collectAsStateWithLifecycle()
  val activePet by viewModel.activePet.collectAsStateWithLifecycle()
  val currentSkillProgress by viewModel.currentPetSkillProgress.collectAsStateWithLifecycle()
  val progressMap = remember(currentSkillProgress) { currentSkillProgress.associateBy { it.guideId } }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("training_screen_list"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Header
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 16.dp)
      ) {
        Text(
          text = "Interactive Training",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.primary
        )
        Text(
          text = if (activePet != null) "Training programs for ${activePet?.name}" else "Science-backed positive reinforcement guides",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }

    // Quick Clicker Practice Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 6.dp)
          .testTag("quick_clicker_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Quick Audio Clicker",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = "Tap to test clicker timing with tone + haptics",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
            )
          }

          Button(
            onClick = { ClickerAudioHelper.playClick(context) },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
            modifier = Modifier
              .size(56.dp)
              .testTag("quick_clicker_button"),
            contentPadding = PaddingValues(0.dp)
          ) {
            Icon(
              imageVector = Icons.Default.TouchApp,
              contentDescription = "Click",
              tint = androidx.compose.ui.graphics.Color.White,
              modifier = Modifier.size(28.dp)
            )
          }
        }
      }
    }

    // Search Bar
    item {
      OutlinedTextField(
        value = trainingState.searchQuery,
        onValueChange = { viewModel.setGuideSearch(it) },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp)
          .testTag("training_search_input"),
        placeholder = { Text("Search guides, leash, recall, crate, tricks...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.outline) },
        trailingIcon = {
          if (trainingState.searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.setGuideSearch("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
          }
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
          unfocusedContainerColor = MaterialTheme.colorScheme.surface,
          focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        singleLine = true
      )
    }

    // Difficulty Filter Chips
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        item {
          CategoryChip(
            label = "All Levels",
            isSelected = trainingState.selectedDifficulty == null,
            onClick = { viewModel.setGuideDifficulty(null) }
          )
        }
        items(GuideDifficulty.values()) { diff ->
          CategoryChip(
            label = diff.label,
            isSelected = trainingState.selectedDifficulty == diff,
            onClick = {
              viewModel.setGuideDifficulty(if (trainingState.selectedDifficulty == diff) null else diff)
            }
          )
        }
      }
    }

    // Category / Focus Chips
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(listOf("All", "Manners", "Focus", "Safety", "Confidence", "Tricks")) { cat ->
          val isSelected = trainingState.selectedCategory == cat
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .clickable { viewModel.setGuideCategory(cat) }
              .testTag("guide_category_$cat"),
            color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(
              text = cat,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // Guides Count
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Training Guides (${guides.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Guides List
    items(guides, key = { it.id }) { guide ->
      val progress = progressMap[guide.id]
      val completedSteps = progress?.completedStepsCount ?: 0
      val isMastered = progress?.isMastered == true || completedSteps >= guide.steps.size

      TrainingGuideCardItem(
        guide = guide,
        completedSteps = completedSteps,
        isMastered = isMastered,
        onCardClick = { onGuideClick(guide.id) },
        onStartSession = { onStartSession(guide.id) }
      )
    }
  }
}

@Composable
fun TrainingGuideCardItem(
  guide: TrainingGuide,
  completedSteps: Int,
  isMastered: Boolean,
  onCardClick: () -> Unit,
  onStartSession: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(18.dp))
      .clickable(onClick = onCardClick)
      .testTag("guide_card_${guide.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          DifficultyBadge(difficulty = guide.difficulty)
          Text(
            text = guide.category,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
          )
        }

        if (isMastered) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(MaterialTheme.colorScheme.primaryContainer)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "Mastered",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      Text(
        text = guide.title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )

      Text(
        text = guide.shortDescription,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      // Step Progress Bar
      val progressFrac = if (guide.steps.isNotEmpty()) (completedSteps.toFloat() / guide.steps.size).coerceIn(0f, 1f) else 0f
      Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Progress: $completedSteps of ${guide.steps.size} steps completed",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "${(progressFrac * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
        }
        LinearProgressIndicator(
          progress = { progressFrac },
          modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = MaterialTheme.colorScheme.primary,
          trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
      }

      // Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "🎯 ${guide.estimatedSessions}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )

        Button(
          onClick = onStartSession,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
          modifier = Modifier.testTag("start_session_btn_${guide.id}"),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp)
        ) {
          Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Practice", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
