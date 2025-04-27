package org.ailingo.app.features.chat.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.defaultProfilePhoto
import ailingo.composeapp.generated.resources.maskot
import ailingo.composeapp.generated.resources.message
import ailingo.composeapp.generated.resources.send_message
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.UiState
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

@Composable
fun ChatScreen(
    topicName: String,
    topicImage: String,
    chatUiState: UiState<MutableList<Conversation>>,
    messagesState: List<Conversation>,
    onEvent: (ChatEvents) -> Unit,
    userAvatar: String? = null
) {
    var messageInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val voiceToTextHandler = rememberVoiceToTextHandler()
    val voiceState by voiceToTextHandler.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

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
            listState.scrollToItem(messagesState.size - 1)
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
                        contentDescription = "Topic name",
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
                    ChatMessageItem(message = message, userAvatar = userAvatar)
                }
                when (chatUiState) {
                    is UiState.Error -> {
                        item {
                            ChatMessageItem(message = Conversation(id = "", conversationId = "", content = chatUiState.message, timestamp = "", type = MessageType.BOT.name))
                        }
                    }

                    is UiState.Idle -> {}
                    is UiState.Loading -> {
                        item {
                            ChatMessageItem(message = Conversation(id = "", conversationId = "", content = "Waiting for response...", timestamp = "", type = MessageType.BOT.name))
                        }
                    }

                    is UiState.Success -> {}
                }
            }

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
                                    coroutineScope.launch {
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

@Composable
fun ChatMessageItem(
    message: Conversation,
    userAvatar: String? = null,
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
                    Card(
                        shape = CircleShape,
                        modifier = Modifier.align(Alignment.End).padding(end = 4.dp, top = 4.dp),
                    ) {
                        if (userAvatar != null) {
                            AsyncImage(
                                model = userAvatar,
                                contentDescription = "User Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(imageSize)
                            )
                        } else {
                            Image(
                                painter = painterResource(Res.drawable.defaultProfilePhoto),
                                modifier = Modifier.size(imageSize),
                                contentDescription = "Default User Avatar"
                            )
                        }
                    }
                } else {
                    Box(modifier = Modifier.padding(4.dp)) {
                        Image(
                            painter = painterResource(Res.drawable.maskot),
                            modifier = Modifier.size(imageSize),
                            contentDescription = "Ailingo Maskot"
                        )
                    }
                }
                Text(
                    text = message.content,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp, end = 8.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        if (!isUserMessage && !message.suggestions.isNullOrEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = endPadding)
                    .padding(bottom = 2.dp)
                    .align(horizontalAlignment),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                message.suggestions.forEach { suggestion ->
                    Box(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
                        )
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