package com.example.ui.components

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
import androidx.compose.material.icons.filled.Warning
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
  onExitPanic: () -> Unit,
  modifier: Modifier = Modifier
) {
  val coroutineScope = rememberCoroutineScope()
  val messages = remember {
    mutableStateListOf(
      PanicMessage(
        sender = MessageSender.AI,
        text = "🚨 **Gemini AI Panic Assistant Active**. Take a deep breath. I am here with you. What is going on with your pet right now? Describe their symptoms, behavior, or tap the camera icon to show me what's happening."
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
        // Emergency Header
        Surface(
          color = Color(0xFFC62828), // Deep emergency red
          modifier = Modifier.fillMaxWidth()
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
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
              )
              Column {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "🚨 AI PANIC & CALM MODE",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                  )
                  Surface(
                    color = Color.White.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(6.dp)
                  ) {
                    Text(
                      text = "✨ Gemini 2.5",
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                      style = MaterialTheme.typography.labelSmall,
                      color = Color.White,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
                Text(
                  text = "Powered by Google Gemini AI • Real-time emergency support",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.White.copy(alpha = 0.85f)
                )
              }
            }

            Button(
              onClick = onExitPanic,
              colors = ButtonDefaults.buttonColors(containerColor = Color.White),
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("exit_panic_button")
            ) {
              Text(
                text = "Exit Panic Mode",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFC62828)
              )
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
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(messages) { msg ->
            val isAi = msg.sender == MessageSender.AI
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
            ) {
              Card(
                modifier = Modifier
                  .fillMaxWidth(0.85f)
                  .testTag(if (isAi) "ai_message" else "user_message"),
                shape = RoundedCornerShape(
                  topStart = 16.dp,
                  topEnd = 16.dp,
                  bottomStart = if (isAi) 4.dp else 16.dp,
                  bottomEnd = if (isAi) 16.dp else 4.dp
                ),
                colors = CardDefaults.cardColors(
                  containerColor = if (isAi) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primaryContainer
                )
              ) {
                Column(
                  modifier = Modifier.padding(14.dp),
                  verticalArrangement = Arrangement.spacedBy(6.dp)
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
                        text = if (isAi) "✨ Gemini AI" else "You",
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
                      shape = RoundedCornerShape(8.dp),
                      modifier = Modifier.fillMaxWidth()
                    ) {
                      Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                      ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                          text = "📷 Live Camera Feed / Back Posture Scan",
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
                    lineHeight = 20.sp
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
                  modifier = Modifier.fillMaxWidth(0.7f),
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    CircularProgressIndicator(
                      modifier = Modifier.size(18.dp),
                      strokeWidth = 2.dp,
                      color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                      text = "✨ Gemini AI is processing symptoms...",
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
              .padding(vertical = 8.dp)
          ) {
            Text(
              text = "⚡ Quick Symptom Reports:",
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
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { handleUserQuery(symptom) }
                    .testTag("symptom_chip_$symptom"),
                  color = MaterialTheme.colorScheme.secondaryContainer,
                  shape = RoundedCornerShape(16.dp)
                ) {
                  Text(
                    text = symptom,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
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
            // Camera Icon Button to show pet's back / posture
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
                contentDescription = "Show Pet Back via Camera",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }

            OutlinedTextField(
              value = inputText,
              onValueChange = { inputText = it },
              modifier = Modifier
                .weight(1f)
                .testTag("panic_input_field"),
              placeholder = { Text("Tell Gemini AI how pet is behaving...") },
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
                .background(Color(0xFFC62828))
                .testTag("panic_send_button")
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = Color.White
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
                text = "Live Camera: Pet Back & Posture",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
            }
            IconButton(onClick = { showCameraSimulation = false }) {
              Icon(Icons.Default.Close, contentDescription = "Close")
            }
          }

          // Simulated camera viewfinder preview box
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
                text = "📷 Viewfinder Active: Aim camera at pet's back or body",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Medium
              )
              Surface(
                color = Color.Red.copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "🔴 Gemini AI Vision Scan: Ready",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Text(
            text = "Point your camera at your pet's back, spine, or posture to let Gemini AI analyze physical tension and stress signals instantly.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Button(
            onClick = {
              showCameraSimulation = false
              handleUserQuery("Showed pet's back via camera feed. Analyzing posture and spine tension.", isCamera = true)
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(50.dp)
              .testTag("capture_back_button"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Capture & Analyze Pet's Back",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }
  }
}
