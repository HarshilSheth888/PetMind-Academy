package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.PetMindAppScaffold
import com.example.ui.theme.PetMindTheme
import com.example.viewmodel.PetMindViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val petMindViewModel: PetMindViewModel = viewModel()
      val themeMode by petMindViewModel.themeMode.collectAsStateWithLifecycle()

      PetMindTheme(themeMode = themeMode) {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = MaterialTheme.colorScheme.background,
        ) {
          PetMindAppScaffold(viewModel = petMindViewModel)
        }
      }
    }
  }
}

