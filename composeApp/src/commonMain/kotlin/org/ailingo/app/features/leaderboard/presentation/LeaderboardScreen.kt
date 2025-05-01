package org.ailingo.app.features.leaderboard.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.coins
import ailingo.composeapp.generated.resources.defaultProfilePhoto
import ailingo.composeapp.generated.resources.emptystate
import ailingo.composeapp.generated.resources.leaderboard_is_empty
import ailingo.composeapp.generated.resources.loadingstate
import ailingo.composeapp.generated.resources.streak
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.ailingo.app.core.presentation.EmptyScreen
import org.ailingo.app.core.presentation.ErrorScreen
import org.ailingo.app.core.presentation.LoadingScreen
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.leaderboard.data.model.Leaderboard
import org.jetbrains.compose.resources.painterResource

@Composable
fun LeaderboardScreen(
    leaderboardState: UiState<List<Leaderboard>>
) {

    when (leaderboardState) {
        is UiState.Loading -> {
            LoadingScreen(modifier = Modifier.fillMaxSize(), image = Res.drawable.loadingstate)
        }

        is UiState.Error -> {
            ErrorScreen(leaderboardState.message, modifier = Modifier.fillMaxSize())
        }

        is UiState.Success -> {
            val leaders = leaderboardState.data
            if (leaders.isEmpty()) {
                EmptyScreen(modifier = Modifier.fillMaxSize(), image = Res.drawable.emptystate, text = Res.string.leaderboard_is_empty)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    itemsIndexed(leaders) { index, leader ->
                        LeaderboardItem(rank = index + 1, leaderboard = leader)
                    }
                }
            }
        }

        is UiState.Idle -> {}
    }
}

@Composable
fun LeaderboardItem(rank: Int, leaderboard: Leaderboard) {
    val isTopThree = rank <= 3
    val backgroundColor = if (isTopThree) {
        when (rank) {
            1 -> Color(0xFFFFE0B2) // Пастельный золотой
            2 -> Color(0xFFE0E0E0) // Пастельный серебряный
            3 -> Color(0xFFD2B48C) // Пастельный бронзовый
            else -> MaterialTheme.colorScheme.onSecondary
        }
    } else {
        MaterialTheme.colorScheme.onSecondary
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "$rank.",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isTopThree) FontWeight.Bold else FontWeight.Normal
                )
                Card(
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    if (leaderboard.avatar == null) {
                        Image(
                            painter = painterResource(Res.drawable.defaultProfilePhoto),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    1.dp,
                                    Color.Black,
                                    CircleShape
                                )
                        )
                    } else {
                        AsyncImage(
                            model = leaderboard.avatar,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    1.dp,
                                    Color.Black,
                                    CircleShape
                                ),
                            placeholder = rememberVectorPainter(Icons.Default.EmojiEvents),
                            error = rememberVectorPainter(Icons.Default.EmojiEvents),
                        )
                    }
                }
                Text(
                    text = leaderboard.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isTopThree) FontWeight.Bold else FontWeight.Normal
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${leaderboard.coins}",
                    fontSize = 14.sp,
                    fontWeight = if (isTopThree) FontWeight.Bold else FontWeight.Normal
                )
                Image(painter = painterResource(Res.drawable.coins), modifier = Modifier.size(24.dp), contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${leaderboard.streak}",
                    fontSize = 14.sp,
                    fontWeight = if (isTopThree) FontWeight.Bold else FontWeight.Normal
                )
                Image(painter = painterResource(Res.drawable.streak), modifier = Modifier.size(24.dp), contentDescription = null)
            }
        }
    }
}