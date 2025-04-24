package org.ailingo.app.features.dictionary.historysearch.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.ailingo.app.core.presentation.UiState
import org.ailingo.app.di.ErrorMapper
import org.ailingo.app.di.SharedDatabase
import org.ailingo.app.features.dictionary.historysearch.data.mapper.toHistoryDictionary
import org.ailingo.app.features.dictionary.historysearch.data.model.DictionarySearchHistory
import org.ailingo.app.features.dictionary.historysearch.domain.repository.DictionarySearchHistoryRepository

class DictionarySearchHistoryRepositoryImpl(
    private val sharedDatabase: SharedDatabase,
    private val errorMapper: ErrorMapper
) : DictionarySearchHistoryRepository {

    override fun getSearchHistory(): Flow<UiState<List<DictionarySearchHistory>>> = flow {
        emit(UiState.Loading())
        try {
            sharedDatabase { database ->
                database.historyDictionaryQueries
                    .getDictionaryHistory()
                    .asFlow()
                    .mapToList(Dispatchers.Default)
                    .map { historyEntities ->
                        historyEntities.map { historyEntity ->
                            historyEntity.toHistoryDictionary()
                        }
                    }.collect { mappedHistory ->
                        emit(UiState.Success(mappedHistory))
                    }
            }
        } catch (e: Exception) {
            emit(UiState.Error(errorMapper.mapError(e)))
        }
    }

    override suspend fun insertWordToSearchHistory(word: DictionarySearchHistory) {
        sharedDatabase { database ->
            database.historyDictionaryQueries.insertDictionaryHistory(
                id = word.id,
                text = word.text
            )
        }
    }

    override suspend fun deleteWordFromSearchHistory(id: Long) {
        sharedDatabase { database ->
            database.historyDictionaryQueries.deleteFromDictionaryHistory(id)
        }
    }
}