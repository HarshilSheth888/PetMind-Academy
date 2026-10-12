package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewmodel.ChatMessageItem
import com.example.viewmodel.ChatMessageSender
import com.example.viewmodel.ChatUiState
import com.example.viewmodel.ChatViewModel

@Composable
fun AiChatFloatingWindow(
  modifier: Modifier = Modifier,
  chatViewModel: ChatViewModel = viewModel(),
) {
  var isChatOpen by remember { mutableStateOf(value = false) }

  Box(modifier = modifier.fillMaxSize()) {
    // Floating Action Button
    AnimatedVisibility(
      visible = !isChatOpen,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
    ) {
      FloatingActionButton(
        onClick = { isChatOpen = true },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
      ) {
        Icon(
          imageVector = Icons.Default.AutoAwesome,
          contentDescription = "Open AI Chat"
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
            text = "AI Chat Assistant",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Online",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      IconButton(onClick = onClose) {
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

  // Auto scroll to latest message
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

    if (uiState is ChatUiState.Loading) {
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          horizontalArrangement = Arrangement.Start
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
  val isLoading = uiState is ChatUiState.Loading

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
        placeholder = { Text("Type a message...") },
        shape = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
          unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
          focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        singleLine = true,
        maxLines = 1,
        enabled = !isLoading
      )

      IconButton(
        onClick = {
          if (inputText.isNotBlank()) {
            viewModel.sendMessage(inputText)
            inputText = ""
          }
        },
        enabled = inputText.isNotBlank() && !isLoading,
        modifier = Modifier
          .size(48.dp)
          .clip(CircleShape)
          .background(
            if (inputText.isNotBlank() && !isLoading) {
              MaterialTheme.colorScheme.primary
            } else {
              MaterialTheme.colorScheme.surfaceVariant
            }
          )
      ) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.Send,
          contentDescription = "Send",
          tint = if (inputText.isNotBlank() && !isLoading) {
            MaterialTheme.colorScheme.onPrimary
          } else {
            Color.Gray
          }
        )
      }
    }
  }
}
