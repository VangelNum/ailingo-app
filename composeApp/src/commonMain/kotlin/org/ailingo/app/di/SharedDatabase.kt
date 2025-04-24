package org.ailingo.app.di

import app.cash.sqldelight.async.coroutines.awaitCreate
import org.ailingo.app.AppDatabase
import org.ailingo.app.features.dictionary.historysearch.di.DriverFactory

class SharedDatabase(
    private val driverFactory: DriverFactory,
) {
    private var database: AppDatabase? = null

    private suspend fun initDatabase() {
        if (database == null) {
            val driver = driverFactory.createDriver("ailingo.db")
            database = AppDatabase(driver).also {
                AppDatabase.Schema.awaitCreate(driver)
            }
        }
    }

    suspend operator fun <R> invoke(block: suspend (AppDatabase) -> R): R {
        initDatabase()
        return block(database!!)
    }
}