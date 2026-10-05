package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewmodel.GeminiLiveViewModel
import com.example.viewmodel.LiveConversationState

/**
 * Minimalist, immersive Jetpack Compose screen for real-time Gemini Live voice-to-voice conversation.
 */
@Composable
fun GeminiLiveScreen(
  onCloseScreen: () -> Unit = {},
  liveViewModel: GeminiLiveViewModel = viewModel()
) {
  val context = LocalContext.current
  var hasAudioPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasAudioPermission = isGranted
  }

  val uiState by liveViewModel.uiState.collectAsStateWithLifecycle()
  val audioLevel by liveViewModel.audioLevel.collectAsStateWithLifecycle()
  val isMuted by liveViewModel.isMuted.collectAsStateWithLifecycle()
  val errorMessage by liveViewModel.errorMessage.collectAsStateWithLifecycle()

  val lifecycleOwner = LocalLifecycleOwner.current

  // Auto connect when audio permission granted
  LaunchedEffect(hasAudioPermission) {
    if (hasAudioPermission && uiState == LiveConversationState.IDLE) {
      liveViewModel.startConversation()
    }
  }

  // Lifecycle handling - cleanly stop audio and WebSocket on pause or destroy
  DisposableEffect(lifecycleOwner) {
    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_STOP || event == Lifecycle.Event.ON_DESTROY) {
        liveViewModel.endConversation()
      }
    }
    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {
      lifecycleOwner.lifecycle.removeObserver(observer)
      liveViewModel.endConversation()
    }
  }

  Surface(
    modifier = Modifier.fillMaxSize(),
    color = Color(0xFF0F111A) // Deep space midnight navy
  ) {
    Box(
      modifier = Modifier.fillMaxSize()
    ) {
      // Background subtle gradient glow
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.radialGradient(
              colors = listOf(
                getPrimaryStateColor(uiState).copy(alpha = 0.22f),
                Color.Transparent
              ),
              radius = 1200f
            )
          )
      )

      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top Bar Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Gemini Live",
              tint = Color(0xFF8AB4F8),
              modifier = Modifier.size(24.dp)
            )
            Text(
              text = "Gemini Live",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }

          IconButton(
            onClick = {
              liveViewModel.endConversation()
              onCloseScreen()
            },
            colors = IconButtonDefaults.iconButtonColors(
              containerColor = Color.White.copy(alpha = 0.12f),
              contentColor = Color.White
            )
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close"
            )
          }
        }

        // Center Audio Visualizer Element
        Box(
          modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
          contentAlignment = Alignment.Center
        ) {
          if (!hasAudioPermission) {
            AudioPermissionCard(
              onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
              }
            )
          } else {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              CentralAudioVisualizer(
                state = uiState,
                audioLevel = audioLevel
              )

              Spacer(modifier = Modifier.height(48.dp))

              // Status Indicator Text
              Text(
                text = getStatusMessage(uiState, isMuted),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center
              )

              if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = errorMessage ?: "",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color(0xFFFF6B6B),
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(horizontal = 16.dp)
                )
              }
            }
          }
        }

        // Bottom Control Actions
        if (hasAudioPermission) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceEvenly,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Mute Microphone Toggle Button
              IconButton(
                onClick = { liveViewModel.toggleMute() },
                enabled = uiState != LiveConversationState.IDLE && uiState != LiveConversationState.ERROR,
                modifier = Modifier
                  .size(56.dp)
                  .clip(CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                  containerColor = if (isMuted) Color(0xFFFF4D4D) else Color.White.copy(alpha = 0.15f),
                  contentColor = Color.White,
                  disabledContainerColor = Color.White.copy(alpha = 0.05f)
                )
              ) {
                Icon(
                  imageVector = if (isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                  contentDescription = if (isMuted) "Unmute" else "Mute",
                  modifier = Modifier.size(26.dp)
                )
              }

              // End Conversation Button (Red Accent)
              if (uiState != LiveConversationState.IDLE) {
                FilledIconButton(
                  onClick = { liveViewModel.endConversation() },
                  modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                  colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFFE53935),
                    contentColor = Color.White
                  )
                ) {
                  Icon(
                    imageVector = Icons.Default.CallEnd,
                    contentDescription = "End Conversation",
                    modifier = Modifier.size(32.dp)
                  )
                }
              } else {
                // Reconnect / Start Button
                FilledIconButton(
                  onClick = { liveViewModel.startConversation() },
                  modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
                  colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = Color(0xFF4285F4),
                    contentColor = Color.White
                  )
                ) {
                  Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Start Conversation",
                    modifier = Modifier.size(32.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
              text = if (uiState == LiveConversationState.IDLE) "Tap start to begin speaking with Gemini" else "Tap red button to end conversation",
              style = MaterialTheme.typography.labelMedium,
              color = Color.White.copy(alpha = 0.6f)
            )
          }
        }
      }
    }
  }
}

/**
 * Central animated element reacting dynamically to audio input/output and state.
 */
@Composable
private fun CentralAudioVisualizer(
  state: LiveConversationState,
  audioLevel: Float
) {
  val infiniteTransition = rememberInfiniteTransition(label = "visualizer_infinite")

  // Continuous subtle pulse
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 0.95f,
    targetValue = 1.05f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_scale"
  )

  // Rotating angle for thinking state
  val rotationAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(2000, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rotation_angle"
  )

  val animatedAudioScale by animateFloatAsState(
    targetValue = 1f + (audioLevel * 0.85f).coerceIn(0f, 1.2f),
    animationSpec = tween(100),
    label = "audio_scale"
  )

  val primaryColor by animateColorAsState(
    targetValue = getPrimaryStateColor(state),
    animationSpec = tween(500),
    label = "primary_color"
  )

  val visualizerSize = 180.dp

  Box(
    modifier = Modifier.size(visualizerSize + 80.dp),
    contentAlignment = Alignment.Center
  ) {
    // Outer Expanding Wave Ripples (active during listening / speaking)
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .rotate(if (state == LiveConversationState.THINKING) rotationAngle else 0f)
    ) {
      val center = this.center
      val baseRadius = (visualizerSize.toPx() / 2f) * animatedAudioScale * pulseScale

      // Ripple Ring 1
      drawCircle(
        color = primaryColor.copy(alpha = (0.25f * (1f - (audioLevel * 0.3f))).coerceIn(0.05f, 0.4f)),
        radius = baseRadius * 1.35f,
        center = center,
        style = Stroke(width = 3.dp.toPx())
      )

      // Ripple Ring 2
      drawCircle(
        color = primaryColor.copy(alpha = (0.15f * (1f - (audioLevel * 0.2f))).coerceIn(0.03f, 0.3f)),
        radius = baseRadius * 1.65f,
        center = center,
        style = Stroke(width = 2.dp.toPx())
      )
    }

    // Inner Glowing Core Circle
    Box(
      modifier = Modifier
        .size(visualizerSize)
        .clip(CircleShape)
        .background(
          Brush.radialGradient(
            colors = listOf(
              primaryColor,
              primaryColor.copy(alpha = 0.6f),
              primaryColor.copy(alpha = 0.15f)
            )
          )
        ),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .size(visualizerSize * 0.6f)
          .clip(CircleShape)
          .background(Color.White.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(48.dp)
        )
      }
    }
  }
}

@Composable
private fun AudioPermissionCard(
  onRequestPermission: () -> Unit
) {
  Card(
    colors = CardDefaults.cardColors(
      containerColor = Color.White.copy(alpha = 0.08f)
    ),
    shape = RoundedCornerShape(24.dp),
    modifier = Modifier.padding(24.dp)
  ) {
    Column(
      modifier = Modifier.padding(24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Mic,
        contentDescription = "Microphone",
        tint = Color(0xFF8AB4F8),
        modifier = Modifier.size(48.dp)
      )

      Text(
        text = "Microphone Permission Required",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        textAlign = TextAlign.Center
      )

      Text(
        text = "Gemini Live requires microphone access to have a continuous voice-to-voice conversation with you.",
        style = MaterialTheme.typography.bodyMedium,
        color = Color.White.copy(alpha = 0.8f),
        textAlign = TextAlign.Center
      )

      Button(
        onClick = onRequestPermission,
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF4285F4),
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Grant Permission")
      }
    }
  }
}

private fun getPrimaryStateColor(state: LiveConversationState): Color {
  return when (state) {
    LiveConversationState.IDLE -> Color(0xFF5F6368) // Slate Gray
    LiveConversationState.CONNECTING -> Color(0xFFFFB74D) // Warm Amber
    LiveConversationState.LISTENING -> Color(0xFF4285F4) // Google Blue / Cyan
    LiveConversationState.THINKING -> Color(0xFFA142F4) // Deep Magenta / Violet
    LiveConversationState.SPEAKING -> Color(0xFF34A853) // Emerald Green
    LiveConversationState.ERROR -> Color(0xFFEA4335) // Coral Red
  }
}

private fun getStatusMessage(state: LiveConversationState, isMuted: Boolean): String {
  if (isMuted && state == LiveConversationState.LISTENING) {
    return "Microphone Muted"
  }
  return when (state) {
    LiveConversationState.IDLE -> "Gemini Live Idle"
    LiveConversationState.CONNECTING -> "Connecting to Gemini..."
    LiveConversationState.LISTENING -> "Listening..."
    LiveConversationState.THINKING -> "Gemini is thinking..."
    LiveConversationState.SPEAKING -> "Gemini is speaking..."
    LiveConversationState.ERROR -> "Connection Error"
  }
}
