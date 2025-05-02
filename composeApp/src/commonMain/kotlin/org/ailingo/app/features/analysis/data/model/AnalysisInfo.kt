package org.ailingo.app.features.analysis.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AnalysisInfo(
    val messageId: String,
    val originalText: String,
    val analysisType: String,
    val issues: List<IssuesMessage>?
)