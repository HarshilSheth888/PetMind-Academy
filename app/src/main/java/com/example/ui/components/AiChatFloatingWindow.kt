package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewmodel.ChatMessageItem
import com.example.viewmodel.ChatMessageSender
import com.example.viewmodel.ChatUiState
import com.example.viewmodel.ChatViewModel
import kotlin.math.roundToInt

@Composable
fun AiChatFloatingWindow(
  modifier: Modifier = Modifier,
  chatViewModel: ChatViewModel = viewModel(),
) {
  var isChatOpen by remember { mutableStateOf(false) }

  // Drag offset for floating action button
  var offsetX by remember { mutableFloatStateOf(0f) }
  var offsetY by remember { mutableFloatStateOf(0f) }

  Box(modifier = modifier.fillMaxSize()) {
    // Draggable & Accessible Floating Action Button
    AnimatedVisibility(
      visible = !isChatOpen,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
        .padding(16.dp)
        .pointerInput(Unit) {
          detectDragGestures { change, dragAmount ->
            change.consume()
            offsetX += dragAmount.x
            offsetY += dragAmount.y
          }
        }
    ) {
      FloatingActionButton(
        onClick = { isChatOpen = true },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = Modifier
          .size(56.dp)
          .semantics { contentDescription = "Open Pawsy Floating AI Pet Companion Chat" }
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = "Pawsy Chat Icon",
          modifier = Modifier.size(28.dp)
        )
      }
    }

    // Full Screen Chat Overlay
    AnimatedVisibility(
      visible = isChatOpen,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.fillMaxSize()
    ) {
      Surface(
        modifier = Modifier
          .fillMaxSize()
          .background(MaterialTheme.colorScheme.background)
          .navigationBarsPadding()
          .imePadding(),
        color = MaterialTheme.colorScheme.background
      ) {
        Column(modifier = Modifier.fillMaxSize()) {
          // Header
          ChatHeader { isChatOpen = false }

          // Chat Conversation History
          ChatMessageList(
            viewModel = chatViewModel,
            modifier = Modifier.weight(1f)
          )

          // Input & Action Bar
          ChatInputField(viewModel = chatViewModel)
        }
      }
    }
  }
}

@Composable
private fun ChatHeader(onClose: () -> Unit) {
  Surface(
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 4.dp
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
        Column {
          Text(
            text = "Pawsy Pet Companion",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Firebase AI Logic • Active",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      IconButton(
        onClick = onClose,
        modifier = Modifier.semantics { contentDescription = "Close AI Chat Overlay" }
      ) {
        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Chat")
      }
    }
  }
}

@Composable
private fun ChatMessageList(
  viewModel: ChatViewModel,
  modifier: Modifier = Modifier
) {
  val messages by viewModel.messages.collectAsStateWithLifecycle()
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val listState = rememberLazyListState()

  // Auto-scroll to latest message
  LaunchedEffect(messages.size, uiState) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  LazyColumn(
    state = listState,
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    items(messages, key = { it.id }) { message ->
      ChatMessageBubble(message = message)
    }

    // Thinking Indicator
    if (uiState is ChatUiState.Thinking) {
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          horizontalArrangement = Arrangement.Start,
          verticalAlignment = Alignment.CenterVertically
        ) {
          ElevatedCard(shape = RoundedCornerShape(18.dp)) {
            Row(
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                strokeWidth = 2.dp
              )
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = "Thinking...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.width(12.dp))
              IconButton(
                onClick = { viewModel.cancelCurrentGeneration() },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Stop,
                  contentDescription = "Stop Response Generation",
                  tint = MaterialTheme.colorScheme.error
                )
              }
            }
          }
        }
      }
    }

    // Streaming State indicator bar
    if (uiState is ChatUiState.Streaming) {
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.Start
        ) {
          Text(
            text = "⚡ Streaming response...",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.width(8.dp))
          IconButton(
            onClick = { viewModel.cancelCurrentGeneration() },
            modifier = Modifier.size(20.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Stop,
              contentDescription = "Stop Response",
              tint = MaterialTheme.colorScheme.error
            )
          }
        }
      }
    }

    // Error State with Retry option
    if (uiState is ChatUiState.Error) {
      val errorState = uiState as ChatUiState.Error
      item {
        ElevatedCard(
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = errorState.message,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.error
            )
            if (errorState.isRetryable) {
              Spacer(modifier = Modifier.size(8.dp))
              Button(
                onClick = { viewModel.retryLastMessage() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
              ) {
                Icon(
                  imageVector = Icons.Default.Refresh,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Retry",
                  color = MaterialTheme.colorScheme.onErrorContainer
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun ChatMessageBubble(message: ChatMessageItem) {
  val isUser = message.sender == ChatMessageSender.USER

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
  ) {
    Surface(
      shape = RoundedCornerShape(
        topStart = 18.dp,
        topEnd = 18.dp,
        bottomStart = if (isUser) 18.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 18.dp
      ),
      color = if (isUser) {
        MaterialTheme.colorScheme.primaryContainer
      } else {
        MaterialTheme.colorScheme.secondaryContainer
      },
      modifier = Modifier.widthIn(max = 280.dp)
    ) {
      Text(
        text = message.text,
        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = if (isUser) {
          MaterialTheme.colorScheme.onPrimaryContainer
        } else {
          MaterialTheme.colorScheme.onSecondaryContainer
        }
      )
    }
  }
}

@Composable
private fun ChatInputField(viewModel: ChatViewModel) {
  var inputText by remember { mutableStateOf("") }
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val isBusy = uiState is ChatUiState.Thinking || uiState is ChatUiState.Streaming

  Surface(
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
      OutlinedTextField(
        value = inputText,
        onValueChange = { inputText = it },
        modifier = Modifier.weight(1f),
        placeholder = { Text("Ask about your pet...") },
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
          unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
          focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        singleLine = true,
        maxLines = 1,
        enabled = !isBusy
      )

      if (isBusy) {
        IconButton(
          onClick = { viewModel.cancelCurrentGeneration() },
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.errorContainer)
            .semantics { contentDescription = "Stop AI generation" }
        ) {
          Icon(
            imageVector = Icons.Default.Stop,
            contentDescription = "Stop Generation",
            tint = MaterialTheme.colorScheme.onErrorContainer
          )
        }
      } else {
        IconButton(
          onClick = {
            if (inputText.isNotBlank()) {
              viewModel.sendMessage(inputText)
              inputText = ""
            }
          },
          enabled = inputText.isNotBlank(),
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
              if (inputText.isNotBlank()) {
                MaterialTheme.colorScheme.primary
              } else {
                MaterialTheme.colorScheme.surfaceVariant
              }
            )
            .semantics { contentDescription = "Send Message to AI Companion" }
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send",
            tint = if (inputText.isNotBlank()) {
              MaterialTheme.colorScheme.onPrimary
            } else {
              Color.Gray
            }
          )
        }
      }
    }
  }
}
