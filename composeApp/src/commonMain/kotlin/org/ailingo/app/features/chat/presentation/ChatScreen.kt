package org.ailingo.app.features.chat.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.ailingo_maskot
import ailingo.composeapp.generated.resources.checking
import ailingo.composeapp.generated.resources.close
import ailingo.composeapp.generated.resources.close_translation
import ailingo.composeapp.generated.resources.defaultProfilePhoto
import ailingo.composeapp.generated.resources.default_user_avatar
import ailingo.composeapp.generated.resources.english
import ailingo.composeapp.generated.resources.error_checking_improvements
import ailingo.composeapp.generated.resources.maskot
import ailingo.composeapp.generated.resources.message
import ailingo.composeapp.generated.resources.no_suggestions_available
import ailingo.composeapp.generated.resources.no_translation
import ailingo.composeapp.generated.resources.russian
import ailingo.composeapp.generated.resources.send_message
import ailingo.composeapp.generated.resources.suggestions_to_improve
import ailingo.composeapp.generated.resources.translate
import ailingo.composeapp.generated.resources.translating
import ailingo.composeapp.generated.resources.translation_error
import ailingo.composeapp.generated.resources.user_avatar
import ailingo.composeapp.generated.resources.waiting_for_response
import ailingo.composeapp.generated.resources.what_can_be_improved
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.core.presentation.custom.CustomButton
import org.ailingo.app.core.presentation.snackbar.SnackbarController
import org.ailingo.app.core.presentation.snackbar.SnackbarEvent
import org.ailingo.app.core.utils.deviceinfo.util.PlatformName
import org.ailingo.app.core.utils.voice.VoiceToTextState
import org.ailingo.app.core.utils.voice.rememberVoiceToTextHandler
import org.ailingo.app.features.chat.data.model.Conversation
import org.ailingo.app.features.chat.presentation.model.MessageType
import org.ailingo.app.getPlatformName
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    topicName: String,
    topicImage: String,
    chatUiState: UiState<MutableList<Conversation>>,
    messagesState: List<Conversation>,
    translateState: UiState<String>,
    singleMessageCheckState: UiState<String>,
    onEvent: (ChatEvents) -> Unit,
    userAvatar: String? = null
) {
    var messageInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val voiceToTextHandler = rememberVoiceToTextHandler()
    val voiceState by voiceToTextHandler.state.collectAsState()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedText by remember { mutableStateOf("") }

    val isConversationFinished by remember(messagesState) {
        derivedStateOf {
            messagesState.any { it.type == "FINAL" }
        }
    }

    var showSingleMessageImprovements by remember { mutableStateOf(false) }
    var messageToImprove by remember { mutableStateOf("") }

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
            Logger.i("Voice Recognition Error: $errorMessage")
            SnackbarController.sendEvent(SnackbarEvent(message = errorMessage))
        }
    }

    LaunchedEffect(messagesState) {
        if (messagesState.isNotEmpty()) {
            if (!isConversationFinished) {
                listState.scrollToItem(messagesState.size - 1)
            }
        }
    }

    AnimatedVisibility(sheetState.isVisible) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                }
            }
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)
            ) {
                Text(
                    selectedText,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                ) {
                    Text(
                        stringResource(Res.string.english),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            stringResource(Res.string.russian),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                AnimatedContent(
                    targetState = translateState,
                    transitionSpec = {
                        if (targetState is UiState.Loading) {
                            fadeIn() togetherWith fadeOut()
                        } else {
                            slideInVertically { height -> height } togetherWith
                                    slideOutVertically { height -> -height }
                        }.using(
                            SizeTransform(clip = false)
                        )
                    },
                    label = "translationAnimation"
                ) { targetState ->
                    Text(
                        when (targetState) {
                            is UiState.Success -> targetState.data
                            is UiState.Loading -> stringResource(Res.string.translating)
                            is UiState.Error -> stringResource(Res.string.translation_error, targetState.message)
                            else -> stringResource(Res.string.no_translation)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                CustomButton(
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(Res.string.close_translation))
                }
            }
        }
    }

    AnimatedVisibility(showSingleMessageImprovements) {
        ModalBottomSheet(
            onDismissRequest = { showSingleMessageImprovements = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    stringResource(Res.string.suggestions_to_improve),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    messageToImprove,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))

                AnimatedContent(
                    targetState = singleMessageCheckState,
                    transitionSpec = {
                        if (targetState is UiState.Loading) {
                            fadeIn() togetherWith fadeOut()
                        } else {
                            slideInVertically { height -> height } togetherWith
                                    slideOutVertically { height -> -height }
                        }.using(
                            SizeTransform(clip = false)
                        )
                    },
                    label = "improvementAnimation"
                ) { targetState ->
                    Text(
                        when (targetState) {
                            is UiState.Success -> targetState.data
                            is UiState.Loading -> stringResource(Res.string.checking)
                            is UiState.Error -> stringResource(Res.string.error_checking_improvements, targetState.message)
                            else -> stringResource(Res.string.no_suggestions_available)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                CustomButton(
                    onClick = { showSingleMessageImprovements = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(stringResource(Res.string.close))
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(start = 16.dp, end = 16.dp, bottom = 8.dp)) {
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
                    modifier = Modifier.padding(end = 8.dp)
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
                                sheetState.show()
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
                                        id = "",
                                        conversationId = "",
                                        content = chatUiState.message,
                                        timestamp = "",
                                        type = MessageType.BOT.name
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
                                        id = "",
                                        conversationId = "",
                                        content = stringResource(Res.string.waiting_for_response),
                                        timestamp = "",
                                        type = MessageType.BOT.name
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
                            //TODO ON DESKTOP
                            if (getPlatformName() != PlatformName.Desktop) {
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
                                    }
                                }) {
                                    if (voiceState is VoiceToTextState.Listening) {
                                        Icon(imageVector = Icons.Filled.Mic, contentDescription = "mic")
                                    } else {
                                        Icon(imageVector = Icons.Filled.MicOff, contentDescription = "mic")
                                    }
                                }
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            sendMessage()
                        },
                        enabled = chatUiState !is UiState.Loading,
                        modifier = Modifier.height(OutlinedTextFieldDefaults.MinHeight),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Text(stringResource(Res.string.send_message))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: Conversation,
    userAvatar: String? = null,
    onSuggestionClicked: (String) -> Unit,
    chatUiState: UiState<MutableList<Conversation>>,
    onTranslate: (String) -> Unit,
    isConversationFinished: Boolean = false,
    onImproveMessage: (String) -> Unit
) {
    val isUserMessage = message.type == MessageType.USER.name
    val backgroundColor = if (isUserMessage) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }
    val horizontalAlignment = if (isUserMessage) Alignment.End else Alignment.Start

    val startPadding = if (isUserMessage) 52.dp else 0.dp
    val endPadding = if (isUserMessage) 0.dp else 52.dp
    val timestampStartPadding = if (isUserMessage) 0.dp else 8.dp
    val timestampEndPadding = if (isUserMessage) 8.dp else 0.dp
    val imageSize = 32.dp

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = backgroundColor,
            modifier = Modifier.padding(
                PaddingValues(
                    start = startPadding,
                    top = 0.dp,
                    end = endPadding,
                    bottom = 4.dp
                )
            )
        ) {
            Column {
                if (isUserMessage) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        modifier = Modifier.padding(start = 4.dp).clickable {
                            onImproveMessage(message.content)
                        }
                    ) {
                        Text(stringResource(Res.string.what_can_be_improved))

                        Card(
                            shape = CircleShape,
                            modifier = Modifier.padding(end = 4.dp, top = 4.dp),
                        ) {
                            if (userAvatar != null) {
                                AsyncImage(
                                    model = userAvatar,
                                    contentDescription = stringResource(Res.string.user_avatar),
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.size(imageSize)
                                )
                            } else {
                                Image(
                                    painter = painterResource(Res.drawable.defaultProfilePhoto),
                                    modifier = Modifier.size(imageSize),
                                    contentDescription = stringResource(Res.string.default_user_avatar)
                                )
                            }
                        }
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.padding(4.dp)) {
                            Image(
                                painter = painterResource(Res.drawable.maskot),
                                modifier = Modifier.size(imageSize),
                                contentDescription = stringResource(Res.string.ailingo_maskot)
                            )
                        }
                        AnimatedVisibility(visible = chatUiState is UiState.Success || chatUiState is UiState.Idle) {
                            Row(
                                modifier = Modifier.clickable {
                                    onTranslate(message.content)
                                }
                            ) {
                                Icon(Icons.Default.Translate, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(Res.string.translate))
                            }
                        }
                    }
                }
                Text(
                    text = message.content,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, end = 8.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        if (!isUserMessage && !message.suggestions.isNullOrEmpty() && !isConversationFinished) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = endPadding)
                    .padding(bottom = 2.dp)
                    .align(horizontalAlignment),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                message.suggestions.forEach { suggestion ->
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable {
                                if (chatUiState is UiState.Success) {
                                    val extractedText = suggestion.substringAfter(". \"").dropLast(1)
                                    onSuggestionClicked(extractedText)
                                }
                            },
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                            )
                            OutlinedCard(
                                modifier = Modifier.clickable {
                                    onTranslate(suggestion)
                                }
                            ) {
                                Icon(
                                    Icons.Filled.Translate,
                                    contentDescription = stringResource(Res.string.translate),
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        val displayTimestamp = formatTimestamp(message.timestamp)
        Text(
            text = displayTimestamp,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(
                start = timestampStartPadding,
                end = timestampEndPadding,
                bottom = 8.dp
            )
        )
    }
}

fun formatTimestamp(timestampString: String): String {
    return if (timestampString.contains("T") && timestampString.length > timestampString.indexOf("T") + 5) {
        val datePart = timestampString.substringBefore('T')
        val timePart = timestampString.substringAfter('T').substring(0, 5)
        "$datePart $timePart".trim()
    } else {
        ""
    }
}