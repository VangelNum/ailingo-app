package org.ailingo.app.features.basicauth.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import org.ailingo.app.di.SharedDatabase
import org.ailingo.app.features.basicauth.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val sharedDatabase: SharedDatabase
) : AuthRepository {

    override suspend fun saveBasicAuth(basicAuthString: String) {
        sharedDatabase { database ->
            database.authCredentialsQueries.transaction {
                database.authCredentialsQueries.deleteCredentials()
                database.authCredentialsQueries.insertCredentials(basicAuthString)
            }
        }
    }

    override suspend fun getBasicAuth(): String? {
        return sharedDatabase { database ->
            database.authCredentialsQueries
                .getCredentials()
                .awaitAsOneOrNull()
                ?.basicAuthString
        }
    }

    override suspend fun deleteBasicAuth() {
        sharedDatabase { database ->
            database.authCredentialsQueries.deleteCredentials()
        }
    }
}