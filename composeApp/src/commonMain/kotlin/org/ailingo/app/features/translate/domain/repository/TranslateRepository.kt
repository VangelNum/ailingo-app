package org.ailingo.app.features.translate.domain.repository

import kotlinx.coroutines.flow.Flow
import org.ailingo.app.core.presentation.UiState

interface TranslateRepository {
    fun translate(text: String): Flow<UiState<String>>
}