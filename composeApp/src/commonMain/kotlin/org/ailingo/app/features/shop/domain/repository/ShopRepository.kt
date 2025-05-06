package org.ailingo.app.features.shop.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.shop.data.model.ShopItem

interface ShopRepository {
    fun getAvailableItems(): Flow<UiState<List<ShopItem>>>
    fun purchaseCoins(itemId: Long): Flow<UiState<String>>
}