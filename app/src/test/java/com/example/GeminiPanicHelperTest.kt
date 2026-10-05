package com.example

import com.example.data.model.PetEntity
import com.example.ui.components.MessageSender
import com.example.ui.components.PanicMessage
import com.example.util.GeminiPanicHelper
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class GeminiPanicHelperTest {

  private val samplePet = PetEntity(
    id = 1,
    name = "Bella",
    species = "Dog",
    breed = "Golden Retriever",
    ageMonths = 24,
    gender = "GIRL"
  )

  @Test
  fun generateCalmingResponse_includesPetNameAndTailoredGuidance() = runTest {
    val userQuery = "Bella is panting heavily and trembling under the table because of thunder"
    val responsePayload = GeminiPanicHelper.generateCalmingResponse(
      userPrompt = userQuery,
      pet = samplePet
    )

    val response = responsePayload.text
    assertTrue("Response should contain pet name Bella", response.contains("Bella"))
    assertTrue("Response should address trembling or breathing", response.lowercase().contains("breathing") || response.lowercase().contains("trembling") || response.lowercase().contains("deep"))
    assertTrue("Response should contain conversational follow-up question", response.contains("?"))
  }

  @Test
  fun generateCalmingResponse_handlesTailWigglingScenario() = runTest {
    val userQuery = "Bella is wiggling her tail"
    val responsePayload = GeminiPanicHelper.generateCalmingResponse(
      userPrompt = userQuery,
      pet = samplePet
    )

    val response = responsePayload.text
    assertTrue("Response should contain pet name Bella", response.contains("Bella"))
    assertTrue("Response should specifically analyze tail movement", response.lowercase().contains("tail") || response.lowercase().contains("wiggl"))
    assertTrue("Response should offer tailored guidance for tail movement", response.lowercase().contains("expressive") || response.lowercase().contains("wag") || response.lowercase().contains("soft"))
  }

  @Test
  fun generateCalmingResponse_declinesOffTopicMathQueries() = runTest {
    val userQuery = "What is 2 + 2?"
    val responsePayload = GeminiPanicHelper.generateCalmingResponse(
      userPrompt = userQuery,
      pet = samplePet
    )

    val response = responsePayload.text
    assertTrue("Response should decline off-topic query", response.contains("limited only to behavioral analysis") || response.contains("care of your pet"))
    assertTrue("Response should reference pet Bella", response.contains("Bella"))
  }

  @Test
  fun generateCalmingResponse_attachesAppControlIntentForDecoder() = runTest {
    val userQuery = "Open behavior decoder to decode body language"
    val responsePayload = GeminiPanicHelper.generateCalmingResponse(
      userPrompt = userQuery,
      pet = samplePet
    )

    assertEquals("decoder", responsePayload.actionRoute)
    assertEquals("🔍 Open Behavior Decoder", responsePayload.actionLabel)
  }

  @Test
  fun generateCalmingResponse_handlesMultiTurnFollowUpQuestions() = runTest {
    val history = listOf(
      PanicMessage(sender = MessageSender.AI, text = "✨ Gemini AI Assistant Active for Bella."),
      PanicMessage(sender = MessageSender.USER, text = "Bella is hiding under the bed."),
      PanicMessage(sender = MessageSender.AI, text = "✨ Gemini AI: I understand your concern about Bella...")
    )

    val followUpQuery = "Should I offer her a treat or water while she is under the bed?"
    val responsePayload = GeminiPanicHelper.generateCalmingResponse(
      userPrompt = followUpQuery,
      conversationHistory = history,
      pet = samplePet
    )

    val response = responsePayload.text
    assertTrue("Response should acknowledge multi-turn chat or question", response.contains("Bella"))
    assertTrue("Response should address question regarding water or treat", response.lowercase().contains("question") || response.lowercase().contains("water") || response.lowercase().contains("treat") || response.lowercase().contains("feeding"))
    assertFalse("Response should not be empty", response.isBlank())
  }

  @Test
  fun generateCalmingResponse_handlesCameraPostureScan() = runTest {
    val cameraQuery = "Showed Bella's back posture via camera feed"
    val responsePayload = GeminiPanicHelper.generateCalmingResponse(
      userPrompt = cameraQuery,
      pet = samplePet,
      isCameraPostureScan = true
    )

    val response = responsePayload.text
    assertTrue("Response should mention spine or posture analysis", response.lowercase().contains("posture") || response.lowercase().contains("spine") || response.lowercase().contains("back"))
    assertTrue("Response should reference pet Bella", response.contains("Bella"))
  }
}
