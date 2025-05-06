package org.ailingo.app.features.shop.presentation

// Import the new presentation data class
import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.coins
import ailingo.composeapp.generated.resources.coins_for_shop
import ailingo.composeapp.generated.resources.emptystate
import ailingo.composeapp.generated.resources.errorstate
import ailingo.composeapp.generated.resources.here_is_empty
import ailingo.composeapp.generated.resources.loadingstate
import ailingo.composeapp.generated.resources.shop_button_bought
import ailingo.composeapp.generated.resources.shop_button_buy
import ailingo.composeapp.generated.resources.shop_button_failed
import ailingo.composeapp.generated.resources.shop_button_processing
import ailingo.composeapp.generated.resources.shop_item_get_coins
import ailingo.composeapp.generated.resources.shop_item_price
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.ailingo.app.core.presentation.EmptyScreen
import org.ailingo.app.core.presentation.ErrorScreen
import org.ailingo.app.core.presentation.LoadingScreen
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.core.presentation.custom.CustomButton
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun ShopScreen(
    // Now receives UiState of a list of ShopItemUiState
    availableItemsState: UiState<List<ShopItemUiState>>,
    onClaim: (itemId: Long) -> Unit
) {
    when (availableItemsState) {
        is UiState.Error -> {
            ErrorScreen(modifier = Modifier.fillMaxSize(), errorMessage = availableItemsState.message, image = Res.drawable.errorstate)
        }

        is UiState.Idle -> {}
        is UiState.Loading -> {
            LoadingScreen(modifier = Modifier.fillMaxSize(), image = Res.drawable.loadingstate)
        }

        is UiState.Success -> {
            if (availableItemsState.data.isEmpty()) {
                EmptyScreen(text = Res.string.here_is_empty, image = Res.drawable.emptystate, modifier = Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Iterate through ShopItemUiState list
                    items(availableItemsState.data, key = { it.shopItem.id }) { itemUiState ->
                        ShopItemCard(
                            itemUiState = itemUiState, // Pass the ShopItemUiState
                            onClaim = { onClaim(itemUiState.shopItem.id) } // Pass the item ID to the event
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShopItemCard(
    // Now receives ShopItemUiState
    itemUiState: ShopItemUiState,
    onClaim: () -> Unit,
    // Removed purchaseCoinsState parameter
) {
    val alpha: Float by animateFloatAsState(targetValue = 1f)

    // Get the specific purchase state for THIS item
    val purchaseState = itemUiState.purchaseUiState

    val isButtonEnabled = purchaseState !is UiState.Loading

    val buttonTextResource = when (purchaseState) {
        is UiState.Loading -> Res.string.shop_button_processing
        is UiState.Success -> Res.string.shop_button_bought
        is UiState.Error -> Res.string.shop_button_failed
        else -> Res.string.shop_button_buy // Includes UiState.Idle
    }

    // Use data from the wrapped ShopItem
    val item = itemUiState.shopItem

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { this.alpha = alpha },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(Res.drawable.coins_for_shop),
                    error = painterResource(Res.drawable.coins_for_shop)
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                startY = 0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )

                Text(
                    text = item.name.uppercase(),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(Res.drawable.coins),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = stringResource(Res.string.shop_item_get_coins),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "${item.coinsToGive}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${stringResource(Res.string.shop_item_price)} ${item.price} RUB",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            CustomButton(
                onClick = onClaim, // This will trigger the ViewModel's purchaseCoins with the correct item ID
                enabled = isButtonEnabled, // Button enabled based on THIS item's state
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(buttonTextResource)) // Text based on THIS item's state
            }
        }
    }
}