package org.ailingo.app.features.basicauth.domain.repository

interface AuthRepository {
    suspend fun saveBasicAuth(basicAuthString: String)
    suspend fun getBasicAuth(): String?
    suspend fun deleteBasicAuth()
}