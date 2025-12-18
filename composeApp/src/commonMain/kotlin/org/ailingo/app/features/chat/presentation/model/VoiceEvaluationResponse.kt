package org.ailingo.app.features.chat.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class VoiceEvaluationResponse(
    val transcribedText: String?,
    val confidence: Float?,
    val wordConfidences: List<WordConfidence>?,
    val grammarErrors: List<GrammarError>?,
    val speechRateWPM: Float?
)

@Serializable
data class WordConfidence(
    val word: String,
    val confidence: Float,
    val start: Float,
    val end: Float
)

@Serializable
data class GrammarError(
    val message: String,
    val shortMessage: String?,
    val ruleId: String?,
    val offset: Int,
    val length: Int,
    val suggestedReplacements: List<String>?,
    val erroneousText: String?
)

class AudioRecordingException(message: String) : Exception(message)