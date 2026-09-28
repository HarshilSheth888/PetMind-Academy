package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ArticleProgressEntity
import com.example.data.model.PetEntity
import com.example.model.Article
import com.example.model.ArticleCategory
import com.example.model.GuideDifficulty
import com.example.model.TrainingGuide
import com.example.ui.theme.AmberSecondary

@Composable
fun AddEditPetDialog(
  existingPet: PetEntity? = null,
  onDismiss: () -> Unit,
  onSave: (name: String, species: String, breed: String, ageMonths: Int, gender: String, tags: String, notes: String, avatarIndex: Int) -> Unit,
) {
  var name by remember { mutableStateOf(existingPet?.name ?: "") }
  var species by remember { mutableStateOf(existingPet?.species ?: "DOG") }
  var breed by remember { mutableStateOf(existingPet?.breed ?: "") }
  var ageYearsText by remember {
    mutableStateOf(existingPet?.let { (it.ageMonths / 12).toString() } ?: "1")
  }
  var ageMonthsText by remember {
    mutableStateOf(existingPet?.let { (it.ageMonths % 12).toString() } ?: "2")
  }
  var gender by remember { mutableStateOf(existingPet?.gender ?: "BOY") }
  var tags by remember { mutableStateOf(existingPet?.personalityTags ?: "Playful, Curious") }
  var notes by remember { mutableStateOf(existingPet?.notes ?: "") }
  var selectedAvatarIndex by remember { mutableIntStateOf(existingPet?.avatarIndex ?: 0) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    ),
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 16.dp)
        .imePadding()
        .testTag("pet_profile_dialog"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (existingPet == null) "Add Pet Profile" else "Edit Pet Profile",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        // Avatar Picker
        Text("Choose Avatar", style = MaterialTheme.typography.labelLarge)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          itemsIndexed(PetAvatarEmojis) { index, emoji ->
            val isSelected = index == selectedAvatarIndex
            Box(
              modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(PetAvatarColors[index % PetAvatarColors.size])
                .border(
                  width = if (isSelected) 3.dp else 0.dp,
                  color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                  shape = CircleShape
                )
                .clickable { selectedAvatarIndex = index }
                .testTag("avatar_option_$index"),
              contentAlignment = Alignment.Center
            ) {
              Text(text = emoji, fontSize = 24.sp)
            }
          }
        }

        // Name
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Pet Name *") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("pet_name_input"),
          singleLine = true
        )

        // Species Selector
        Text("Species", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          listOf("DOG" to "🐕 Dog", "CAT" to "🐈 Cat", "OTHER" to "🐾 Other").forEach { (type, label) ->
            val selected = species == type
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable { species = type },
              color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = label,
                modifier = Modifier.padding(vertical = 10.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Breed
        OutlinedTextField(
          value = breed,
          onValueChange = { breed = it },
          label = { Text("Breed / Mix") },
          placeholder = { Text("e.g. Golden Retriever, Tabby") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        // Age (Years + Months)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          OutlinedTextField(
            value = ageYearsText,
            onValueChange = { ageYearsText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Years") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
          OutlinedTextField(
            value = ageMonthsText,
            onValueChange = { ageMonthsText = it.filter { ch -> ch.isDigit() } },
            label = { Text("Months") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        // Gender Selector
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
          listOf("BOY" to "Boy (Male)", "GIRL" to "Girl (Female)").forEach { (gen, label) ->
            val selected = gender == gen
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable { gender = gen },
              color = if (selected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = label,
                modifier = Modifier.padding(vertical = 10.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        // Personality Tags
        OutlinedTextField(
          value = tags,
          onValueChange = { tags = it },
          label = { Text("Personality & Traits") },
          placeholder = { Text("e.g. Energetic, Sensitive, Food Motivated") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        // Focus / Notes
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Current Focus / Notes") },
          placeholder = { Text("e.g. Working on loose leash walking") },
          modifier = Modifier.fillMaxWidth(),
          minLines = 2
        )

        // Actions
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          TextButton(onClick = onDismiss) {
            Text("Cancel")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (name.isNotBlank()) {
                val totalMonths = ((ageYearsText.toIntOrNull() ?: 0) * 12) + (ageMonthsText.toIntOrNull() ?: 0)
                onSave(name.trim(), species, breed.trim(), totalMonths, gender, tags.trim(), notes.trim(), selectedAvatarIndex)
                onDismiss()
              }
            },
            enabled = name.isNotBlank(),
            modifier = Modifier.testTag("save_pet_button")
          ) {
            Text("Save Pet")
          }
        }
      }
    }
  }
}

@Composable
fun LogSessionDialog(
  guideTitle: String,
  initialDurationSeconds: Int,
  repetitions: Int,
  successCount: Int,
  totalSteps: Int,
  currentStepIndex: Int,
  onDismiss: () -> Unit,
  onSave: (rating: Int, notes: String, stepCompleted: Int) -> Unit
) {
  var rating by remember { mutableIntStateOf(4) }
  var notes by remember { mutableStateOf("") }
  var stepCompleted by remember { mutableIntStateOf(currentStepIndex + 1) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(
      usePlatformDefaultWidth = false,
      decorFitsSystemWindows = false
    )
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .padding(vertical = 16.dp)
        .imePadding()
        .testTag("log_session_dialog"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Text(
          text = "Log Training Session",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold
        )

        Text(
          text = guideTitle,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold
        )

        // Session Stats Summary Card
        Card(
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${initialDurationSeconds / 60}m ${initialDurationSeconds % 60}s",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text("Duration", style = MaterialTheme.typography.labelSmall)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = repetitions.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text("Reps", style = MaterialTheme.typography.labelSmall)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              val rate = if (repetitions > 0) ((successCount.toFloat() / repetitions) * 100).toInt() else 100
              Text(
                text = "$rate%",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
              )
              Text("Success Rate", style = MaterialTheme.typography.labelSmall)
            }
          }
        }

        // How did the session go? Rating
        Text("Session Success / Focus Rating", style = MaterialTheme.typography.labelLarge)
        Row(
          horizontalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxWidth()
        ) {
          for (star in 1..5) {
            IconButton(
              onClick = { rating = star },
              modifier = Modifier.testTag("rating_star_$star")
            ) {
              Icon(
                imageVector = if (star <= rating) Icons.Default.Star else Icons.Outlined.StarBorder,
                contentDescription = "$star stars",
                tint = if (star <= rating) AmberSecondary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(32.dp)
              )
            }
          }
        }

        // Step Progression
        Text("Step Completed Today", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          for (step in 1..totalSteps) {
            val selected = stepCompleted == step
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .clickable { stepCompleted = step },
              color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "Step $step",
                modifier = Modifier.padding(vertical = 8.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        // Reflection Notes
        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Reflection & Observations") },
          placeholder = { Text("e.g. Great focus indoors! Leash stayed loose.") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("session_notes_input"),
          minLines = 2
        )

        // Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismiss) {
            Text("Cancel")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              onSave(rating, notes.trim(), stepCompleted)
              onDismiss()
            },
            modifier = Modifier.testTag("confirm_log_session_button")
          ) {
            Text("Save Log")
          }
        }
      }
    }
  }
}

@Composable
fun TrainingFilterDialog(
  allGuides: List<TrainingGuide>,
  currentDifficulties: Set<GuideDifficulty>,
  currentCategories: Set<String>,
  currentSpecies: Set<String>,
  activePet: PetEntity? = null,
  onApply: (Set<GuideDifficulty>, Set<String>, Set<String>) -> Unit,
  onDismiss: () -> Unit
) {
  var tempDifficulties by remember { mutableStateOf(currentDifficulties) }
  var tempCategories by remember { mutableStateOf(currentCategories) }
  var tempSpecies by remember { mutableStateOf(currentSpecies) }

  val filteredCount = remember(tempDifficulties, tempCategories, tempSpecies, activePet) {
    allGuides.filter { guide ->
      val matchesDifficulty = tempDifficulties.isEmpty() || (guide.difficulty in tempDifficulties)
      val matchesCategory = tempCategories.isEmpty() || tempCategories.any { it.equals(guide.category, ignoreCase = true) }
      val matchesSpecies = guide.matchesSpecies(tempSpecies, activePet)
      matchesDifficulty && matchesCategory && matchesSpecies
    }.size
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("training_filter_dialog"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Filter Guides",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Text("Difficulty", style = MaterialTheme.typography.labelLarge)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          item {
            CategoryChip(
              label = "All",
              isSelected = tempDifficulties.isEmpty(),
              onClick = { tempDifficulties = emptySet() }
            )
          }
          items(GuideDifficulty.entries.toTypedArray()) { diff ->
            val isSelected = diff in tempDifficulties
            CategoryChip(
              label = diff.label,
              isSelected = isSelected,
              onClick = {
                tempDifficulties = if (isSelected) tempDifficulties - diff else tempDifficulties + diff
              }
            )
          }
        }

        Text("Category", style = MaterialTheme.typography.labelLarge)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          item {
            CategoryChip(
              label = "All",
              isSelected = tempCategories.isEmpty(),
              onClick = { tempCategories = emptySet() }
            )
          }
          items(listOf("Manners", "Focus", "Safety", "Confidence", "Tricks")) { cat ->
            val isSelected = cat in tempCategories
            Surface(
              modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                  tempCategories = if (isSelected) tempCategories - cat else tempCategories + cat
                }
                .testTag("filter_category_$cat"),
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

        Text("Species", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf("Dog" to "🐕 Dogs", "Cat" to "🐈 Cats").forEach { (speciesKey, label) ->
            val isSelected = speciesKey in tempSpecies
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                  tempSpecies = if (isSelected) tempSpecies - speciesKey else tempSpecies + speciesKey
                },
              color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = label,
                modifier = Modifier.padding(vertical = 10.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
          onClick = {
            onApply(tempDifficulties, tempCategories, tempSpecies)
            onDismiss()
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Apply Filters ($filteredCount)")
        }
      }
    }
  }
}

@Composable
fun ArticleFilterDialog(
  allArticles: List<Article>,
  articleProgressList: List<ArticleProgressEntity>,
  currentCategories: Set<ArticleCategory>,
  showBookmarksOnly: Boolean,
  currentSpecies: Set<String>,
  activePet: PetEntity? = null,
  onApply: (Set<ArticleCategory>, Set<String>, Boolean) -> Unit,
  onDismiss: () -> Unit
) {
  var tempCategories by remember { mutableStateOf(currentCategories) }
  var tempBookmarksOnly by remember { mutableStateOf(showBookmarksOnly) }
  var tempSpecies by remember { mutableStateOf(currentSpecies) }

  val filteredCount = remember(tempCategories, tempBookmarksOnly, tempSpecies, articleProgressList, activePet) {
    val progressMap = articleProgressList.associateBy { it.articleId }
    allArticles.filter { article ->
      val matchesCategory = tempCategories.isEmpty() || article.category in tempCategories
      val matchesSpecies = article.matchesSpecies(tempSpecies, activePet)
      val matchesBookmark = !tempBookmarksOnly || (progressMap[article.id]?.isBookmarked == true)
      matchesCategory && matchesSpecies && matchesBookmark
    }.size
  }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("article_filter_dialog"),
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Filter Articles",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        // Saved / Bookmarks Toggle
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { tempBookmarksOnly = !tempBookmarksOnly },
          color = if (tempBookmarksOnly) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          shape = RoundedCornerShape(12.dp)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Icon(
              imageVector = if (tempBookmarksOnly) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = null,
              tint = if (tempBookmarksOnly) AmberSecondary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "Show Saved Articles Only",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = if (tempBookmarksOnly) FontWeight.Bold else FontWeight.Normal,
              color = if (tempBookmarksOnly) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Text("Topics", style = MaterialTheme.typography.labelLarge)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          item {
            CategoryChip(
              label = "All Topics",
              isSelected = tempCategories.isEmpty(),
              onClick = { tempCategories = emptySet() }
            )
          }
          items(ArticleCategory.entries.toTypedArray()) { category ->
            val isSelected = category in tempCategories
            CategoryChip(
              label = category.title,
              isSelected = isSelected,
              onClick = {
                tempCategories = if (isSelected) tempCategories - category else tempCategories + category
              }
            )
          }
        }

        Text("Species", style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf("Dog" to "🐕 Dogs", "Cat" to "🐈 Cats").forEach { (speciesKey, label) ->
            val isSelected = speciesKey in tempSpecies
            Surface(
              modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                  tempSpecies = if (isSelected) tempSpecies - speciesKey else tempSpecies + speciesKey
                },
              color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = label,
                modifier = Modifier.padding(vertical = 10.dp),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Button(
          onClick = {
            onApply(tempCategories, tempSpecies, tempBookmarksOnly)
            onDismiss()
          },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Apply Filters ($filteredCount)")
        }
      }
    }
  }
}
