package org.ailingo.app.features.dictionary.historysearch.di

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    suspend fun createDriver(
        name: String
    ): SqlDriver
}