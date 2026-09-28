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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.ArticlesData
import com.example.model.Article
import com.example.model.ArticleCategory
import com.example.ui.components.ArticleFilterDialog
import com.example.ui.components.PetProfileSwitcher
import com.example.ui.components.ThemeToggleButton
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
import com.example.ui.theme.BrandGradient
import com.example.viewmodel.PetMindViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  viewModel: PetMindViewModel,
  onArticleClick: (String) -> Unit,
  modifier: Modifier = Modifier,
  @Suppress("UNUSED_PARAMETER") onOpenDecoder: () -> Unit = {},
  @Suppress("UNUSED_PARAMETER") onOpenTrain: () -> Unit = {},
) {
  val homeState by viewModel.homeState.collectAsStateWithLifecycle()
  val articles by viewModel.filteredArticles.collectAsStateWithLifecycle()
  val progressList by viewModel.articleProgressList.collectAsStateWithLifecycle()
  val progressMap = remember(progressList) { progressList.associateBy { it.articleId } }
  val activePet by viewModel.activePet.collectAsStateWithLifecycle()
  val allPets by viewModel.allPets.collectAsStateWithLifecycle()
  val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

  var showFilterDialog by remember { mutableStateOf(false) }

  if (showFilterDialog) {
    ArticleFilterDialog(
      allArticles = ArticlesData.articles,
      articleProgressList = progressList,
      currentCategories = homeState.selectedCategories,
      showBookmarksOnly = homeState.showBookmarksOnly,
      currentSpecies = homeState.selectedSpecies,
      activePet = activePet,
      onApply = { categories, species, showBookmarks ->
        viewModel.setArticleFilters(categories, species, showBookmarks)
      },
      onDismiss = { showFilterDialog = false }
    )
  }

  val dailyFact = remember {
    ArticlesData.dailyPetFacts.random()
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen_feed"),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Header Bar with Pet Avatar & Greeting
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
          Text(
            text = "PetMind",
            style = MaterialTheme.typography.headlineMedium.copy(
              brush = BrandGradient
            ),
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = if (activePet != null) "Mentality insights for ${activePet?.name}" else "Expert Pet Psychology & Mentality",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          ThemeToggleButton(
            themeMode = themeMode,
            onToggle = { viewModel.cycleThemeMode() }
          )
          PetProfileSwitcher(
            allPets = allPets,
            activePet = activePet,
            onPetSelected = { viewModel.selectPet(it) }
          )
        }
      }
    }

    // Hero Banner Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 6.dp)
          .clip(RoundedCornerShape(22.dp))
          .clickable { onArticleClick("art_body_language_canine") }
          .testTag("hero_article_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(160.dp)
          ) {
            Image(
              painter = painterResource(id = R.drawable.img_hero_pet_mentality),
              contentDescription = "Pet Mentality Hero",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
            Box(
              modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BrandGradient)
                .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
              Text(
                text = "FEATURED MENTALITY GUIDE",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "The Canine Emotional Spectrum: Reading Micro-Signals",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              maxLines = 2
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Learn how to detect calming signals, displacement gestures, and emotional thresholds before reactivity starts.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "By Dr. Elena Rostova • 5 min read",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
              )
              Text(
                text = "Read Guide →",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
              )
            }
          }
        }
      }
    }

    // Daily Pet Psychology Fact
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f)),
        shape = RoundedCornerShape(16.dp)
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.Top,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(CircleShape)
              .background(AmberSecondary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Lightbulb,
              contentDescription = null,
              tint = AmberSecondary,
              modifier = Modifier.size(20.dp)
            )
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Daily Behavior Fact: ${dailyFact.first}",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = dailyFact.second,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
              lineHeight = 18.sp
            )
          }
        }
      }
    }

    // Search Bar & Filter Button
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = homeState.searchQuery,
          onValueChange = { viewModel.setArticleSearch(it) },
          modifier = Modifier
            .weight(1f)
            .testTag("article_search_input"),
          placeholder = { Text("Search topics...") },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.outline)
          },
          trailingIcon = {
            if (homeState.searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.setArticleSearch("") }) {
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

        val activeFiltersCount = homeState.selectedCategories.size +
          (if (homeState.showBookmarksOnly) 1 else 0) +
          homeState.selectedSpecies.size

        IconButton(
          onClick = { showFilterDialog = true },
          modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .testTag("article_filter_button")
        ) {
          BadgedBox(
            badge = {
              if (activeFiltersCount > 0) {
                Badge(
                  containerColor = MaterialTheme.colorScheme.primary,
                  contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                  Text(activeFiltersCount.toString())
                }
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.FilterList,
              contentDescription = "Filter",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }

    // Article Feed Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (homeState.showBookmarksOnly) "Saved Articles (${articles.size})" else "Expert Articles (${articles.size})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Article Items
    if (articles.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
          shape = RoundedCornerShape(16.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.Pets, contentDescription = null, modifier = Modifier.size(36.dp), tint = MaterialTheme.colorScheme.outline)
            Text(
              text = "No articles found",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "Try clearing filters or searching another keyword.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    } else {
      items(articles, key = { it.id }) { article ->
        val progress = progressMap[article.id]
        val isBookmarked = progress?.isBookmarked == true
        val isRead = progress?.isRead == true

        ArticleCardItem(
          article = article,
          isBookmarked = isBookmarked,
          isRead = isRead,
          onCardClick = { onArticleClick(article.id) },
          onBookmarkToggle = { viewModel.toggleArticleBookmark(article.id) }
        )
      }
    }
  }
}

@Composable
fun ArticleCardItem(
  article: Article,
  isBookmarked: Boolean,
  isRead: Boolean,
  onCardClick: () -> Unit,
  onBookmarkToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  val imageRes = when (article.heroImageResName) {
    "img_hero_pet_mentality" -> R.drawable.img_hero_pet_mentality
    "img_dog_training" -> R.drawable.img_dog_training
    "img_cat_behavior" -> R.drawable.img_cat_behavior
    else -> R.drawable.img_hero_pet_mentality
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(18.dp))
      .clickable(onClick = onCardClick)
      .testTag("article_card_${article.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Thumbnail
      Box(
        modifier = Modifier
          .size(92.dp)
          .clip(RoundedCornerShape(14.dp))
      ) {
        Image(
          painter = painterResource(id = imageRes),
          contentDescription = article.title,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )
        if (isRead) {
          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(4.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.surface)
              .padding(2.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Read",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      // Content Details
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Category tag
          val (badgeBg, badgeText) = when (article.category) {
            ArticleCategory.PSYCHOLOGY -> BadgeBlueBg to BadgeBlueText
            ArticleCategory.COGNITION -> BadgePurpleBg to BadgePurpleText
            ArticleCategory.BEHAVIOR_SOLUTIONS -> BadgeRoseBg to BadgeRoseText
            ArticleCategory.ENRICHMENT -> BadgeAmberBg to BadgeAmberText
            ArticleCategory.BONDING -> BadgeGreenBg to BadgeGreenText
          }

          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .background(badgeBg)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = article.category.title,
              style = MaterialTheme.typography.labelSmall,
              color = badgeText,
              fontWeight = FontWeight.SemiBold
            )
          }

          IconButton(
            onClick = onBookmarkToggle,
            modifier = Modifier
              .size(28.dp)
              .testTag("bookmark_btn_${article.id}")
          ) {
            Icon(
              imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = if (isBookmarked) "Saved" else "Save",
              tint = if (isBookmarked) AmberSecondary else MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Text(
          text = article.title,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Text(
          text = article.summary,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.padding(top = 2.dp)
        ) {
          Text(
            text = "${article.readTimeMinutes} min read",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
          )
          Text(
            text = "•",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
          )
          Text(
            text = article.speciesTarget,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }
  }
}
