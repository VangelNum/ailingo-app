package org.ailingo.app.features.dictionary.historysearch.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import org.ailingo.app.AppDatabase
import java.nio.file.FileSystems


actual class DriverFactory(private val appPath: String) {
    actual suspend fun createDriver(name: String): SqlDriver {
        val dbPath = appPath + FileSystems.getDefault().separator + name
        val driver = JdbcSqliteDriver(url = "jdbc:sqlite:$dbPath")
        AppDatabase.Schema.create(driver)
        return driver
    }
}