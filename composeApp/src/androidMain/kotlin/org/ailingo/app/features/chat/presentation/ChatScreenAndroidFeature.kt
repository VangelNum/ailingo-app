package org.ailingo.app.features.chat.presentation

import AiLingo.composeApp.BuildConfig.BASE_URL
import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.analyze_conversation
import ailingo.composeapp.generated.resources.close
import ailingo.composeapp.generated.resources.confidence_assessment
import ailingo.composeapp.generated.resources.confidence_level_high
import ailingo.composeapp.generated.resources.confidence_level_low
import ailingo.composeapp.generated.resources.confidence_level_medium
import ailingo.composeapp.generated.resources.confidence_level_very_low
import ailingo.composeapp.generated.resources.conversation_finished
import ailingo.composeapp.generated.resources.error
import ailingo.composeapp.generated.resources.evaluation_results
import ailingo.composeapp.generated.resources.evaluation_results_appear
import ailingo.composeapp.generated.resources.issue
import ailingo.composeapp.generated.resources.message
import ailingo.composeapp.generated.resources.no_specific_feedback
import ailingo.composeapp.generated.resources.original_text
import ailingo.composeapp.generated.resources.overall_confidence
import ailingo.composeapp.generated.resources.processing_voice
import ailingo.composeapp.generated.resources.recording
import ailingo.composeapp.generated.resources.send_message
import ailingo.composeapp.generated.resources.speech_rate
import ailingo.composeapp.generated.resources.speech_rate_assessment
import ailingo.composeapp.generated.resources.speech_rate_fast
import ailingo.composeapp.generated.resources.speech_rate_normal
import ailingo.composeapp.generated.resources.speech_rate_slow
import ailingo.composeapp.generated.resources.speech_rate_very_fast
import ailingo.composeapp.generated.resources.speech_rate_very_slow
import ailingo.composeapp.generated.resources.start_recording
import ailingo.composeapp.generated.resources.stop_recording
import ailingo.composeapp.generated.resources.suggestions
import ailingo.composeapp.generated.resources.suggestions_corrections
import ailingo.composeapp.generated.resources.tap_microphone
import ailingo.composeapp.generated.resources.voice_pronunciation_check
import ailingo.composeapp.generated.resources.waiting_for_response
import ailingo.composeapp.generated.resources.what_we_heard
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.append
import io.ktor.http.headers
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.core.presentation.snackbar.SnackbarController
import org.ailingo.app.core.presentation.snackbar.SnackbarEvent
import org.ailingo.app.core.utils.deviceinfo.util.PlatformName
import org.ailingo.app.core.utils.voice.VoiceToTextState
import org.ailingo.app.core.utils.voice.rememberVoiceToTextHandler
import org.ailingo.app.features.chat.data.model.Conversation
import org.ailingo.app.features.chat.presentation.model.AudioRecordingException
import org.ailingo.app.features.chat.presentation.model.GrammarError
import org.ailingo.app.features.chat.presentation.model.MessageType
import org.ailingo.app.features.chat.presentation.model.VoiceEvaluationResponse
import org.ailingo.app.features.chat.presentation.model.WordConfidence
import org.ailingo.app.getPlatformName
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import rememberAudioRecorder
import java.io.File

@OptIn(markerClass = [ExperimentalMaterial3Api::class])
@Composable
actual fun ChatScreenAndroidFeature(
    topicName: String,
    topicImage: String,
    chatUiState: UiState<MutableList<Conversation>>,
    messagesState: List<Conversation>,
    translateState: UiState<String>,
    singleMessageCheckState: UiState<String>,
    onEvent: (ChatEvents) -> Unit,
    userAvatar: String?,
    onNavigateToAnalyzeConversation: () -> Unit
) {
    val httpClient: HttpClient = koinInject()
    var messageInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val voiceToTextHandler = rememberVoiceToTextHandler()
    val voiceState by voiceToTextHandler.state.collectAsState()
    val scope = rememberCoroutineScope()
    val translateSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedText by remember { mutableStateOf("") }

    val isConversationFinished by remember(messagesState) {
        derivedStateOf {
            messagesState.any { it.type == "FINAL" }
        }
    }

    var showSingleMessageImprovements by remember { mutableStateOf(false) }
    var messageToImprove by remember { mutableStateOf("") }

    // State for the voice evaluation feature
    val audioRecorder = rememberAudioRecorder()
    var isRecordingForEvaluation by remember { mutableStateOf(false) }
    var voiceEvaluationApiState by remember { mutableStateOf<UiState<VoiceEvaluationResponse>>(UiState.Idle()) }
    var showVoiceEvaluationSheetInternal by remember { mutableStateOf(false) }

    fun sendMessage() {
        messageInput = messageInput.trim()
        if (messageInput.isNotBlank()) {
            onEvent(ChatEvents.OnSendMessage(messageInput))
            messageInput = ""
        }
    }

    LaunchedEffect(voiceState) {
        if (voiceState is VoiceToTextState.Result) {
            val resultText = (voiceState as VoiceToTextState.Result).text
            if (resultText.isNotBlank()) {
                messageInput = resultText
            }
        } else if (voiceState is VoiceToTextState.Error) {
            val errorMessage = (voiceState as VoiceToTextState.Error).message
            SnackbarController.sendEvent(SnackbarEvent(message = errorMessage))
        }
    }

    LaunchedEffect(messagesState) {
        if (messagesState.isNotEmpty()) {
            val lastMessage = messagesState.lastOrNull()
            if (lastMessage != null && (!isUserMessage(lastMessage) || messagesState.size == listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size)) {
                listState.scrollToItem(messagesState.size - 1)
            }
        }
    }

    // Voice Evaluation Handlers (Simulated ViewModel logic)
    fun onShowVoiceEvaluationSheetRequest() {
        voiceEvaluationApiState = UiState.Idle()
        isRecordingForEvaluation = false // Ensure recording is reset
        // Any other prep needed before showing the sheet
    }

    fun onStartVoiceEvaluationRecording() {
        isRecordingForEvaluation = true
        voiceEvaluationApiState = UiState.Idle() // Clear previous results/errors
        try {
            // MODIFIED: Changed filename to suggest WAV format.
            // Ensure your rememberAudioRecorder() can handle this and produce a valid WAV.
            // Vosk typically prefers WAV PCM, 16kHz, 16-bit, mono.
            audioRecorder.startRecording("voice_eval_temp.wav")
        } catch (e: AudioRecordingException) {
            isRecordingForEvaluation = false
            voiceEvaluationApiState = UiState.Error("Recording init failed: ${e.message}")
            scope.launch {
                SnackbarController.sendEvent(SnackbarEvent(message = "Recording error: ${e.message}"))
            }
        } catch (e: Exception) {
            isRecordingForEvaluation = false
            voiceEvaluationApiState = UiState.Error("Failed to start recording: ${e.message}")
            scope.launch {
                SnackbarController.sendEvent(SnackbarEvent(message = "Unexpected error starting recording: ${e.message}"))
            }
        }
    }

    fun onStopVoiceEvaluationRecordingAndProcess() {
        if (!isRecordingForEvaluation && !audioRecorder.isRecording()) return // Already stopped or never started
        isRecordingForEvaluation = false
        val audioFilePath = audioRecorder.stopRecording()

        if (audioFilePath != null && File(audioFilePath).exists() && File(audioFilePath).length() > 0) {
            voiceEvaluationApiState = UiState.Loading()
            // Real API call to /api/v1/voice/evaluate
            scope.launch {
                val file = File(audioFilePath)
                try {
                    val response = httpClient.submitFormWithBinaryData(
                        url = "$BASE_URL/api/v1/voice/evaluate",
                        formData = formData {
                            append("audio", file.readBytes(), Headers.build {
                                append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                                // MODIFIED: Changed ContentType to audio/wav
                                append(HttpHeaders.ContentType, ContentType.parse("audio/wav"))
                            })
                        }
                    ) {
                        method = HttpMethod.Post
                        headers {
                            append(HttpHeaders.Accept, "application/hal+json") // Or application/json depending on your API
                        }
                    }

                    if (response.status.isSuccess()) {
                        val result: VoiceEvaluationResponse = response.body()
                        voiceEvaluationApiState = UiState.Success(result)
                    } else {
                        val errorBody = try {
                            response.body<String>()
                        } catch (e: Exception) {
                            "No error body available"
                        }
                        // Log for debugging, especially if you get 415 Unsupported Media Type
                        println("Ktor API Error ${response.status.value}: $errorBody")
                        voiceEvaluationApiState = UiState.Error("API Error ${response.status.value}: ${response.status.description}")
                        SnackbarController.sendEvent(SnackbarEvent(message = "API error: ${response.status.value}"))
                    }

                } catch (e: Exception) {
                    val errorMessage = "API request failed: ${e.message}"
                    println("Ktor Exception: $errorMessage")
                    e.printStackTrace()
                    voiceEvaluationApiState = UiState.Error(errorMessage)
                    SnackbarController.sendEvent(SnackbarEvent(message = errorMessage))
                } finally {
                    if (audioFilePath != null) {
                        File(audioFilePath).delete()
                    }
                }
            }
        } else {
            voiceEvaluationApiState = UiState.Error("Failed to record audio or the recording was empty.")
            if (audioFilePath != null) {
                File(audioFilePath).delete()
            }
            scope.launch {
                SnackbarController.sendEvent(SnackbarEvent(message = "Audio recording failed or was empty."))
            }
        }
    }

    fun onCancelVoiceEvaluationRecording() {
        if (audioRecorder.isRecording()) {
            audioRecorder.cancelRecording()
        }
        isRecordingForEvaluation = false
    }

    fun onDismissVoiceEvaluationSheet() {
        if (isRecordingForEvaluation || audioRecorder.isRecording()) {
            onCancelVoiceEvaluationRecording()
        }
        showVoiceEvaluationSheetInternal = false
        voiceEvaluationApiState = UiState.Idle()
    }

    TranslateMessageBottomSheet(
        translateSheetState = translateSheetState,
        scope = scope,
        selectedText = selectedText,
        translateState = translateState
    )

    SingleMessageCheckBottomSheet(
        showSingleMessageImprovements = showSingleMessageImprovements,
        onShowSingleMessageImprovementsChange = { showSingleMessageImprovements = it },
        messageToImprove = messageToImprove,
        singleMessageCheckState = singleMessageCheckState
    )

    VoiceEvaluationBottomSheet(
        showSheet = showVoiceEvaluationSheetInternal,
        onDismissRequest = {
            onDismissVoiceEvaluationSheet()
        },
        isRecording = isRecordingForEvaluation,
        evaluationState = voiceEvaluationApiState,
        onRecordToggle = {
            if (isRecordingForEvaluation) {
                onStopVoiceEvaluationRecordingAndProcess()
            } else {
                onStartVoiceEvaluationRecording()
            }
        },
        onSendTranscribedText = { transcribedText -> // Add this
            messageInput = transcribedText
            sendMessage()
            onDismissVoiceEvaluationSheet()
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            shape = CircleShape
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(4.dp)
            ) {
                Card(
                    shape = CircleShape,
                    modifier = Modifier.size(50.dp)
                ) {
                    AsyncImage(
                        model = topicImage,
                        contentDescription = topicName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Text(
                    topicName,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(end = 8.dp),
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = listState
            ) {
                items(messagesState) { message ->
                    ChatMessageItem(
                        message = message,
                        userAvatar = userAvatar,
                        onSuggestionClicked = { suggestion ->
                            onEvent(ChatEvents.OnSendMessage(suggestion))
                        },
                        chatUiState = chatUiState,
                        onTranslate = { text ->
                            selectedText = text
                            onEvent(ChatEvents.OnTranslateText(text))
                            scope.launch {
                                translateSheetState.show()
                            }
                        },
                        isConversationFinished = isConversationFinished,
                        onImproveMessage = { messageContent ->
                            messageToImprove = messageContent
                            onEvent(ChatEvents.OnCheckSingleMessage(messageContent))
                            showSingleMessageImprovements = true
                        }
                    )
                }
                if (!isConversationFinished) {
                    when (chatUiState) {
                        is UiState.Error -> {
                            item {
                                ChatMessageItem(
                                    message = Conversation(
                                        id = "error_msg_${chatUiState.message.hashCode()}",
                                        conversationId = "",
                                        content = chatUiState.message,
                                        timestamp = "",
                                        type = MessageType.BOT.name,
                                        suggestions = null
                                    ),
                                    onSuggestionClicked = {},
                                    chatUiState = chatUiState,
                                    onTranslate = {},
                                    isConversationFinished = isConversationFinished,
                                    onImproveMessage = {}
                                )
                            }
                        }

                        is UiState.Idle -> {}
                        is UiState.Loading -> {
                            item {
                                ChatMessageItem(
                                    message = Conversation(
                                        id = "loading_msg",
                                        conversationId = "",
                                        content = stringResource(Res.string.waiting_for_response),
                                        timestamp = "",
                                        type = MessageType.BOT.name,
                                        suggestions = null
                                    ),
                                    onSuggestionClicked = {},
                                    chatUiState = chatUiState,
                                    onTranslate = {},
                                    isConversationFinished = isConversationFinished,
                                    onImproveMessage = {}
                                )
                            }
                        }

                        is UiState.Success -> {}
                    }
                }
            }

            AnimatedVisibility(!isConversationFinished) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = messageInput,
                        onValueChange = { messageInput = it },
                        modifier = Modifier
                            .weight(1f)
                            .onKeyEvent { event ->
                                if (event.key == Key.Enter && chatUiState !is UiState.Loading) {
                                    sendMessage()
                                    return@onKeyEvent true
                                }
                                false
                            },
                        placeholder = { Text(stringResource(Res.string.message)) },
                        shape = RoundedCornerShape(32.dp),
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        trailingIcon = {
                            if (getPlatformName() != PlatformName.Desktop) {
                                Row {
                                    IconButton(onClick = {
                                        if (voiceToTextHandler.isAvailable) {
                                            scope.launch {
                                                if (voiceState is VoiceToTextState.Listening) {
                                                    voiceToTextHandler.stopListening()
                                                } else {
                                                    messageInput = ""
                                                    voiceToTextHandler.startListening()
                                                }
                                            }
                                        } else {
                                            scope.launch {
                                                SnackbarController.sendEvent(SnackbarEvent(message = "Voice recognition not available"))
                                            }
                                        }
                                    }) {
                                        val micIcon = if (voiceState is VoiceToTextState.Listening) Icons.Filled.Mic else Icons.Filled.MicOff
                                        val micDescription = if (voiceState is VoiceToTextState.Listening) "Stop STT Listening" else "Start STT Listening"
                                        Icon(imageVector = micIcon, contentDescription = micDescription)
                                    }

                                    // Voice Evaluation Button
                                    IconButton(onClick = {
                                        onShowVoiceEvaluationSheetRequest()
                                        showVoiceEvaluationSheetInternal = true
                                    }) {
                                        Icon(
                                            imageVector = Icons.Filled.RecordVoiceOver,
                                            contentDescription = "Evaluate Voice"
                                        )
                                    }
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = { sendMessage() },
                        enabled = chatUiState !is UiState.Loading && messageInput.isNotBlank(),
                        modifier = Modifier.height(OutlinedTextFieldDefaults.MinHeight),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Text(stringResource(Res.string.send_message))
                    }
                }
            }
            AnimatedVisibility(visible = isConversationFinished) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
                        enabled = false
                    ) {
                        Text(stringResource(Res.string.conversation_finished))
                    }
                    Button(
                        onClick = { onNavigateToAnalyzeConversation() },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight)
                    ) {
                        Text(stringResource(Res.string.analyze_conversation))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceEvaluationBottomSheet(
    showSheet: Boolean,
    onDismissRequest: () -> Unit,
    isRecording: Boolean,
    evaluationState: UiState<VoiceEvaluationResponse>,
    onRecordToggle: () -> Unit,
    onSendTranscribedText: (String) -> Unit
) {
    var transcribedTextFromEvaluation by remember { mutableStateOf("") }
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            modifier = Modifier.defaultMinSize(minHeight = 250.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(stringResource(Res.string.voice_pronunciation_check), style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))

                IconButton(
                    onClick = onRecordToggle,
                    modifier = Modifier.size(64.dp),
                    enabled = evaluationState !is UiState.Loading
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Filled.StopCircle else Icons.Filled.Mic,
                        contentDescription = if (isRecording) stringResource(Res.string.stop_recording) else stringResource(Res.string.start_recording),
                        modifier = Modifier.size(48.dp),
                        tint = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = when {
                        isRecording -> stringResource(Res.string.recording)
                        evaluationState is UiState.Loading -> stringResource(Res.string.processing_voice)
                        else -> stringResource(Res.string.tap_microphone)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedContent(
                    targetState = evaluationState,
                    label = "evaluationResultAnimation",
                    transitionSpec = {
                        (slideInVertically { height -> height } + fadeIn()) togetherWith
                                (slideOutVertically { height -> -height } + fadeOut()) using
                                SizeTransform(clip = false)
                    }
                ) { state ->
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        when (state) {
                            is UiState.Success -> {
                                val response = state.data
                                transcribedTextFromEvaluation = response.transcribedText ?: ""

                                Text(stringResource(Res.string.evaluation_results), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))

                                response.transcribedText?.let { text ->
                                    Text(
                                        stringResource(
                                            if (response.grammarErrors.isNullOrEmpty()) Res.string.what_we_heard
                                            else Res.string.what_we_heard // This seems to be the same string resource, might be intentional
                                        ),
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    val annotatedString = buildAnnotatedString {
                                        append(text)
                                        response.grammarErrors?.forEach { error ->
                                            // MODIFICATION STARTS HERE
                                            error.erroneousText?.let { erroneous ->
                                                if (erroneous.isNotEmpty()) {
                                                    val start = error.offset
                                                    val end = error.offset + erroneous.length // Use length of erroneousText

                                                    // Boundary checks: ensure start and end are valid for 'text'
                                                    if (start >= 0 && end <= text.length) {
                                                        addStyle(
                                                            style = SpanStyle(
                                                                color = MaterialTheme.colorScheme.error,
                                                                textDecoration = TextDecoration.Underline,
                                                                fontWeight = FontWeight.SemiBold
                                                            ),
                                                            start = start,
                                                            end = end
                                                        )
                                                    }
                                                }
                                            }
                                            // MODIFICATION ENDS HERE
                                        }
                                    }
                                    Text(annotatedString, style = MaterialTheme.typography.bodyLarge)
                                    Spacer(modifier = Modifier.height(14.dp))
                                }

                                response.confidence?.let { confidenceValue ->
                                    Text(stringResource(Res.string.overall_confidence), style = MaterialTheme.typography.titleSmall)
                                    Text("%.2f%%".format(confidenceValue * 100), style = MaterialTheme.typography.bodyLarge)
                                    getConfidenceDescription(confidenceValue)?.let { descResId ->
                                        Text(
                                            stringResource(Res.string.confidence_assessment, stringResource(descResId)),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                }

                                response.speechRateWPM?.let { wpm ->
                                    Text(stringResource(Res.string.speech_rate), style = MaterialTheme.typography.titleSmall)
                                    Text("$wpm WPM (Words Per Minute)", style = MaterialTheme.typography.bodyLarge)
                                    getSpeechRateDescription(wpm)?.let { descResId ->
                                        Text(
                                            stringResource(Res.string.speech_rate_assessment, stringResource(descResId)),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                }

                                response.grammarErrors?.takeIf { it.isNotEmpty() }?.let { errors ->
                                    Text(stringResource(Res.string.suggestions_corrections), style = MaterialTheme.typography.titleSmall)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    errors.forEachIndexed { index, error ->
                                        OutlinedCard(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                        ) {
                                            Column(Modifier.padding(12.dp)) {
                                                error.erroneousText?.let { erroneous ->
                                                    Text(
                                                        stringResource(Res.string.original_text, erroneous),
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Text(stringResource(Res.string.issue, error.message), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                                error.suggestedReplacements?.takeIf { it.isNotEmpty() }?.let { suggestions ->
                                                    Text(stringResource(Res.string.suggestions, suggestions.joinToString(" / ")), style = MaterialTheme.typography.bodyMedium)
                                                }
                                            }
                                        }
                                        if (index < errors.size - 1) Spacer(modifier = Modifier.height(4.dp))
                                    }
                                    Spacer(modifier = Modifier.height(14.dp))
                                }

                                if (response.transcribedText == null && response.grammarErrors.isNullOrEmpty() && response.confidence == null && response.speechRateWPM == null) {
                                    Text(
                                        stringResource(Res.string.no_specific_feedback),
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }

                            is UiState.Error -> {
                                Text(
                                    stringResource(Res.string.error, state.message),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )
                                transcribedTextFromEvaluation = ""
                            }

                            is UiState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(vertical = 16.dp)
                                )
                            }

                            is UiState.Idle -> {
                                if (!isRecording) {
                                    Text(
                                        stringResource(Res.string.evaluation_results_appear),
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                                transcribedTextFromEvaluation = ""
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f, fill = false)) // Pushes buttons to bottom
                Spacer(modifier = Modifier.height(16.dp)) // Increased spacing before buttons

                // Send the Transcribed Text Button
                Button(
                    onClick = {
                        if (transcribedTextFromEvaluation.isNotEmpty()) {
                            onSendTranscribedText(transcribedTextFromEvaluation)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp), // Slightly more rounded
                    enabled = transcribedTextFromEvaluation.isNotEmpty() && evaluationState is UiState.Success
                ) {
                    Text(stringResource(Res.string.send_message))
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Close Button
                OutlinedButton( // Changed to OutlinedButton for differentiation
                    onClick = onDismissRequest,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(Res.string.close))
                }
            }
        }
    }
}

@Composable
private fun getConfidenceDescription(confidence: Float): StringResource? {
    return when {
        confidence >= 0.85f -> Res.string.confidence_level_high
        confidence >= 0.70f -> Res.string.confidence_level_medium
        confidence >= 0.50f -> Res.string.confidence_level_low
        else -> Res.string.confidence_level_very_low
    }
}
@Composable
private fun getSpeechRateDescription(wpm: Float): StringResource? {
    return when {
        wpm > 170f -> Res.string.speech_rate_very_fast
        wpm > 130f -> Res.string.speech_rate_fast
        wpm >= 100f -> Res.string.speech_rate_normal
        wpm >= 70f -> Res.string.speech_rate_slow
        else -> Res.string.speech_rate_very_slow
    }
}

@Composable
private fun PreviewAppTheme(content: @Composable () -> Unit) {
    MaterialTheme { // Replace with your actual app theme if it's more complex
        Surface(color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}

// Sample Data for Previews
private val sampleVoiceEvaluationResponseFull = VoiceEvaluationResponse(
    transcribedText = "Hello world, this is a test sentence with some mistaeks.",
    confidence = 0.92f,
    wordConfidences = listOf(
        WordConfidence("Hello", 0.95f, 0.1f, 0.5f),
        WordConfidence("world", 0.90f, 0.6f, 1.0f),
        WordConfidence("this", 0.98f, 1.1f, 1.4f),
        WordConfidence("is", 0.99f, 1.5f, 1.7f),
        WordConfidence("a", 1.0f, 1.8f, 1.9f),
        WordConfidence("test", 0.93f, 2.0f, 2.4f),
        WordConfidence("sentence", 0.89f, 2.5f, 3.1f),
        WordConfidence("with", 0.96f, 3.2f, 3.5f),
        WordConfidence("some", 0.91f, 3.6f, 3.9f),
        WordConfidence("mistaeks.", 0.70f, 4.0f, 4.5f) // Erroneous part
    ),
    grammarErrors = listOf(
        GrammarError(
            message = "Possible spelling mistake found.",
            shortMessage = "Spelling",
            ruleId = "SPELL_CHECK",
            offset = 43, // "Hello world, this is a test sentence with some ".length = 43
            length = 8,  // Length of "mistaeks" - this is from the API, used if erroneousText is null
            suggestedReplacements = listOf("mistakes"),
            erroneousText = "mistaeks" // Actual text that is wrong
        )
    ),
    speechRateWPM = 125.5f
)

private val sampleVoiceEvaluationResponseMinimal = VoiceEvaluationResponse(
    transcribedText = "Okay.",
    confidence = 0.75f,
    wordConfidences = null,
    grammarErrors = null,
    speechRateWPM = 110f
)

private val sampleVoiceEvaluationResponseNoFeedback = VoiceEvaluationResponse(
    transcribedText = null,
    confidence = null,
    wordConfidences = null,
    grammarErrors = null,
    speechRateWPM = null
)

private val sampleVoiceEvaluationResponseWithErrorHighlight = VoiceEvaluationResponse(
    transcribedText = "I can has cheezburger?",
    confidence = 0.65f,
    grammarErrors = listOf(
        GrammarError(
            message = "Incorrect verb form. Consider 'have' or 'get'.",
            shortMessage = "Verb form",
            ruleId = "VERB_FORM_HAS",
            offset = 6, // "I can ".length = 6
            length = 3, // Length of "has"
            suggestedReplacements = listOf("have", "get"),
            erroneousText = "has"
        )
    ),
    speechRateWPM = 100f,
    wordConfidences = emptyList()
)

@Preview(showBackground = true, name = "Idle State")
@Composable
fun VoiceEvaluationBottomSheetPreview_Idle() {
    PreviewAppTheme {
        Box(modifier = Modifier.fillMaxSize().padding(top=50.dp)) { // Simulate being in a sheet
            VoiceEvaluationBottomSheet(
                showSheet = true,
                onDismissRequest = {},
                isRecording = false,
                evaluationState = UiState.Idle(),
                onRecordToggle = {},
                onSendTranscribedText = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Recording State")
@Composable
fun VoiceEvaluationBottomSheetPreview_Recording() {
    PreviewAppTheme {
        Box(modifier = Modifier.fillMaxSize().padding(top=50.dp)) {
            VoiceEvaluationBottomSheet(
                showSheet = true,
                onDismissRequest = {},
                isRecording = true,
                evaluationState = UiState.Idle(), // Or UiState.Loading() if processing immediately
                onRecordToggle = {},
                onSendTranscribedText = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Loading State")
@Composable
fun VoiceEvaluationBottomSheetPreview_Loading() {
    PreviewAppTheme {
        Box(modifier = Modifier.fillMaxSize().padding(top=50.dp)) {
            VoiceEvaluationBottomSheet(
                showSheet = true,
                onDismissRequest = {},
                isRecording = false,
                evaluationState = UiState.Loading(),
                onRecordToggle = {},
                onSendTranscribedText = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Success State - Full Data")
@Composable
fun VoiceEvaluationBottomSheetPreview_SuccessFull() {
    PreviewAppTheme {
        Box(modifier = Modifier.fillMaxSize().padding(top=50.dp)) {
            VoiceEvaluationBottomSheet(
                showSheet = true,
                onDismissRequest = {},
                isRecording = false,
                evaluationState = UiState.Success(sampleVoiceEvaluationResponseFull),
                onRecordToggle = {},
                onSendTranscribedText = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Success State - Minimal Data")
@Composable
fun VoiceEvaluationBottomSheetPreview_SuccessMinimal() {
    PreviewAppTheme {
        Box(modifier = Modifier.fillMaxSize().padding(top=50.dp)) {
            VoiceEvaluationBottomSheet(
                showSheet = true,
                onDismissRequest = {},
                isRecording = false,
                evaluationState = UiState.Success(sampleVoiceEvaluationResponseMinimal),
                onRecordToggle = {},
                onSendTranscribedText = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Success State - No Specific Feedback")
@Composable
fun VoiceEvaluationBottomSheetPreview_SuccessNoFeedback() {
    PreviewAppTheme {
        Box(modifier = Modifier.fillMaxSize().padding(top=50.dp)) {
            VoiceEvaluationBottomSheet(
                showSheet = true,
                onDismissRequest = {},
                isRecording = false,
                evaluationState = UiState.Success(sampleVoiceEvaluationResponseNoFeedback),
                onRecordToggle = {},
                onSendTranscribedText = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Success State - With Error Highlight")
@Composable
fun VoiceEvaluationBottomSheetPreview_SuccessWithErrorHighlight() {
    PreviewAppTheme {
        Box(modifier = Modifier.fillMaxSize().padding(top=50.dp)) {
            VoiceEvaluationBottomSheet(
                showSheet = true,
                onDismissRequest = {},
                isRecording = false,
                evaluationState = UiState.Success(sampleVoiceEvaluationResponseWithErrorHighlight),
                onRecordToggle = {},
                onSendTranscribedText = {}
            )
        }
    }
}


@Preview(showBackground = true, name = "Error State")
@Composable
fun VoiceEvaluationBottomSheetPreview_Error() {
    PreviewAppTheme {
        Box(modifier = Modifier.fillMaxSize().padding(top=50.dp)) {
            VoiceEvaluationBottomSheet(
                showSheet = true,
                onDismissRequest = {},
                isRecording = false,
                evaluationState = UiState.Error("This is a sample error message from the API."),
                onRecordToggle = {},
                onSendTranscribedText = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Success State - Send Button Enabled")
@Composable
fun VoiceEvaluationBottomSheetPreview_SuccessSendEnabled() {
    PreviewAppTheme {
        Box(modifier = Modifier.fillMaxSize().padding(top=50.dp)) {
            VoiceEvaluationBottomSheet(
                showSheet = true,
                onDismissRequest = {},
                isRecording = false,
                evaluationState = UiState.Success(sampleVoiceEvaluationResponseMinimal.copy(transcribedText = "Hello there")),
                onRecordToggle = {},
                onSendTranscribedText = {}
            )
        }
    }
}

@Preview(showBackground = true, name = "Success State - Send Button Disabled (No Text)")
@Composable
fun VoiceEvaluationBottomSheetPreview_SuccessSendDisabled() {
    PreviewAppTheme {
        Box(modifier = Modifier.fillMaxSize().padding(top=50.dp)) {
            VoiceEvaluationBottomSheet(
                showSheet = true,
                onDismissRequest = {},
                isRecording = false,
                evaluationState = UiState.Success(sampleVoiceEvaluationResponseMinimal.copy(transcribedText = null)),
                onRecordToggle = {},
                onSendTranscribedText = {}
            )
        }
    }
}