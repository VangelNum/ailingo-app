package org.ailingo.app.features.additional.presentation

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.achievements
import ailingo.composeapp.generated.resources.daily_bonus
import ailingo.composeapp.generated.resources.leaderboard
import ailingo.composeapp.generated.resources.profile
import ailingo.composeapp.generated.resources.profilepixel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun AdditionalScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToDailyBonus: () -> Unit,
) {
    val items: List<AdditionalItems> = listOf(
        AdditionalItems.Profile,
        AdditionalItems.Leaderboard,
        AdditionalItems.DailyBonus,
        AdditionalItems.Achievements,
    )

    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f), Color.Transparent)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        when (item) {
                            AdditionalItems.Profile -> onNavigateToProfile()
                            AdditionalItems.Leaderboard -> onNavigateToLeaderboard()
                            AdditionalItems.Achievements -> onNavigateToAchievements()
                            AdditionalItems.DailyBonus -> onNavigateToDailyBonus()
                        }
                    }
                    .height(150.dp),
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(item.image),
                        modifier = Modifier
                            .fillMaxSize().blur(10.dp)
                            .wrapContentHeight().drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(gradient, blendMode = BlendMode.Multiply)
                                }
                            },
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    Text(
                        text = stringResource(item.titleResId).uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        textAlign = TextAlign.Center,
                    )

                    Card(shape = MaterialTheme.shapes.large) {
                        Image(
                            painter = painterResource(item.image),
                            modifier = Modifier.size(118.dp),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                }

            }
        }
    }
}

sealed class AdditionalItems(
    val titleResId: StringResource,
    val image: DrawableResource
) {
    object Profile : AdditionalItems(
        titleResId = Res.string.profile,
        image = Res.drawable.profilepixel
    )

    object DailyBonus : AdditionalItems(
        titleResId = Res.string.daily_bonus,
        image = Res.drawable.daily_bonus
    )

    object Leaderboard : AdditionalItems(
        titleResId = Res.string.leaderboard,
        image = Res.drawable.leaderboard
    )

    object Achievements : AdditionalItems(
        titleResId = Res.string.achievements,
        image = Res.drawable.achievements
    )
}