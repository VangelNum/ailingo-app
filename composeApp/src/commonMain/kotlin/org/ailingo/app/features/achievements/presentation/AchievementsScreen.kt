package org.ailingo.app.features.achievements.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.claim
import ailingo.composeapp.generated.resources.claimed
import ailingo.composeapp.generated.resources.coins
import ailingo.composeapp.generated.resources.coins_reward
import ailingo.composeapp.generated.resources.experience_reward
import ailingo.composeapp.generated.resources.icon_experience
import ailingo.composeapp.generated.resources.loadingstate
import ailingo.composeapp.generated.resources.locked
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.ailingo.app.core.presentation.ErrorScreen
import org.ailingo.app.core.presentation.LoadingScreen
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.achievements.data.model.Achievement
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AchievementsScreen(
    achievementUiState: UiState<List<Achievement>>,
    claimAchievementState: UiState<Boolean>,
    onEvent: (AchievementsEvent) -> Unit
) {
    when (achievementUiState) {
        is UiState.Error -> {
            ErrorScreen(achievementUiState.message)
        }

        is UiState.Idle -> {}
        is UiState.Loading -> {
            LoadingScreen(modifier = Modifier.fillMaxSize(), image = Res.drawable.loadingstate)
        }

        is UiState.Success -> {
            val items = achievementUiState.data.sortedBy { achievement ->
                when {
                    achievement.isAvailable -> 0
                    achievement.claimed -> 1
                    else -> 2
                }
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items) { achievement ->
                    AchievementItem(
                        achievement = achievement,
                        onEvent = onEvent,
                        claimAchievementState = claimAchievementState
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun AchievementItem(
    achievement: Achievement,
    onEvent: (AchievementsEvent) -> Unit,
    claimAchievementState: UiState<Boolean>
) {
    val isClaimingInProgress = claimAchievementState is UiState.Loading
    val cardAlpha = if (!achievement.isAvailable && !achievement.claimed) 0.8f else 1f
    val cardElevation = if (!achievement.isAvailable && !achievement.claimed) CardDefaults.cardElevation(defaultElevation = 4.dp) else CardDefaults.cardElevation(defaultElevation = 6.dp)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(cardAlpha),
        shape = MaterialTheme.shapes.medium,
        elevation = cardElevation,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = achievement.imageUrl,
                contentDescription = achievement.description,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = achievement.description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(Res.drawable.coins),
                        contentDescription = stringResource(Res.string.coins_reward),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = achievement.coins.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Image(
                        painter = painterResource(Res.drawable.icon_experience),
                        contentDescription = stringResource(Res.string.experience_reward),
                        modifier = Modifier.size(20.dp),
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = achievement.xp.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Box(
                modifier = Modifier.widthIn(min = 90.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                when {
                    achievement.claimed -> {
                        Text(
                            text = stringResource(Res.string.claimed),
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    achievement.isAvailable -> {
                        Button(
                            onClick = {
                                achievement.achievementId?.let { id ->
                                    onEvent(AchievementsEvent.OnClaimAchievement(id))
                                }
                            },
                            enabled = !isClaimingInProgress && achievement.achievementId != null,
                            modifier = Modifier.defaultMinSize(minHeight = OutlinedTextFieldDefaults.MinHeight),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        ) {
                            if (isClaimingInProgress) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 3.dp
                                )
                            } else {
                                Text(
                                    text = stringResource(Res.string.claim),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }

                    else -> {
                        Text(
                            text = stringResource(Res.string.locked),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}