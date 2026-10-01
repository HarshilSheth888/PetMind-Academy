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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
  val isCameraCapture: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanicModeModal(
  activePet: PetEntity?,
  onExitPanic: () -> Unit,
  modifier: Modifier = Modifier
) {
  val coroutineScope = rememberCoroutineScope()
  val petName = activePet?.name ?: "your pet"
  val messages = remember {
    mutableStateListOf(
      PanicMessage(
        sender = MessageSender.AI,
        text = "🚨 **Gemini AI Panic & Calm Assistant Active** for $petName. Take a deep breath. I am with you and $petName. What is going on right now? Describe their symptoms, behavior, or tap the camera icon to show me what's happening."
      )
    )
  }

  var inputText by remember { mutableStateOf("") }
  var isGenerating by remember { mutableStateOf(false) }
  var showCameraSimulation by remember { mutableStateOf(false) }
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
    messages.add(PanicMessage(sender = MessageSender.USER, text = query, isCameraCapture = isCamera))

    coroutineScope.launch {
      isGenerating = true
      val aiResponse = GeminiPanicHelper.generateCalmingResponse(
        userPrompt = query,
        isCameraPostureScan = isCamera
      )
      isGenerating = false
      messages.add(PanicMessage(sender = MessageSender.AI, text = aiResponse))
    }
  }

  Dialog(
    onDismissRequest = { /* Prevent accidental dismissal during panic mode */ },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .testTag("panic_mode_modal"),
      color = MaterialTheme.colorScheme.background
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
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
                Column {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                  ) {
                    Text(
                      text = "✨ Gemini AI Calm & Emergency",
                      style = MaterialTheme.typography.titleMedium,
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )
                  }
                  Text(
                    text = activePet?.let { "Supporting ${it.name} (${it.breed.ifBlank { it.species }})" } ?: "Real-time pet nervous system co-regulation",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.9f)
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
                    text = msg.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isAi) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer,
                    lineHeight = 22.sp
                  )
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
              placeholder = { Text("Tell Gemini AI how $petName is behaving...") },
              shape = RoundedCornerShape(24.dp),
              colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surface
              ),
              singleLine = true
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
}
