package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.model.BehaviorSignal
import com.example.ui.components.EmotionalStateBadge
import com.example.ui.theme.BadgeGreenBg
import com.example.ui.theme.BadgeGreenText
import com.example.ui.theme.BadgeRoseBg
import com.example.ui.theme.BadgeRoseText
import com.example.ui.theme.BrandGradient
import com.example.ui.theme.TealPrimary
import com.example.ui.components.ThemeToggleButton
import com.example.viewmodel.PetMindViewModel

@Composable
fun BehaviorDecoderScreen(
  viewModel: PetMindViewModel,
  modifier: Modifier = Modifier
) {
  val decoderState by viewModel.decoderState.collectAsStateWithLifecycle()
  val signals by viewModel.filteredSignals.collectAsStateWithLifecycle()
  val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
  val expandedCards = remember { mutableStateMapOf<String, Boolean>() }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("behavior_decoder_screen"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(
          modifier = Modifier.weight(1f, fill = false),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(
            text = "Behavior Decoder",
            style = MaterialTheme.typography.headlineMedium.copy(
              brush = BrandGradient
            ),
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = "Translate physical signals and body language into emotional states",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        ThemeToggleButton(
          themeMode = themeMode,
          onToggle = { viewModel.cycleThemeMode() }
        )
      }
    }

    // Species Tabs (Dog / Cat)
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        listOf("Dog" to "🐕 Canine Signals", "Cat" to "🐈 Feline Signals").forEach { (speciesKey, label) ->
          val isSelected = decoderState.selectedSpecies == speciesKey
          Surface(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(14.dp))
              .clickable { viewModel.setDecoderSpecies(speciesKey) }
              .testTag("decoder_species_$speciesKey"),
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(14.dp)
          ) {
            Text(
              text = label,
              modifier = Modifier.padding(vertical = 12.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
              style = MaterialTheme.typography.labelMedium,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
              color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // Search Bar
    item {
      OutlinedTextField(
        value = decoderState.searchQuery,
        onValueChange = { viewModel.setDecoderSearch(it) },
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp)
          .testTag("decoder_search_input"),
        placeholder = { Text("Search signal, whale eye, tail, ears...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.outline) },
        trailingIcon = {
          if (decoderState.searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.setDecoderSearch("") }) {
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

    // Body Part Filter Chips
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        items(listOf("All", "Eyes", "Ears", "Tail", "Mouth/Face", "Body Posture")) { bodyPart ->
          val isSelected = decoderState.selectedBodyPart == bodyPart
          Surface(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .clickable { viewModel.setDecoderBodyPart(bodyPart) }
              .testTag("body_part_chip_$bodyPart"),
            color = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text(
              text = bodyPart,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
              style = MaterialTheme.typography.labelSmall,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // Signals Count
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Observed Signals (${signals.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Signals List
    items(signals, key = { it.id }) { signal ->
      val isExpanded = expandedCards[signal.id] ?: false
      BehaviorSignalCard(
        signal = signal,
        isExpanded = isExpanded,
        onToggleExpand = {
          expandedCards[signal.id] = !isExpanded
        }
      )
    }
  }
}

@Composable
fun BehaviorSignalCard(
  signal: BehaviorSignal,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(18.dp))
      .clickable(onClick = onToggleExpand)
      .testTag("decoder_card_${signal.id}"),
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
        EmotionalStateBadge(state = signal.emotionalState)

        Surface(
          color = MaterialTheme.colorScheme.surfaceVariant,
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = signal.bodyPart,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Text(
        text = signal.observationTitle,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )

      Text(
        text = signal.description,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      // Meaning Card
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(10.dp))
          .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f))
          .padding(12.dp)
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text(
            text = "🧠 What It Means:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = signal.whatItMeans,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            lineHeight = 18.sp
          )
        }
      }

      // Expandable Actionable Guidelines
      AnimatedVisibility(visible = isExpanded) {
        Column(
          modifier = Modifier.padding(top = 6.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Scientific Mechanism
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(8.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
              .padding(10.dp)
          ) {
            Text(
              text = "🔬 Scientific Insight: ${signal.scientificExplanation}",
              style = MaterialTheme.typography.bodySmall,
              fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          // What owner should do
          Card(
            colors = CardDefaults.cardColors(containerColor = BadgeGreenBg.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "✓ What You Should Do:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = BadgeGreenText
              )
              signal.whatOwnerShouldDo.forEach { act ->
                Row(
                  verticalAlignment = Alignment.Top,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Icon(Icons.Default.Check, contentDescription = null, tint = BadgeGreenText, modifier = Modifier.size(16.dp))
                  Text(text = act, style = MaterialTheme.typography.bodySmall, color = BadgeGreenText)
                }
              }
            }
          }

          // What NOT to do
          Card(
            colors = CardDefaults.cardColors(containerColor = BadgeRoseBg.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(12.dp)
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(
                text = "✕ What NOT to Do:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = BadgeRoseText
              )
              signal.whatNOTToDo.forEach { warn ->
                Row(
                  verticalAlignment = Alignment.Top,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Icon(Icons.Default.Close, contentDescription = null, tint = BadgeRoseText, modifier = Modifier.size(16.dp))
                  Text(text = warn, style = MaterialTheme.typography.bodySmall, color = BadgeRoseText)
                }
              }
            }
          }
        }
      }

      // Expand / Collapse Footer
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isExpanded) "Show Less" else "Tap for Actionable Dos & Don'ts",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.SemiBold
        )
        Icon(
          imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}
