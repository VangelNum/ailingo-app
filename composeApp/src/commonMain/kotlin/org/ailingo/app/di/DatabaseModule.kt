package org.ailingo.app.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.ailingo.app.AppDatabase
import org.ailingo.app.features.dictionary.historysearch.data.repository.DictionarySearchHistorySearchHistoryRepositoryImpl
import org.ailingo.app.features.dictionary.historysearch.di.DatabaseDriverFactory
import org.ailingo.app.features.dictionary.historysearch.domain.repository.DictionarySearchHistoryRepository
import org.ailingo.app.features.basicauth.data.repository.AuthRepositoryImpl
import org.ailingo.app.features.basicauth.domain.repository.AuthRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val databaseModule = module {
    single<Deferred<DictionarySearchHistoryRepository>>(named("dictionaryRepository")) {
        CoroutineScope(Dispatchers.Default).async {
            val driver = get<DatabaseDriverFactory>().provideDbDriver(
                AppDatabase.Schema
            )
            val db = AppDatabase(driver)
            DictionarySearchHistorySearchHistoryRepositoryImpl(db, get())
        }
    }
    single<Deferred<AuthRepository>>(named("authRepository")) {
        CoroutineScope(Dispatchers.Default).async {
            val driver = get<DatabaseDriverFactory>().provideDbDriver(
                AppDatabase.Schema
            )
            val db = AppDatabase(driver)
            AuthRepositoryImpl(db)
        }
    }
}