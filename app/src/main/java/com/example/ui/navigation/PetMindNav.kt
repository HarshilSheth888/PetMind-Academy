package com.example.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.AutoGraph
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.ActiveTrainingSessionScreen
import com.example.ui.screens.ArticleDetailScreen
import com.example.ui.screens.BehaviorDecoderScreen
import com.example.ui.screens.GuideDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.TrainingScreen
import com.example.viewmodel.PetMindViewModel

sealed class Screen(
  val route: String,
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
) {
  object Learn : Screen("learn", "Learn", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook)
  object Training : Screen("training", "Training", Icons.Default.FitnessCenter, Icons.Outlined.FitnessCenter)
  object Decoder : Screen("decoder", "Decoder", Icons.Default.Psychology, Icons.Outlined.Psychology)
  object Progress : Screen("progress", "Progress", Icons.Default.AutoGraph, Icons.Outlined.AutoGraph)

  object ArticleDetail : Screen("article/{articleId}", "Article", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook) {
    fun createRoute(articleId: String) = "article/$articleId"
  }

  object GuideDetail : Screen("guide/{guideId}", "Guide", Icons.Default.FitnessCenter, Icons.Outlined.FitnessCenter) {
    fun createRoute(guideId: String) = "guide/$guideId"
  }

  object ActiveSession : Screen("session/{guideId}", "Practice", Icons.Default.FitnessCenter, Icons.Outlined.FitnessCenter) {
    fun createRoute(guideId: String) = "session/$guideId"
  }
}

val BottomNavItems = listOf(
  Screen.Learn,
  Screen.Training,
  Screen.Decoder,
  Screen.Progress
)

@Composable
fun PetMindAppScaffold(
  viewModel: PetMindViewModel,
  modifier: Modifier = Modifier
) {
  val navController = rememberNavController()
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  val isBottomBarVisible = BottomNavItems.any { it.route == currentRoute }

  Scaffold(
    bottomBar = {
      if (isBottomBarVisible) {
        NavigationBar(modifier = Modifier.testTag("bottom_nav_bar")) {
          BottomNavItems.forEach { screen ->
            val isSelected = currentRoute == screen.route
            NavigationBarItem(
              icon = {
                Icon(
                  imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                  contentDescription = screen.label
                )
              },
              label = { Text(screen.label) },
              selected = isSelected,
              onClick = {
                navController.navigate(screen.route) {
                  popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                  }
                  launchSingleTop = true
                  restoreState = true
                }
              },
              modifier = Modifier.testTag("nav_item_${screen.route}")
            )
          }
        }
      }
    },
    modifier = modifier.fillMaxSize()
  ) { innerPadding ->
    NavHost(
      navController = navController,
      startDestination = Screen.Learn.route,
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // 1. Learn Screen (Articles)
      composable(Screen.Learn.route) {
        HomeScreen(
          viewModel = viewModel,
          onArticleClick = { articleId ->
            navController.navigate(Screen.ArticleDetail.createRoute(articleId))
          },
          onOpenDecoder = {
            navController.navigate(Screen.Decoder.route)
          },
          onOpenTrain = {
            navController.navigate(Screen.Training.route)
          }
        )
      }

      // 2. Training Guides Catalog
      composable(Screen.Training.route) {
        TrainingScreen(
          viewModel = viewModel,
          onGuideClick = { guideId ->
            navController.navigate(Screen.GuideDetail.createRoute(guideId))
          },
          onStartSession = { guideId ->
            navController.navigate(Screen.ActiveSession.createRoute(guideId))
          }
        )
      }

      // 3. Behavior Decoder Screen
      composable(Screen.Decoder.route) {
        BehaviorDecoderScreen(viewModel = viewModel)
      }

      // 4. Progress & Milestones Screen
      composable(Screen.Progress.route) {
        ProgressScreen(
          viewModel = viewModel,
          onNavigateToGuide = { guideId ->
            navController.navigate(Screen.GuideDetail.createRoute(guideId))
          }
        )
      }

      // 5. Article Detail Screen
      composable(
        route = Screen.ArticleDetail.route,
        arguments = listOf(navArgument("articleId") { type = NavType.StringType })
      ) { backStackEntry ->
        val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
        ArticleDetailScreen(
          articleId = articleId,
          viewModel = viewModel,
          onNavigateBack = { navController.popBackStack() }
        )
      }

      // 6. Guide Detail Screen
      composable(
        route = Screen.GuideDetail.route,
        arguments = listOf(navArgument("guideId") { type = NavType.StringType })
      ) { backStackEntry ->
        val guideId = backStackEntry.arguments?.getString("guideId") ?: ""
        GuideDetailScreen(
          guideId = guideId,
          viewModel = viewModel,
          onNavigateBack = { navController.popBackStack() },
          onStartSession = { gId ->
            navController.navigate(Screen.ActiveSession.createRoute(gId))
          }
        )
      }

      // 7. Active Training Session Companion
      composable(
        route = Screen.ActiveSession.route,
        arguments = listOf(navArgument("guideId") { type = NavType.StringType })
      ) { backStackEntry ->
        val guideId = backStackEntry.arguments?.getString("guideId") ?: ""
        ActiveTrainingSessionScreen(
          guideId = guideId,
          viewModel = viewModel,
          onNavigateBack = { navController.popBackStack() }
        )
      }
    }
  }
}
