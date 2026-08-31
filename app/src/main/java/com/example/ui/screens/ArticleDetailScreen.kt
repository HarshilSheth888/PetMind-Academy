package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.ArticlesData
import com.example.model.QuizQuestion
import com.example.ui.components.PsychologyCalloutCard
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.BadgeGreenBg
import com.example.ui.theme.BadgeGreenText
import com.example.ui.theme.BadgeRoseBg
import com.example.ui.theme.BadgeRoseText
import com.example.ui.theme.TealPrimary
import com.example.viewmodel.PetMindViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
  articleId: String,
  viewModel: PetMindViewModel,
  onNavigateBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val article = remember(articleId) {
    ArticlesData.articles.firstOrNull { it.id == articleId } ?: ArticlesData.articles.first()
  }

  val progressList by viewModel.articleProgressList.collectAsStateWithLifecycle()
  val progress = progressList.firstOrNull { it.articleId == article.id }
  val isBookmarked = progress?.isBookmarked == true
  val isRead = progress?.isRead == true

  val selectedAnswers = remember { mutableStateMapOf<Int, Int>() }
  var quizSubmitted by remember { mutableStateOf(false) }

  val imageRes = when (article.heroImageResName) {
    "img_hero_pet_mentality" -> R.drawable.img_hero_pet_mentality
    "img_dog_training" -> R.drawable.img_dog_training
    "img_cat_behavior" -> R.drawable.img_cat_behavior
    else -> R.drawable.img_hero_pet_mentality
  }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = article.category.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        },
        navigationIcon = {
          IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.testTag("article_back_btn")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          IconButton(
            onClick = { viewModel.toggleArticleBookmark(article.id) },
            modifier = Modifier.testTag("detail_bookmark_btn")
          ) {
            Icon(
              imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
              contentDescription = if (isBookmarked) "Saved" else "Save",
              tint = if (isBookmarked) AmberSecondary else MaterialTheme.colorScheme.onSurface
            )
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      )
    },
    modifier = modifier.testTag("article_detail_screen")
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      contentPadding = PaddingValues(bottom = 60.dp)
    ) {
      // Hero Image
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
        ) {
          Image(
            painter = painterResource(id = imageRes),
            contentDescription = article.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
          Box(
            modifier = Modifier
              .align(Alignment.BottomEnd)
              .padding(12.dp)
              .clip(RoundedCornerShape(8.dp))
              .background(Color.Black.copy(alpha = 0.65f))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = "${article.readTimeMinutes} min read • Target: ${article.speciesTarget}",
              style = MaterialTheme.typography.labelSmall,
              color = Color.White
            )
          }
        }
      }

      // Title & Author Meta
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = article.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            lineHeight = 32.sp
          )
          Text(
            text = article.subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
          )

          HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

          // Author Card
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(22.dp)
              )
            }
            Column {
              Text(
                text = article.authorName,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = article.authorCredentials,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Core Psychology Mechanism Callout
      item {
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
          PsychologyCalloutCard(
            insight = article.corePsychologyInsight,
            title = "The Neuropsychology Principle"
          )
        }
      }

      // Article Body Sections
      items(article.sections.size) { index ->
        val section = article.sections[index]
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = section.heading,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = section.body,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 24.sp,
            color = MaterialTheme.colorScheme.onBackground
          )

          if (section.takeaway != null) {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
              shape = RoundedCornerShape(10.dp)
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                Text(text = "💡", fontSize = 14.sp)
                Text(
                  text = "Key Takeaway: ${section.takeaway}",
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }

      // Knowledge Check Interactive Quiz
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(Icons.Default.Quiz, contentDescription = null, tint = AmberSecondary)
            Text(
              text = "Interactive Knowledge Check",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }

          article.quiz.forEachIndexed { qIndex, question ->
            QuizQuestionItem(
              questionIndex = qIndex,
              quiz = question,
              selectedOption = selectedAnswers[qIndex],
              isSubmitted = quizSubmitted,
              onOptionSelected = { optIndex ->
                if (!quizSubmitted) {
                  selectedAnswers[qIndex] = optIndex
                }
              }
            )
          }

          if (!quizSubmitted && selectedAnswers.size == article.quiz.size) {
            Button(
              onClick = {
                quizSubmitted = true
                val score = selectedAnswers.count { (qIdx, ans) ->
                  article.quiz.getOrNull(qIdx)?.correctOptionIndex == ans
                }
                viewModel.markArticleRead(article.id, score)
              },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("submit_quiz_button")
            ) {
              Text("Check Answers & Mark Read")
            }
          } else if (quizSubmitted) {
            val score = selectedAnswers.count { (qIdx, ans) ->
              article.quiz.getOrNull(qIdx)?.correctOptionIndex == ans
            }
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
              shape = RoundedCornerShape(14.dp)
            ) {
              Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.size(28.dp)
                )
                Column {
                  Text(
                    text = "Article Completed! Score: $score / ${article.quiz.size}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                  Text(
                    text = "Your progress has been recorded in your development log.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                }
              }
            }
          } else if (!isRead) {
            Button(
              onClick = { viewModel.markArticleRead(article.id, 0) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("mark_read_button")
            ) {
              Text("Mark as Read")
            }
          }
        }
      }
    }
  }
}

@Composable
fun QuizQuestionItem(
  questionIndex: Int,
  quiz: QuizQuestion,
  selectedOption: Int?,
  isSubmitted: Boolean,
  onOptionSelected: (Int) -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Text(
        text = "Q${questionIndex + 1}: ${quiz.question}",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold
      )

      quiz.options.forEachIndexed { optIndex, optionText ->
        val isSelected = selectedOption == optIndex
        val isCorrect = optIndex == quiz.correctOptionIndex

        val (bgColor, borderColor, textColor) = when {
          !isSubmitted && isSelected -> Triple(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onPrimaryContainer)
          isSubmitted && isCorrect -> Triple(BadgeGreenBg, Color(0xFF137333), BadgeGreenText)
          isSubmitted && isSelected && !isCorrect -> Triple(BadgeRoseBg, Color(0xFFC5221F), BadgeRoseText)
          else -> Triple(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), Color.Transparent, MaterialTheme.colorScheme.onSurface)
        }

        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(enabled = !isSubmitted) { onOptionSelected(optIndex) }
            .testTag("quiz_opt_${questionIndex}_$optIndex"),
          color = bgColor,
          shape = RoundedCornerShape(10.dp)
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Box(
              modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (isSelected || (isSubmitted && isCorrect)) borderColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
              contentAlignment = Alignment.Center
            ) {
              if (isSubmitted && isCorrect) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
              } else if (isSubmitted && isSelected && !isCorrect) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
              }
            }
            Text(
              text = optionText,
              style = MaterialTheme.typography.bodySmall,
              color = textColor,
              fontWeight = if (isSelected || (isSubmitted && isCorrect)) FontWeight.SemiBold else FontWeight.Normal
            )
          }
        }
      }

      if (isSubmitted) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(10.dp)
        ) {
          Text(
            text = "Explanation: ${quiz.explanation}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
