package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.data.model.MilestoneEntity
import com.example.data.model.PetEntity
import com.example.data.model.TrainingLogEntity
import com.example.ui.components.AddEditPetDialog
import com.example.ui.components.PetAvatarBadge
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.BadgeGreenBg
import com.example.ui.theme.BadgeGreenText
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.PetMindViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressScreen(
  viewModel: PetMindViewModel,
  onNavigateToGuide: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val allPets by viewModel.allPets.collectAsStateWithLifecycle()
  val activePet by viewModel.activePet.collectAsStateWithLifecycle()
  val logs by viewModel.currentPetLogs.collectAsStateWithLifecycle()
  val skillProgressList by viewModel.currentPetSkillProgress.collectAsStateWithLifecycle()
  val milestones by viewModel.currentPetMilestones.collectAsStateWithLifecycle()
  val totalTrainingTimeSeconds by viewModel.currentPetTotalTime.collectAsStateWithLifecycle()
  val totalSessionCount by viewModel.currentPetSessionCount.collectAsStateWithLifecycle()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Skills, 1: Milestones, 2: Logs History
  var showAddPetDialog by remember { mutableStateOf(false) }
  var petToEdit by remember { mutableStateOf<PetEntity?>(null) }

  val masteredCount = remember(skillProgressList) {
    skillProgressList.count { it.isMastered }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("progress_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Header & Pet Selector
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Pet Development",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.ExtraBold,
              color = MaterialTheme.colorScheme.primary
            )
            Text(
              text = "Progress tracking & behavior milestones",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Button(
            onClick = { showAddPetDialog = true },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            modifier = Modifier.testTag("add_pet_top_button")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Pet", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          }
        }

        // Pet Profile Selector Cards
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(allPets) { pet ->
            val isSelected = pet.id == activePet?.id
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable { viewModel.selectPet(pet.id) }
                .testTag("pet_selector_${pet.id}"),
              color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              shape = RoundedCornerShape(16.dp)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                PetAvatarBadge(avatarIndex = pet.avatarIndex, species = pet.species, sizeDp = 36)
                Column {
                  Text(
                    text = pet.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Text(
                    text = "${pet.species} • ${pet.breed.ifBlank { "Companion" }}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.outline
                  )
                }
              }
            }
          }
        }
      }
    }

    // Active Pet Detailed Profile Card
    if (activePet != null) {
      item {
        val pet = activePet!!
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                PetAvatarBadge(avatarIndex = pet.avatarIndex, species = pet.species, sizeDp = 48)
                Column {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = pet.name,
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold
                    )
                    Text(
                      text = if (pet.gender == "BOY") "♂ Boy" else "♀ Girl",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                  Text(
                    text = "${pet.ageMonths / 12}y ${pet.ageMonths % 12}m • ${pet.breed.ifBlank { pet.species }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              IconButton(
                onClick = { petToEdit = pet },
                modifier = Modifier.testTag("edit_pet_button")
              ) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Pet", tint = MaterialTheme.colorScheme.outline)
              }
            }

            if (pet.personalityTags.isNotBlank()) {
              Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "Traits: ${pet.personalityTags}",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSecondaryContainer,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
              }
            }

            if (pet.notes.isNotBlank()) {
              Text(
                text = "Focus: ${pet.notes}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }
    }

    // Quick Stats 4-Grid
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Training Time
        StatCard(
          label = "Total Time",
          value = "${(totalTrainingTimeSeconds ?: 0) / 60}m",
          icon = Icons.Default.Timer,
          modifier = Modifier.weight(1f)
        )
        // Sessions
        StatCard(
          label = "Sessions",
          value = "$totalSessionCount",
          icon = Icons.Default.History,
          modifier = Modifier.weight(1f)
        )
        // Skills Mastered
        StatCard(
          label = "Mastered",
          value = "$masteredCount",
          icon = Icons.Default.CheckCircle,
          modifier = Modifier.weight(1f)
        )
        // Milestones
        val unlockedMilestones = milestones.count { it.isUnlocked }
        StatCard(
          label = "Badges",
          value = "$unlockedMilestones",
          icon = Icons.Default.EmojiEvents,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Section Tabs
    item {
      TabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
        containerColor = MaterialTheme.colorScheme.surface
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("Skills (${skillProgressList.size})") }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Milestones (${milestones.size})") }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("Logs (${logs.size})") }
        )
      }
    }

    // Tab 0: Skills in Progress
    if (selectedTab == 0) {
      if (skillProgressList.isEmpty()) {
        item {
          EmptyStateMessage(
            message = "No active skills recorded yet.",
            subMessage = "Start a practice session from the Training tab to track progress!"
          )
        }
      } else {
        items(skillProgressList, key = { it.id }) { skill ->
          val guide = TrainingGuidesData.guides.firstOrNull { it.id == skill.guideId }
          val title = guide?.title ?: "Skill Training"
          val progressFrac = if (skill.totalStepsCount > 0) (skill.completedStepsCount.toFloat() / skill.totalStepsCount).coerceIn(0f, 1f) else 0f

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 6.dp)
              .clickable { onNavigateToGuide(skill.guideId) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = title,
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.weight(1f)
                )
                if (skill.isMastered) {
                  Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(6.dp)
                  ) {
                    Text(
                      text = "Mastered",
                      style = MaterialTheme.typography.labelSmall,
                      color = MaterialTheme.colorScheme.onPrimaryContainer,
                      fontWeight = FontWeight.Bold,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Step ${skill.completedStepsCount} of ${skill.totalStepsCount}",
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
          }
        }
      }
    }

    // Tab 1: Milestones & Badges
    if (selectedTab == 1) {
      if (milestones.isEmpty()) {
        item {
          EmptyStateMessage(
            message = "No milestones initialized yet.",
            subMessage = "Add a pet profile to start unlocking development badges."
          )
        }
      } else {
        items(milestones, key = { it.id }) { milestone ->
          MilestoneCardItem(
            milestone = milestone,
            onToggle = { viewModel.toggleMilestone(milestone) }
          )
        }
      }
    }

    // Tab 2: Training Log History
    if (selectedTab == 2) {
      if (logs.isEmpty()) {
        item {
          EmptyStateMessage(
            message = "No training logs yet.",
            subMessage = "Complete your first interactive training session to record observations!"
          )
        }
      } else {
        items(logs, key = { it.id }) { log ->
          TrainingLogCardItem(log = log)
        }
      }
    }
  }

  if (showAddPetDialog) {
    AddEditPetDialog(
      onDismiss = { showAddPetDialog = false },
      onSave = { name, species, breed, ageMonths, gender, tags, notes, avatarIndex ->
        viewModel.addNewPet(name, species, breed, ageMonths, gender, tags, notes, avatarIndex)
      }
    )
  }

  if (petToEdit != null) {
    AddEditPetDialog(
      existingPet = petToEdit,
      onDismiss = { petToEdit = null },
      onSave = { name, species, breed, ageMonths, gender, tags, notes, avatarIndex ->
        viewModel.updatePet(
          petToEdit!!.copy(
            name = name,
            species = species,
            breed = breed,
            ageMonths = ageMonths,
            gender = gender,
            personalityTags = tags,
            notes = notes,
            avatarIndex = avatarIndex
          )
        )
        petToEdit = null
      }
    )
  }
}

@Composable
fun StatCard(
  label: String,
  value: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier,
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 4.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(20.dp)
      )
      Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.outline
      )
    }
  }
}

@Composable
fun MilestoneCardItem(
  milestone: MilestoneEntity,
  onToggle: () -> Unit
) {
  val icon = when (milestone.iconType) {
    "STAR" -> Icons.Default.Star
    "SHIELD" -> Icons.Default.Security
    "TROPHY" -> Icons.Default.EmojiEvents
    "ACADEMIC" -> Icons.Default.School
    "HEART" -> Icons.Default.Favorite
    else -> Icons.Default.EmojiEvents
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .clickable(onClick = onToggle)
      .testTag("milestone_card_${milestone.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (milestone.isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (milestone.isUnlocked) 1.5.dp else 0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(if (milestone.isUnlocked) AmberSecondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (milestone.isUnlocked) Color.White else MaterialTheme.colorScheme.outline,
          modifier = Modifier.size(22.dp)
        )
      }

      Column(modifier = Modifier.weight(1f)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = milestone.title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (milestone.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
          )
        }
        Text(
          text = milestone.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Icon(
        imageVector = if (milestone.isUnlocked) Icons.Default.LockOpen else Icons.Default.Lock,
        contentDescription = null,
        tint = if (milestone.isUnlocked) TealPrimary else MaterialTheme.colorScheme.outline,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}

@Composable
fun TrainingLogCardItem(log: TrainingLogEntity) {
  val dateStr = remember(log.timestamp) {
    SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault()).format(Date(log.timestamp))
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .testTag("training_log_card_${log.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = log.guideTitle,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.weight(1f)
        )
        // Rating Stars
        Row {
          for (i in 1..5) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = null,
              tint = if (i <= log.difficultyRating) AmberSecondary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(
          text = "⏱️ ${log.durationSeconds / 60}m ${log.durationSeconds % 60}s",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary
        )
        Text(
          text = "🎯 ${log.successCount}/${log.repetitions} Reps",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary
        )
        Text(
          text = dateStr,
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )
      }

      if (log.notes.isNotBlank()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(10.dp)
        ) {
          Text(
            text = "\"${log.notes}\"",
            style = MaterialTheme.typography.bodySmall,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
fun EmptyStateMessage(message: String, subMessage: String) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 24.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Pets,
        contentDescription = null,
        modifier = Modifier.size(36.dp),
        tint = MaterialTheme.colorScheme.outline
      )
      Text(
        text = message,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = subMessage,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
      )
    }
  }
}
