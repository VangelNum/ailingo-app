package org.ailingo.app.features.dictionary.historysearch.di

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import org.ailingo.app.AppDatabase

actual class DriverFactory(private val context: Context) {
    actual suspend fun createDriver(name: String): SqlDriver {
        return AndroidSqliteDriver(AppDatabase.Schema.synchronous(), context, name)
    }
}