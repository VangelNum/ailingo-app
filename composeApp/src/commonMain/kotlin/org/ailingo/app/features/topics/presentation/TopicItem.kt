package org.ailingo.app.features.topics.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.action_cancel
import ailingo.composeapp.generated.resources.action_confirm
import ailingo.composeapp.generated.resources.action_ok
import ailingo.composeapp.generated.resources.coins
import ailingo.composeapp.generated.resources.defaultProfilePhoto
import ailingo.composeapp.generated.resources.gain
import ailingo.composeapp.generated.resources.icon_experience
import ailingo.composeapp.generated.resources.loading_error
import ailingo.composeapp.generated.resources.price
import ailingo.composeapp.generated.resources.req
import ailingo.composeapp.generated.resources.topic_confirmation_message
import ailingo.composeapp.generated.resources.topic_confirmation_title
import ailingo.composeapp.generated.resources.topic_insufficient_coins_message
import ailingo.composeapp.generated.resources.topic_insufficient_coins_title
import ailingo.composeapp.generated.resources.topic_required_xp
import ailingo.composeapp.generated.resources.topic_xp_required_message
import ailingo.composeapp.generated.resources.topic_xp_required_title
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.SubcomposeAsyncImage
import org.ailingo.app.core.presentation.SmallLoadingIndicator
import org.ailingo.app.features.topics.data.model.Topic
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

const val DEFAULT_IMAGE_URL = "https://i.ibb.co/YB1hWJWb/default-Profile-Photo.png"

@Composable
fun TopicItem(
    topic: Topic,
    currentUserXp: Int,
    currentUserCoins: Int,
    onTopicClick: (String, String) -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f), Color.Transparent)
    )

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showXpRequirementDialog by remember { mutableStateOf(false) }
    var showInsufficientCoinsDialog by remember { mutableStateOf(false) }

    val requiredXp = topic.level * 100
    val canAccessTopicByXp = currentUserXp >= requiredXp || topic.level == 0
    val canAffordTopic = currentUserCoins >= topic.price

    if (showInsufficientCoinsDialog) {
        AlertDialog(
            onDismissRequest = { showInsufficientCoinsDialog = false },
            title = { Text(stringResource(Res.string.topic_insufficient_coins_title)) },
            text = {
                Text(
                    stringResource(
                        Res.string.topic_insufficient_coins_message,
                        topic.price,
                        currentUserCoins
                    )
                )
            },
            confirmButton = {
                Button(onClick = { showInsufficientCoinsDialog = false }) {
                    Text(stringResource(Res.string.action_ok))
                }
            }
        )
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text(stringResource(Res.string.topic_confirmation_title)) },
            text = {
                Text(
                    stringResource(
                        Res.string.topic_confirmation_message,
                        topic.name,
                        topic.price.toString()
                    )
                )
            },
            confirmButton = {
                Button(onClick = {
                    onTopicClick(topic.name, topic.imageUrl?: DEFAULT_IMAGE_URL)
                    showConfirmationDialog = false
                }) {
                    Text(stringResource(Res.string.action_confirm))
                }
            },
            dismissButton = {
                Button(onClick = { showConfirmationDialog = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }

    if (showXpRequirementDialog) {
        AlertDialog(
            onDismissRequest = { showXpRequirementDialog = false },
            title = { Text(stringResource(Res.string.topic_xp_required_title)) },
            text = {
                Text(
                    stringResource(
                        Res.string.topic_xp_required_message,
                        requiredXp,
                        currentUserXp
                    )
                )
            },
            confirmButton = {
                Button(onClick = { showXpRequirementDialog = false }) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }

    Card(
        colors = CardDefaults.cardColors(
            containerColor =
                when {
                    topic.isCompleted -> MaterialTheme.colorScheme.primaryContainer
                    !canAccessTopicByXp -> MaterialTheme.colorScheme.surfaceVariant
                    !canAffordTopic -> MaterialTheme.colorScheme.surfaceVariant
                    else -> MaterialTheme.colorScheme.primaryContainer
                }
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clickable {
            when {
                !canAccessTopicByXp -> {
                    showXpRequirementDialog = true
                }
                !canAffordTopic -> {
                    showInsufficientCoinsDialog = true
                }
                else -> {
                    showConfirmationDialog = true
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                .clip(RoundedCornerShape(16.dp))
        ) {
            SubcomposeAsyncImage(
                model = topic.imageUrl?: DEFAULT_IMAGE_URL,
                contentScale = ContentScale.Crop,
                contentDescription = topic.name,
                alpha = if (!canAccessTopicByXp || !canAffordTopic) 0.5f else 1f,
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(gradient, blendMode = BlendMode.Multiply)
                        }
                    },
                loading = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        SmallLoadingIndicator()
                    }
                },
                error = {
                    if (LocalInspectionMode.current) {
                        Image(
                            painter = painterResource(Res.drawable.defaultProfilePhoto),
                            contentDescription = null
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                stringResource(Res.string.loading_error),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
            )

            Text(
                text = topic.name.uppercase(),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Color.Black.copy(alpha = 0.2f),
                        RoundedCornerShape(
                            bottomEnd = 16.dp,
                            bottomStart = 16.dp
                        )
                    )
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            )

            if (!canAccessTopicByXp || !canAffordTopic) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription =
                        if (!canAccessTopicByXp) stringResource(Res.string.topic_required_xp, requiredXp)
                        else if (!canAffordTopic) stringResource(Res.string.topic_insufficient_coins_title)
                        else null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(48.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
        ) {
            when {
                topic.isCompleted -> {
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
                                    topic.price.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Image(
                                    painter = painterResource(Res.drawable.coins),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
                                .padding(vertical = 4.dp)
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
                            }
                        }
                    }
                }

                !canAccessTopicByXp -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                stringResource(Res.string.req),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                            )
                            Text(
                                stringResource(
                                    Res.string.topic_required_xp,
                                    requiredXp
                                ),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.3f))
                                .padding(vertical = 4.dp)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                stringResource(Res.string.gain),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    topic.topicXp.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Image(painter = painterResource(Res.drawable.icon_experience), contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                !canAffordTopic -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                stringResource(Res.string.req),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    topic.price.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Image(
                                    painter = painterResource(Res.drawable.coins),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.3f))
                                .padding(vertical = 4.dp)
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                stringResource(Res.string.gain),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    topic.topicXp.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Image(painter = painterResource(Res.drawable.icon_experience), contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                else -> {
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
                                    topic.price.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Image(
                                    painter = painterResource(Res.drawable.coins),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight()
                                .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f))
                                .padding(vertical = 4.dp)
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
                                    topic.topicXp.toString(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Image(painter = painterResource(Res.drawable.icon_experience), contentDescription = null, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}