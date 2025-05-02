package org.ailingo.app.features.analysis.data.model

import kotlinx.serialization.Serializable

@Serializable
data class IssuesMessage(
    val type: String,
    val text: String,
    val description: String?,
    val suggestion: String?
)