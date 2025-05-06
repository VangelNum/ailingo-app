package org.ailingo.app.features.topics.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.action_cancel
import ailingo.composeapp.generated.resources.action_confirm
import ailingo.composeapp.generated.resources.action_ok
import ailingo.composeapp.generated.resources.coins
import ailingo.composeapp.generated.resources.defaultProfilePhoto
import ailingo.composeapp.generated.resources.gain
import ailingo.composeapp.generated.resources.go_to_shop
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
    onTopicClick: (String, String) -> Unit,
    onGoToShopClick: () -> Unit
) {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f), Color.Transparent)
    )

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showXpRequirementDialog by remember { mutableStateOf(false) }
    var showInsufficientCoinsDialog by remember { mutableStateOf(false) }

    val requiredXp = topic.level * 100
    val canAccessTopicByXp = currentUserXp >= requiredXp || topic.level == 0
    val enoughtMoneyForStartTopic = currentUserCoins >= topic.price

    // Dialogs
    ConfirmationDialog(
        showDialog = showConfirmationDialog,
        topicName = topic.name,
        topicPrice = topic.price.toString(),
        onConfirm = {
            onTopicClick(topic.name, topic.imageUrl ?: DEFAULT_IMAGE_URL)
            showConfirmationDialog = false
        },
        onDismiss = { showConfirmationDialog = false }
    )

    RequirementDialog(
        showDialog = showXpRequirementDialog,
        title = stringResource(Res.string.topic_xp_required_title),
        message = stringResource(Res.string.topic_xp_required_message, requiredXp, currentUserXp),
        onDismiss = { showXpRequirementDialog = false }
    )

    InsufficientCoinsDialog(
        showDialog = showInsufficientCoinsDialog,
        topicPrice = topic.price,
        currentUserCoins = currentUserCoins,
        onGoToShopClick = onGoToShopClick,
        onDismiss = { showInsufficientCoinsDialog = false }
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = when {
                topic.isCompleted -> MaterialTheme.colorScheme.primaryContainer
                !canAccessTopicByXp -> MaterialTheme.colorScheme.surfaceVariant
                !enoughtMoneyForStartTopic -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.primaryContainer
            }
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.clickable {
            when {
                !canAccessTopicByXp -> showXpRequirementDialog = true
                !enoughtMoneyForStartTopic -> showInsufficientCoinsDialog = true
                else -> showConfirmationDialog = true
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
                model = topic.imageUrl ?: DEFAULT_IMAGE_URL,
                contentScale = ContentScale.Crop,
                contentDescription = topic.name,
                alpha = if (!canAccessTopicByXp || !enoughtMoneyForStartTopic) 0.5f else 1f,
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
                    ErrorImage()
                }
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

            if (!canAccessTopicByXp || !enoughtMoneyForStartTopic) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = when {
                        !canAccessTopicByXp -> stringResource(Res.string.topic_required_xp, requiredXp)
                        !enoughtMoneyForStartTopic -> stringResource(Res.string.topic_insufficient_coins_title)
                        else -> null
                    },
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
            val backgroundColor = when {
                topic.isCompleted -> MaterialTheme.colorScheme.primary
                !canAccessTopicByXp || !enoughtMoneyForStartTopic -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.primary
            }

            val contentColorAlpha = when {
                topic.isCompleted -> 0.7f
                !canAccessTopicByXp || !enoughtMoneyForStartTopic -> 0.7f
                else -> 0.7f
            }

            val contentColor = when {
                topic.isCompleted -> MaterialTheme.colorScheme.onPrimary
                !canAccessTopicByXp || !enoughtMoneyForStartTopic -> MaterialTheme.colorScheme.onErrorContainer
                else -> MaterialTheme.colorScheme.onPrimary
            }

            val isReq = !canAccessTopicByXp || !enoughtMoneyForStartTopic
            val reqXpOrCoin = if (!canAccessTopicByXp) stringResource(
                Res.string.topic_required_xp,
                requiredXp
            ) else topic.price.toString()

            val gainExperience = topic.topicXp.toString()
            val gainCoin = topic.coinCompleteTopic.toString()
            TopicInfoRow(
                backgroundColor = backgroundColor,
                isReq = isReq,
                reqXpOrCoin = reqXpOrCoin,
                gainExperience = gainExperience,
                gainCoin = gainCoin,
                contentColorAlpha = contentColorAlpha,
                contentColor = contentColor
            )
        }
    }
}

@Composable
private fun TopicInfoRow(
    backgroundColor: Color,
    isReq: Boolean,
    reqXpOrCoin: String,
    gainExperience: String,
    gainCoin: String,
    contentColorAlpha: Float,
    contentColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                stringResource(if (isReq) Res.string.req else Res.string.price),
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = contentColorAlpha)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    reqXpOrCoin,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Image(
                    painter = painterResource(if (isReq) Res.drawable.icon_experience else Res.drawable.coins),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(contentColor.copy(alpha = 0.3f))
                .padding(vertical = 4.dp, horizontal = 2.dp)
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                stringResource(Res.string.gain),
                style = MaterialTheme.typography.bodySmall,
                color = contentColor.copy(alpha = contentColorAlpha)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    gainExperience,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Image(
                    painter = painterResource(Res.drawable.icon_experience),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    gainCoin,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
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

@Composable
fun ErrorImage() {
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
}

@Composable
fun ConfirmationDialog(
    showDialog: Boolean,
    topicName: String,
    topicPrice: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(Res.string.topic_confirmation_title)) },
            text = {
                Text(
                    stringResource(
                        Res.string.topic_confirmation_message,
                        topicName,
                        topicPrice
                    )
                )
            },
            confirmButton = {
                Button(onClick = onConfirm) {
                    Text(stringResource(Res.string.action_confirm))
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }
}

@Composable
fun RequirementDialog(
    showDialog: Boolean,
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text(stringResource(Res.string.action_cancel))
                }
            }
        )
    }
}

@Composable
fun InsufficientCoinsDialog(
    showDialog: Boolean,
    topicPrice: Int,
    currentUserCoins: Int,
    onGoToShopClick: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(Res.string.topic_insufficient_coins_title)) },
            text = {
                Text(
                    stringResource(
                        Res.string.topic_insufficient_coins_message,
                        topicPrice,
                        currentUserCoins
                    )
                )
            },
            confirmButton = {
                Button(onClick = {
                    onDismiss()
                    onGoToShopClick()
                }) {
                    Text(stringResource(Res.string.go_to_shop))
                }
            },
            dismissButton = {
                Button(onClick = onDismiss) {
                    Text(stringResource(Res.string.action_ok))
                }
            }
        )
    }
}