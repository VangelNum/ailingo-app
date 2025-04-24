package org.ailingo.app.features.dictionary.historysearch.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.WebWorkerDriver
import org.ailingo.app.AppDatabase
import org.w3c.dom.Worker

actual class DriverFactory {
    actual suspend fun createDriver(name: String): SqlDriver {
        return WebWorkerDriver(
            jsWorker()
        ).also { AppDatabase.Schema.create(it).await() }
    }
}

fun jsWorker(): Worker =
    js("""new Worker("/sqlite.worker.js")""")