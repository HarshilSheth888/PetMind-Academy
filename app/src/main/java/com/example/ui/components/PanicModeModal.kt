package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import com.example.ui.screens.GeminiLiveScreen
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PetEntity
import com.example.ui.theme.BrandGradient
import com.example.util.GeminiPanicHelper
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class MessageSender {
  AI, USER
}

data class PanicMessage(
  val sender: MessageSender,
  val text: String,
  val timestamp: String = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()),
  val isCameraCapture: Boolean = false,
  val actionRoute: String? = null,
  val actionLabel: String? = null
)

@Composable
fun formatAiResponseText(text: String): AnnotatedString {
  return remember(text) {
    buildAnnotatedString {
      val lines = text.split("\n")
      lines.forEachIndexed { index, line ->
        if (index > 0) append("\n")
        val trimmed = line.trim()
        when {
          trimmed.startsWith("✨") || trimmed.contains("Protocol:") || trimmed.contains("What to Do Now:") ||
              trimmed.contains("Guidance") || trimmed.contains("Regarding your question:") ||
              trimmed.contains("Tailored Guidance") -> {
            pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
            append(line)
            pop()
          }
          trimmed.matches(Regex("^\\d+\\..*")) -> {
            val colonIndex = line.indexOf(':')
            if (colonIndex != -1) {
              val prefix = line.substring(0, colonIndex + 1)
              val rest = line.substring(colonIndex + 1)
              pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
              append(prefix)
              pop()
              append(rest)
            } else {
              pushStyle(SpanStyle(fontWeight = FontWeight.Bold))
              append(line)
              pop()
            }
          }
          else -> {
            append(line)
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanicModeModal(
  activePet: PetEntity?,
  onExitPanic: () -> Unit,
  modifier: Modifier = Modifier,
  onNavigateToRoute: ((String) -> Unit)? = null
) {
  val coroutineScope = rememberCoroutineScope()
  val petName = activePet?.name ?: "your pet"
  val petSpecies = activePet?.species ?: "pet"

  val initialGreeting = if (activePet != null) {
    "✨ Gemini AI Assistant Active for $petName ($petSpecies). Take a deep breath. I am here to listen, understand what $petName is going through, and help guide you step-by-step. What is happening right now?"
  } else {
    "✨ Gemini AI Assistant Active. Take a deep breath. I am here with you and your pet. What is going on right now? Describe their symptoms, behavior, or tap the camera icon to show me what's happening."
  }

  val messages = remember(activePet) {
    mutableStateListOf(
      PanicMessage(
        sender = MessageSender.AI,
        text = initialGreeting
      )
    )
  }

  var inputText by remember { mutableStateOf("") }
  var isGenerating by remember { mutableStateOf(false) }
  var showCameraSimulation by remember { mutableStateOf(false) }
  var showLiveVoiceScreen by remember { mutableStateOf(false) }
  val listState = rememberLazyListState()

  // Auto scroll to bottom on new message
  LaunchedEffect(messages.size, isGenerating) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  val quickSymptoms = listOf(
    "Trembling & Panting 🫨",
    "Hiding & Cowering 🐾",
    "Arching Back / Stiff 🐈",
    "Vomiting / Distress 🤢",
    "Excessive Barking / Whining 📢"
  )

  fun handleUserQuery(query: String, isCamera: Boolean = false) {
    if (query.isBlank() || isGenerating) return
    val userMsg = PanicMessage(sender = MessageSender.USER, text = query, isCameraCapture = isCamera)
    val historySnapshot = messages.toList()
    messages.add(userMsg)

    coroutineScope.launch {
      isGenerating = true
      val aiPayload = GeminiPanicHelper.generateCalmingResponse(
        userPrompt = query,
        conversationHistory = historySnapshot,
        pet = activePet,
        isCameraPostureScan = isCamera
      )
      isGenerating = false
      messages.add(
        PanicMessage(
          sender = MessageSender.AI,
          text = aiPayload.text,
          actionRoute = aiPayload.actionRoute,
          actionLabel = aiPayload.actionLabel
        )
      )
    }
  }

  Surface(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .testTag("panic_mode_modal"),
    color = MaterialTheme.colorScheme.background
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .navigationBarsPadding()
        .imePadding()
    ) {
      // Emergency & AI Header with Brand Gradient
      Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        shadowElevation = 4.dp
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(BrandGradient)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp),
              modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.Psychology,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(24.dp)
                )
              }
              Column(
                modifier = Modifier.weight(1f)
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "✨ Gemini AI Pet Companion",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                }
                Text(
                  text = activePet?.let { "Supporting ${it.name} (${it.breed.ifBlank { it.species }})" } ?: "Real-time pet nervous system co-regulation",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.White.copy(alpha = 0.9f),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                onClick = { showLiveVoiceScreen = true },
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.25f),
                modifier = Modifier.clip(CircleShape)
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Gemini Live Voice",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                  Text(
                    text = "Live",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                }
              }

              IconButton(
                onClick = onExitPanic,
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(Color.White.copy(alpha = 0.2f))
                  .testTag("exit_panic_button")
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Exit Panic Mode",
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
          }
        }
      }

      // Chat Conversation List
      LazyColumn(
        state = listState,
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        items(messages) { msg ->
          val isAi = msg.sender == MessageSender.AI
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
          ) {
            Card(
              modifier = Modifier
                .fillMaxWidth(0.88f)
                .testTag(if (isAi) "ai_message" else "user_message"),
              shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isAi) 4.dp else 18.dp,
                bottomEnd = if (isAi) 18.dp else 4.dp
              ),
              colors = CardDefaults.cardColors(
                containerColor = if (isAi) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
              ),
              border = if (isAi) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)) else null
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
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Icon(
                      imageVector = if (isAi) Icons.Default.Psychology else Icons.Default.Close,
                      contentDescription = null,
                      modifier = Modifier.size(16.dp),
                      tint = if (isAi) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                      text = if (isAi) "✨ Gemini AI Assistant" else "You",
                      style = MaterialTheme.typography.labelSmall,
                      fontWeight = FontWeight.Bold,
                      color = if (isAi) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                  }
                  Text(
                    text = msg.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.outline
                  )
                }

                if (msg.isCameraCapture) {
                  Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(10.dp),
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                      Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                      Text(
                        text = "📷 Live Posture & Spine Scan Captured",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                    }
                  }
                }

                Text(
                  text = if (isAi) formatAiResponseText(msg.text) else AnnotatedString(msg.text),
                  style = MaterialTheme.typography.bodyMedium,
                  color = if (isAi) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer,
                  lineHeight = 22.sp
                )

                // Render App Control Navigation Button if attached by AI
                if (msg.actionRoute != null && msg.actionLabel != null) {
                  Button(
                    onClick = {
                      onNavigateToRoute?.invoke(msg.actionRoute)
                      onExitPanic()
                    },
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(top = 8.dp)
                      .testTag("ai_action_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                  ) {
                    Text(
                      text = msg.actionLabel,
                      style = MaterialTheme.typography.labelLarge,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onPrimary
                    )
                  }
                }
              }
            }
          }
        }

        if (isGenerating) {
          item {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.Start
            ) {
              Card(
                modifier = Modifier.fillMaxWidth(0.75f),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
              ) {
                Row(
                  modifier = Modifier.padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.5.dp,
                    color = MaterialTheme.colorScheme.primary
                  )
                  Text(
                    text = "✨ Gemini AI is analyzing nervous system & behavioral state...",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }

      // Quick Symptom Chips
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
        ) {
          Text(
            text = "⚡ Quick Emergency Reports for $petName:",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
          )
          LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(quickSymptoms) { symptom ->
              Surface(
                modifier = Modifier
                  .clip(RoundedCornerShape(18.dp))
                  .clickable { handleUserQuery(symptom) }
                  .testTag("symptom_chip_$symptom"),
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(18.dp),
                tonalElevation = 2.dp
              ) {
                Text(
                  text = symptom,
                  modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.SemiBold,
                  color = MaterialTheme.colorScheme.onSecondaryContainer
                )
              }
            }
          }
        }
      }

      // Input & Camera Action Bar
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          IconButton(
            onClick = { showCameraSimulation = true },
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer)
              .testTag("panic_camera_button")
          ) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = "Scan Posture via Camera",
              tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
          }

          OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            modifier = Modifier
              .weight(1f)
              .testTag("panic_input_field"),
            placeholder = {
              Text(
                text = "Ask Gemini AI about your pet...",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
              unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              focusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            singleLine = true,
            maxLines = 1
          )

          IconButton(
            onClick = {
              if (inputText.isNotBlank()) {
                handleUserQuery(inputText)
                inputText = ""
              }
            },
            modifier = Modifier
              .size(48.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primary)
              .testTag("panic_send_button")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send",
              tint = MaterialTheme.colorScheme.onPrimary
            )
          }
        }
      }
    }
  }

  // Camera Simulation Dialog for showing pet's back & posture
  if (showCameraSimulation) {
    Dialog(onDismissRequest = { showCameraSimulation = false }) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
          .testTag("camera_simulation_dialog"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Text(
                text = "Live Camera: $petName Posture Scan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
            IconButton(onClick = { showCameraSimulation = false }) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(220.dp)
              .clip(RoundedCornerShape(16.dp))
              .background(Color.Black)
              .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(text = "🐕 / 🐈", fontSize = 48.sp)
              Text(
                text = "📷 Viewfinder Active: Aim camera at $petName's back or spine",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Medium
              )
              Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "✨ Gemini AI Vision Scan: Ready",
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onPrimary,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Text(
            text = "Point your camera at $petName's back, spine, or body posture to let Gemini AI analyze physical tension and stress signals instantly.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Button(
            onClick = {
              showCameraSimulation = false
              handleUserQuery("Showed $petName's back via camera feed. Analyzing posture and spine tension.", isCamera = true)
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("capture_back_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Capture & Analyze Posture",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimary
            )
          }
        }
      }
    }
  }

  if (showLiveVoiceScreen) {
    Dialog(
      onDismissRequest = { showLiveVoiceScreen = false },
      properties = androidx.compose.ui.window.DialogProperties(
        usePlatformDefaultWidth = false
      )
    ) {
      GeminiLiveScreen(
        onCloseScreen = { showLiveVoiceScreen = false }
      )
    }
  }
}
