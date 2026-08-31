package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PetEntity
import com.example.model.ArticleCategory
import com.example.model.EmotionalState
import com.example.model.GuideDifficulty
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.BadgeAmberBg
import com.example.ui.theme.BadgeAmberText
import com.example.ui.theme.BadgeBlueBg
import com.example.ui.theme.BadgeBlueText
import com.example.ui.theme.BadgeGreenBg
import com.example.ui.theme.BadgeGreenText
import com.example.ui.theme.BadgePurpleBg
import com.example.ui.theme.BadgePurpleText
import com.example.ui.theme.BadgeRoseBg
import com.example.ui.theme.BadgeRoseText
import com.example.ui.theme.TealPrimary

@Composable
fun CategoryChip(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null
) {
  val bg = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
  val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .clickable(onClick = onClick)
      .testTag("category_chip_$label"),
    color = bg,
    shape = RoundedCornerShape(20.dp),
    tonalElevation = if (isSelected) 4.dp else 0.dp
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          modifier = Modifier.size(16.dp),
          tint = contentColor
        )
      }
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        color = contentColor,
        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
      )
    }
  }
}

@Composable
fun DifficultyBadge(difficulty: GuideDifficulty, modifier: Modifier = Modifier) {
  val (bg, textColor) = when (difficulty) {
    GuideDifficulty.FOUNDATION -> BadgeGreenBg to BadgeGreenText
    GuideDifficulty.INTERMEDIATE -> BadgeBlueBg to BadgeBlueText
    GuideDifficulty.ADVANCED -> BadgePurpleBg to BadgePurpleText
    GuideDifficulty.BEHAVIOR_MOD -> BadgeAmberBg to BadgeAmberText
  }

  Box(
    modifier = modifier
      .clip(RoundedCornerShape(6.dp))
      .background(bg)
      .padding(horizontal = 8.dp, vertical = 3.dp)
  ) {
    Text(
      text = difficulty.label,
      style = MaterialTheme.typography.labelSmall,
      color = textColor,
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Composable
fun EmotionalStateBadge(state: EmotionalState, modifier: Modifier = Modifier) {
  val (bg, text) = when (state) {
    EmotionalState.CALM_CONTENT -> BadgeGreenBg to BadgeGreenText
    EmotionalState.PLAYFUL_EXCITED -> BadgeBlueBg to BadgeBlueText
    EmotionalState.ALERT_FOCUSED -> BadgeAmberBg to BadgeAmberText
    EmotionalState.ANXIOUS_STRESSED -> BadgeAmberBg to BadgeAmberText
    EmotionalState.DEFENSIVE_FEARFUL -> BadgeRoseBg to BadgeRoseText
    EmotionalState.OVERSTIMULATED -> BadgeRoseBg to BadgeRoseText
  }

  Row(
    modifier = modifier
      .clip(RoundedCornerShape(8.dp))
      .background(bg)
      .padding(horizontal = 10.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Text(text = state.icon, fontSize = 13.sp)
    Text(
      text = state.label,
      style = MaterialTheme.typography.labelMedium,
      color = text,
      fontWeight = FontWeight.SemiBold
    )
  }
}

@Composable
fun PsychologyCalloutCard(
  insight: String,
  modifier: Modifier = Modifier,
  title: String = "What's Happening in Their Brain"
) {
  Card(
    modifier = modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f)
    ),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Lightbulb,
          contentDescription = null,
          tint = AmberSecondary,
          modifier = Modifier.size(20.dp)
        )
        Text(
          text = title,
          style = MaterialTheme.typography.titleSmall,
          color = MaterialTheme.colorScheme.onSecondaryContainer,
          fontWeight = FontWeight.Bold
        )
      }
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = insight,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
        lineHeight = 21.sp
      )
    }
  }
}

val PetAvatarColors = listOf(
  Color(0xFF0F5B63),
  Color(0xFFD97736),
  Color(0xFF4A7C59),
  Color(0xFF6B4E71),
  Color(0xFFC05C46),
  Color(0xFF33658A)
)

val PetAvatarEmojis = listOf("🐕", "🐈", "🦮", "🐱", "🐶", "🐾")

@Composable
fun PetAvatarBadge(
  avatarIndex: Int,
  species: String,
  sizeDp: Int = 44,
  modifier: Modifier = Modifier
) {
  val safeIndex = (avatarIndex).coerceIn(0, PetAvatarEmojis.size - 1)
  val bg = PetAvatarColors[safeIndex % PetAvatarColors.size]
  val emoji = PetAvatarEmojis[safeIndex]

  Box(
    modifier = modifier
      .size(sizeDp.dp)
      .clip(CircleShape)
      .background(bg),
    contentAlignment = Alignment.Center
  ) {
    Text(
      text = emoji,
      fontSize = (sizeDp * 0.52).sp
    )
  }
}
