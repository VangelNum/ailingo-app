package org.ailingo.app.features.dailybonus.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.claim_bonus
import ailingo.composeapp.generated.resources.claimed_for_today
import ailingo.composeapp.generated.resources.claiming
import ailingo.composeapp.generated.resources.daily_bonus_title
import ailingo.composeapp.generated.resources.loadingstate
import ailingo.composeapp.generated.resources.next_bonus_in
import ailingo.composeapp.generated.resources.retry
import ailingo.composeapp.generated.resources.streak_days
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.ailingo.app.core.presentation.ErrorScreen
import org.ailingo.app.core.presentation.LoadingScreen
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.core.presentation.custom.CustomButton
import org.ailingo.app.core.presentation.snackbar.SnackbarController
import org.ailingo.app.core.presentation.snackbar.SnackbarEvent
import org.ailingo.app.features.dailybonus.data.model.DailyBonusInfo
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

private const val DISPLAY_STREAK_CIRCLES = 10

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DailyBonusScreen(
    dailyBonusInfoState: UiState<DailyBonusInfo>,
    claimDailyBonusState: UiState<DailyBonusInfo>,
    onEvent: (DailyBonusEvent) -> Unit,
    onRefreshUserInfo:()->Unit
) {
    var currentRemainingTime by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(dailyBonusInfoState) {
        if (dailyBonusInfoState is UiState.Success) {
            val dailyBonusInfo = dailyBonusInfoState.data
            if (!dailyBonusInfo.isAvailable && dailyBonusInfo.totalRemainingTimeSeconds > 0) {
                currentRemainingTime = dailyBonusInfo.totalRemainingTimeSeconds
                try {
                    while (currentRemainingTime != null && currentRemainingTime!! > 0) {
                        delay(1.seconds)
                        currentRemainingTime = currentRemainingTime!! - 1
                    }
                    if (currentRemainingTime != null && currentRemainingTime!! <= 0) {
                        currentRemainingTime = null
                        onEvent(DailyBonusEvent.OnGetDailyBonusInfo)
                    }
                } finally {
                    currentRemainingTime = null
                }

            } else {
                currentRemainingTime = null
            }
        } else {
            currentRemainingTime = null
        }
    }

    LaunchedEffect(claimDailyBonusState) {
        when (claimDailyBonusState) {
            is UiState.Success -> {
                onRefreshUserInfo()
                SnackbarController.sendEvent(SnackbarEvent(message = claimDailyBonusState.data.message))
                onEvent(DailyBonusEvent.OnGetDailyBonusInfo)
            }

            is UiState.Error -> {
                SnackbarController.sendEvent(SnackbarEvent(message = claimDailyBonusState.message))
                onEvent(DailyBonusEvent.OnGetDailyBonusInfo)
            }

            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        when (dailyBonusInfoState) {
            is UiState.Loading, is UiState.Idle -> {
                LoadingScreen(modifier = Modifier.fillMaxSize(), image = Res.drawable.loadingstate)
            }

            is UiState.Error -> {
                ErrorScreen(errorMessage = dailyBonusInfoState.message, onButtonClick = {
                    onEvent(DailyBonusEvent.OnGetDailyBonusInfo)
                }, buttonMessage = stringResource(Res.string.retry))
            }

            is UiState.Success -> {
                val dailyBonusInfo = dailyBonusInfoState.data
                val claiming = claimDailyBonusState is UiState.Loading

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(
                        24.dp
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(Res.string.daily_bonus_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = stringResource(Res.string.streak_days, dailyBonusInfo.streak),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }


                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Your Progress Path",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            FlowRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(
                                    12.dp,
                                    Alignment.CenterHorizontally
                                ),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val startDay = maxOf(1, dailyBonusInfo.streak - DISPLAY_STREAK_CIRCLES / 2)
                                val endDay = startDay + DISPLAY_STREAK_CIRCLES -1


                                for (day in startDay..endDay) {
                                    DailyBonusCircle(
                                        day = day,
                                        streak = dailyBonusInfo.streak,
                                        isAvailableToClaim = dailyBonusInfo.isAvailable && day == dailyBonusInfo.streak + 1
                                    )
                                }
                            }
                        }
                    }


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        val displayTime = currentRemainingTime
                        if (!dailyBonusInfo.isAvailable && displayTime != null && displayTime > 0) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(Res.string.next_bonus_in),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = formatRemainingTime(displayTime),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else if (!dailyBonusInfo.isAvailable) {
                            Text(
                                text = stringResource(Res.string.claimed_for_today),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Spacer(modifier = Modifier.height(48.dp))
                        }
                    }


                    Spacer(modifier = Modifier.height(8.dp))

                    CustomButton(
                        onClick = {
                            if (dailyBonusInfo.isAvailable && !claiming) {
                                onEvent(DailyBonusEvent.OnClaimDailyBonus)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(56.dp),
                        enabled = dailyBonusInfo.isAvailable && !claiming
                    ) {
                        if (claiming) {
                            Text(stringResource(Res.string.claiming), modifier = Modifier.padding(end = 8.dp))
                            LinearProgressIndicator(
                                modifier = Modifier.size(24.dp).clip(CircleShape),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        } else {
                            Text(
                                text = if (dailyBonusInfo.isAvailable) stringResource(Res.string.claim_bonus)
                                else stringResource(Res.string.claimed_for_today),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun DailyBonusCircle(
    day: Int,
    streak: Int,
    isAvailableToClaim: Boolean
) {
    val isClaimed = day <= streak

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isClaimed -> MaterialTheme.colorScheme.primary
            isAvailableToClaim -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        }, label = "circleBackgroundColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isClaimed -> MaterialTheme.colorScheme.primary
            isAvailableToClaim -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        }, label = "circleBorderColor"
    )


    val textColor by animateColorAsState(
        targetValue = when {
            isClaimed -> MaterialTheme.colorScheme.onPrimary
            isAvailableToClaim -> MaterialTheme.colorScheme.onPrimaryContainer
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }, label = "circleTextColor"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "borderPulse")
    val animatedBorderStrokeWidth by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                1f at 0 using LinearEasing
                2f at 500 using LinearEasing
                1f at 1000 using LinearEasing
            },
        ), label = "animatedBorderWidth"
    )


    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = if (isAvailableToClaim) animatedBorderStrokeWidth.dp else 1.dp,
                color = borderColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isClaimed) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Day $day claimed",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )
        } else {
            Text(
                text = day.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
fun formatRemainingTime(totalSeconds: Long): String {
    if (totalSeconds <= 0) return "0s"

    val duration = totalSeconds.seconds
    val hours = duration.inWholeHours
    val minutes = duration.inWholeMinutes % 60
    val seconds = duration.inWholeSeconds % 60

    return buildString {
        if (hours > 0) append("${hours}h ")
        if (minutes > 0 || hours > 0) append("${minutes}m ")
        append("${seconds}s")
    }.trim()
}