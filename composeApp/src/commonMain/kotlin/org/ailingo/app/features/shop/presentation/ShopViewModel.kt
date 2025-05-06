package org.ailingo.app.features.shop.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.features.shop.data.model.ShopItem
import org.ailingo.app.features.shop.domain.repository.ShopRepository

class ShopViewModel(
    private val shopRepository: ShopRepository,
) : ViewModel() {
    private val _availableItemsState = MutableStateFlow<UiState<List<ShopItemUiState>>>(UiState.Idle())
    val availableItemsState = _availableItemsState.asStateFlow()

    init {
        onEvent(ShopEvent.OnGetAvailableItems)
    }

    fun onEvent(event: ShopEvent) {
        when (event) {
            is ShopEvent.OnGetAvailableItems -> getAvailableItems()
            is ShopEvent.OnPurchaseCoins -> purchaseCoins(event.itemId)
        }
    }

    fun getAvailableItems() {
        viewModelScope.launch {
            shopRepository.getAvailableItems().collect { uiState ->
                _availableItemsState.update { currentState ->
                    when (uiState) {
                        is UiState.Loading -> UiState.Loading()
                        is UiState.Error -> UiState.Error(uiState.message)
                        is UiState.Success -> {
                            val currentItems = (currentState as? UiState.Success)?.data ?: emptyList()
                            val newItems = uiState.data.map { shopItem ->
                                val existingItemState = currentItems.find { it.shopItem.id == shopItem.id }
                                ShopItemUiState(
                                    shopItem = shopItem,
                                    purchaseUiState = existingItemState?.purchaseUiState ?: UiState.Idle()
                                )
                            }
                            UiState.Success(newItems)
                        }
                        is UiState.Idle -> UiState.Idle()
                    }
                }
            }
        }
    }

    fun purchaseCoins(itemId: Long) {
        val currentListState = _availableItemsState.value
        if (currentListState is UiState.Success) {
            val itemToPurchase = currentListState.data.find { it.shopItem.id == itemId }
            if (itemToPurchase?.purchaseUiState is UiState.Loading) {
                return
            }

            _availableItemsState.update { currentState ->
                if (currentState is UiState.Success) {
                    val updatedList = currentState.data.map { item ->
                        if (item.shopItem.id == itemId) {
                            item.copy(purchaseUiState = UiState.Loading())
                        } else {
                            item
                        }
                    }
                    UiState.Success(updatedList)
                } else {
                    currentState
                }
            }

            viewModelScope.launch {
                shopRepository.purchaseCoins(itemId).collect { purchaseResultState ->
                    _availableItemsState.update { currentState ->
                        if (currentState is UiState.Success) {
                            val updatedList = currentState.data.map { item ->
                                if (item.shopItem.id == itemId) {
                                    item.copy(purchaseUiState = purchaseResultState)
                                } else {
                                    item
                                }
                            }
                            UiState.Success(updatedList)
                        } else {
                            currentState
                        }
                    }
                }
            }
        }
    }
}

data class ShopItemUiState(
    val shopItem: ShopItem,
    val purchaseUiState: UiState<String> = UiState.Idle()
)
