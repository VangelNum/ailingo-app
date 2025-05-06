package org.ailingo.app.features.topics.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.cancel
import ailingo.composeapp.generated.resources.coins
import ailingo.composeapp.generated.resources.emptystate
import ailingo.composeapp.generated.resources.enter_custom_topic
import ailingo.composeapp.generated.resources.gain
import ailingo.composeapp.generated.resources.icon_experience
import ailingo.composeapp.generated.resources.loadingstate
import ailingo.composeapp.generated.resources.price
import ailingo.composeapp.generated.resources.start_chat
import ailingo.composeapp.generated.resources.start_your_topic
import ailingo.composeapp.generated.resources.topic_list_empty
import ailingo.composeapp.generated.resources.type_topic_hint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import org.ailingo.app.core.presentation.EmptyScreen
import org.ailingo.app.core.presentation.ErrorScreen
import org.ailingo.app.core.presentation.LoadingScreen
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.topics.data.model.Topic
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun TopicsScreen(
    topicsUiState: UiState<List<Topic>>,
    currentUserXp: Int,
    currentUserCoins: Int,
    onTopicClick: (String, String) -> Unit,
    onClickCustomTopic: (String) -> Unit,
    onGoToShopClick: () -> Unit
) {
    var showCustomTopicDialog by remember { mutableStateOf(false) }
    var customTopicText by remember { mutableStateOf("") }

    when (topicsUiState) {
        is UiState.Error -> {
            ErrorScreen(errorMessage = topicsUiState.message)
        }

        is UiState.Idle -> {}
        is UiState.Loading -> {
            LoadingScreen(modifier = Modifier.fillMaxSize(), image = Res.drawable.loadingstate)
        }

        is UiState.Success -> {
            if (topicsUiState.data.isEmpty()) {
                EmptyScreen(
                    text = Res.string.topic_list_empty,
                    modifier = Modifier.fillMaxSize(),
                    image = Res.drawable.emptystate
                )
            } else {
                TopicsContent(
                    topics = topicsUiState.data,
                    currentUserXp = currentUserXp,
                    currentUserCoins = currentUserCoins,
                    onTopicClick = onTopicClick,
                    onCustomTopicCardClick = { showCustomTopicDialog = true },
                    onGoToShopClick = onGoToShopClick
                )
            }
        }
    }

    if (showCustomTopicDialog) {
        AlertDialog(
            onDismissRequest = {
                showCustomTopicDialog = false
                customTopicText = ""
            },
            title = {
                Text(stringResource(Res.string.enter_custom_topic))
            },
            text = {
                Column {
                    Text(stringResource(Res.string.type_topic_hint))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customTopicText,
                        onValueChange = { customTopicText = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customTopicText.isNotBlank()) {
                            onClickCustomTopic(customTopicText)
                            showCustomTopicDialog = false
                            customTopicText = ""
                        }
                    },
                    enabled = customTopicText.isNotBlank()
                ) {
                    Text(stringResource(Res.string.start_chat))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCustomTopicDialog = false
                        customTopicText = ""
                    }
                ) {
                    Text(stringResource(Res.string.cancel))
                }
            }
        )
    }
}

@Composable
fun CustomTopicCard(
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(Res.string.start_your_topic),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = stringResource(Res.string.start_your_topic),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stringResource(Res.string.price),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                200.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Image(
                                painter = painterResource(Res.drawable.coins),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .fillMaxHeight()
                            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stringResource(Res.string.gain),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "0",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Image(
                                painter = painterResource(Res.drawable.icon_experience),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                100.toString(),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Image(
                                painter = painterResource(Res.drawable.coins),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopicsContent(
    topics: List<Topic>,
    currentUserXp: Int,
    currentUserCoins: Int,
    onTopicClick: (String, String) -> Unit,
    onCustomTopicCardClick: () -> Unit,
    onGoToShopClick: () -> Unit // Add this lambda parameter
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val adaptiveLazyGridSize =
        if (adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED ||
            adaptiveInfo.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.MEDIUM
        ) 260.dp
        else 140.dp
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(adaptiveLazyGridSize),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(start = 8.dp, end = 8.dp, bottom = 8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            CustomTopicCard(
                onClick = {
                    onCustomTopicCardClick()
                }
            )
        }
        items(topics) { topic ->
            TopicItem(
                topic,
                currentUserXp,
                currentUserCoins,
                onTopicClick,
                onGoToShopClick = onGoToShopClick
            )
        }
    }
}