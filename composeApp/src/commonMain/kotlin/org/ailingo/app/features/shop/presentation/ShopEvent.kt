package org.ailingo.app.features.shop.presentation

sealed class ShopEvent {
    data class OnPurchaseCoins(val itemId: Long) : ShopEvent()
    data object OnGetAvailableItems : ShopEvent()
}