package org.ailingo.app.features.basicauth.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOne
import org.ailingo.app.AppDatabase
import org.ailingo.app.features.basicauth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    database: AppDatabase
) : AuthRepository {
    private val authCredentialsQueries = database.authCredentialsQueries

    override suspend fun saveBasicAuth(basicAuthString: String) {
        authCredentialsQueries.transaction {
            authCredentialsQueries.deleteCredentials()
            authCredentialsQueries.insertCredentials(basicAuthString)
        }
    }

    override suspend fun getBasicAuth(): String? {
        return authCredentialsQueries
            .getCredentials()
            .awaitAsOne()
    }

    override suspend fun deleteBasicAuth() {
        authCredentialsQueries.deleteCredentials()
    }
}