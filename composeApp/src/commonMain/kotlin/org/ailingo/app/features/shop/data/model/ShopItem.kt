package org.ailingo.app.features.shop.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ShopItem(
    val id: Long,
    val name: String,
    val  description: String,
    val price: Int,
    val coinsToGive: Int,
    val imageUrl: String? = null
)