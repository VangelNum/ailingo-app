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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.min // Import the min function
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

private const val DISPLAY_STREAK_CIRCLES = 10

@OptIn(ExperimentalMaterial3Api::class, ExperimentalTime::class)
@Composable
fun DailyBonusScreen(
    dailyBonusInfoState: UiState<DailyBonusInfo>,
    claimDailyBonusState: UiState<DailyBonusInfo>,
    onEvent: (DailyBonusEvent) -> Unit,
    onRefreshUserInfo: () -> Unit
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
                        // Fetch updated info when time runs out
                        onEvent(DailyBonusEvent.OnGetDailyBonusInfo)
                    }
                } finally {
                    // Ensure timer is reset if LaunchedEffect is cancelled
                    // (though getting info should handle this state naturally)
                    if (currentRemainingTime != null && currentRemainingTime!! > 0) {
                        // Timer cancelled before finishing, keep state
                    } else {
                        // Timer finished or started at 0
                        currentRemainingTime = null
                    }
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
                onRefreshUserInfo() // Refresh user info to reflect coin gain
                SnackbarController.sendEvent(SnackbarEvent(message = claimDailyBonusState.data.message))
                onEvent(DailyBonusEvent.OnGetDailyBonusInfo) // Fetch updated daily bonus info
            }

            is UiState.Error -> {
                SnackbarController.sendEvent(SnackbarEvent(message = claimDailyBonusState.message))
                onEvent(DailyBonusEvent.OnGetDailyBonusInfo) // Fetch updated daily bonus info
            }

            else -> {} // Do nothing for Loading or Idle
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
                        8.dp
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
                            .fillMaxWidth(),
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
                                text = "TAKE YOUR COINS", // Consider making this a resource string
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
                                // Calculate the range of days to display
                                // Start from 1 or streak - DISPLAY_STREAK_CIRCLES / 2, ensuring it's at least 1
                                val startDay = maxOf(1, dailyBonusInfo.streak - DISPLAY_STREAK_CIRCLES / 2 + 1) // Start slightly before the streak, centered around the *next* potential claim
                                val endDay = startDay + DISPLAY_STREAK_CIRCLES - 1

                                for (day in startDay..endDay) {
                                    DailyBonusCircle(
                                        day = day,
                                        streak = dailyBonusInfo.streak,
                                        // A day is available to claim if it's the *next* day in the streak
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
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    CustomButton(
                        onClick = {
                            if (dailyBonusInfo.isAvailable && !claiming) {
                                onEvent(DailyBonusEvent.OnClaimDailyBonus)
                            }
                        },
                        enabled = dailyBonusInfo.isAvailable && !claiming,
                        shape = RoundedCornerShape(16.dp)
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

    // Calculate coins for this day
    val coinsForDay = min(day * 5, 50)

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isClaimed -> MaterialTheme.colorScheme.primary
            isAvailableToClaim -> MaterialTheme.colorScheme.primaryContainer
            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) // Color for future unclaimed days
        }, label = "circleBackgroundColor"
    )

    val borderColor by animateColorAsState(
        targetValue = when {
            isClaimed -> MaterialTheme.colorScheme.primary // Claimed day uses primary border (or maybe transparent/none?)
            isAvailableToClaim -> MaterialTheme.colorScheme.primary // Available day uses primary border
            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f) // Future unclaimed days use outline
        }, label = "circleBorderColor"
    )


    val textColor by animateColorAsState(
        targetValue = when {
            isClaimed -> MaterialTheme.colorScheme.onPrimary // Claimed day text color (though icon is shown)
            isAvailableToClaim -> MaterialTheme.colorScheme.onPrimaryContainer // Available day text color
            else -> MaterialTheme.colorScheme.onSurfaceVariant // Future unclaimed days text color
        }, label = "circleTextColor"
    )

    // Pulsing animation only for the available day
    val infiniteTransition = rememberInfiniteTransition(label = "borderPulse")
    val animatedBorderStrokeWidth by infiniteTransition.animateFloat(
        initialValue = 1.dp.value, // Start from 1dp width
        targetValue = 2.dp.value,   // Animate to 2dp width
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
                width = if (isAvailableToClaim) animatedBorderStrokeWidth.dp else 1.dp, // Use animated width if available
                color = borderColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isClaimed) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Day $day claimed", // Or "Bonus for day $day claimed"
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(28.dp)
            )
        } else {
            // Show the coin amount for unclaimed days
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "+$coinsForDay", // Show coins with a plus sign
                    fontSize = 16.sp, // Adjusted font size slightly to fit "+50"
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                // Optional: add a small text "coins" or an icon if desired
                // Text("coins", fontSize = 8.sp, color = textColor.copy(alpha = 0.8f))
            }
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
        // Only show minutes if there are hours or if minutes > 0
        if (minutes > 0 || hours > 0) append("${minutes}m ")
        // Always show seconds if totalSeconds > 0, unless hours/minutes cover it sufficiently (optional refinement)
        // For simplicity, show seconds if totalSeconds < 1 hour
        if (totalSeconds < 3600 || (totalSeconds >= 3600 && seconds > 0)) {
            append("${seconds}s")
        }
    }.trim()
}

@Preview
@Composable
fun DailyBonusScreenPreview() {
    CompositionLocalProvider() { // Assuming CompositionLocalProvider provides necessary context like MaterialTheme
        DailyBonusScreen(
            dailyBonusInfoState = UiState.Success(
                // Example data: streak of 5, next bonus is available
                DailyBonusInfo(
                    streak = 5,
                    coinsRewarded = 10, // This field in the model seems to be the *last* amount received, not the next.
                    message = "Bonus Claimed!",
                    totalRemainingTimeSeconds = 0, // 0 means available or already claimed
                    isAvailable = true // Set to true to show available state
                )
            ),
            claimDailyBonusState = UiState.Idle(),
            onEvent = {},
            onRefreshUserInfo = {}
        )
    }
}

@Preview
@Composable
fun DailyBonusScreenClaimedPreview() {
    CompositionLocalProvider() {
        DailyBonusScreen(
            dailyBonusInfoState = UiState.Success(
                // Example data: streak of 6, claimed for today, next in 23 hours
                DailyBonusInfo(
                    streak = 6,
                    coinsRewarded = 30,
                    message = "Bonus claimed today",
                    totalRemainingTimeSeconds = 23 * 3600 + 59 * 60 + 59, // ~24 hours
                    isAvailable = false // Set to false to show claimed/timer state
                )
            ),
            claimDailyBonusState = UiState.Idle(),
            onEvent = {},
            onRefreshUserInfo = {}
        )
    }
}

@Preview
@Composable
fun DailyBonusScreenLoadingPreview() {
    CompositionLocalProvider() {
        DailyBonusScreen(
            dailyBonusInfoState = UiState.Loading(),
            claimDailyBonusState = UiState.Idle(),
            onEvent = {},
            onRefreshUserInfo = {}
        )
    }
}

@Preview
@Composable
fun DailyBonusCirclePreview() {
    Column {
        // Claimed day 3 (shows checkmark)
        DailyBonusCircle(day = 3, streak = 5, isAvailableToClaim = false)
        // Day 6 (not claimed, not available, shows coins for day 6 = 30)
        DailyBonusCircle(day = 6, streak = 5, isAvailableToClaim = false)
        // Day 6 (not claimed, IS available, shows coins for day 6 = 30, pulses)
        DailyBonusCircle(day = 6, streak = 5, isAvailableToClaim = true)
        // Day 10 (not claimed, not available, shows coins for day 10 = 50)
        DailyBonusCircle(day = 10, streak = 5, isAvailableToClaim = false)
        // Day 11 (not claimed, not available, shows coins for day 11 = 50)
        DailyBonusCircle(day = 11, streak = 5, isAvailableToClaim = false)
    }
}