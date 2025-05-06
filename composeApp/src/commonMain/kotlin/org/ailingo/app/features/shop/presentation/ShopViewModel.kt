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
    // State now holds a list of ShopItemUiState, each with its own purchase state
    private val _availableItemsState = MutableStateFlow<UiState<List<ShopItemUiState>>>(UiState.Idle())
    val availableItemsState = _availableItemsState.asStateFlow()

    // Remove the global _purchaseCoinsState

    init {
        onEvent(ShopEvent.OnGetAvailableItems)
    }

    fun onEvent(event: ShopEvent) {
        when (event) {
            is ShopEvent.OnGetAvailableItems -> getAvailableItems()
            is ShopEvent.OnPurchaseCoins -> purchaseCoins(event.itemId)
        }
    }

    // Fetch items and map them to ShopItemUiState with initial Idle state
    fun getAvailableItems() {
        viewModelScope.launch {
            shopRepository.getAvailableItems().collect { uiState ->
                _availableItemsState.update { currentState ->
                    when (uiState) {
                        is UiState.Loading -> UiState.Loading()
                        is UiState.Error -> UiState.Error(uiState.message)
                        is UiState.Success -> {
                            // Map fetched ShopItems to ShopItemUiState, preserving existing state if possible
                            // or initializing to Idle
                            val currentItems = (currentState as? UiState.Success)?.data ?: emptyList()
                            val newItems = uiState.data.map { shopItem ->
                                // Try to find the existing item state to preserve purchase state
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

    // Purchase coins for a specific item
    fun purchaseCoins(itemId: Long) {
        // Ensure we are not already processing for this item or globally
        val currentListState = _availableItemsState.value
        if (currentListState is UiState.Success) {
            val itemToPurchase = currentListState.data.find { it.shopItem.id == itemId }
            if (itemToPurchase?.purchaseUiState is UiState.Loading) {
                // Already processing this item
                return
            }

            // Update the specific item's state to Loading
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
                    currentState // Should ideally be Success when purchase is initiated
                }
            }

            // Launch the purchase coroutine
            viewModelScope.launch {
                shopRepository.purchaseCoins(itemId).collect { purchaseResultState ->
                    // Update the specific item's state based on the purchase result
                    _availableItemsState.update { currentState ->
                        if (currentState is UiState.Success) {
                            val updatedList = currentState.data.map { item ->
                                if (item.shopItem.id == itemId) {
                                    item.copy(purchaseUiState = purchaseResultState) // Update with Success/Error
                                } else {
                                    item
                                }
                            }
                            UiState.Success(updatedList)
                        } else {
                            currentState // Should ideally be Success
                        }
                    }
                }
            }
        }
    }
}

data class ShopItemUiState(
    val shopItem: ShopItem,
    val purchaseUiState: UiState<String> = UiState.Idle() // State specifically for this item's purchase
)