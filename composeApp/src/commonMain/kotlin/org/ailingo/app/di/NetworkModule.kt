package org.ailingo.app.di

import ailingo.composeapp.generated.resources.Res
import ailingo.composeapp.generated.resources.connection_timeout
import ailingo.composeapp.generated.resources.could_not_connect
import ailingo.composeapp.generated.resources.request_timeout
import ailingo.composeapp.generated.resources.unexpected_error
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.HttpRequestPipeline
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.AttributeKey
import kotlinx.coroutines.Deferred
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.ailingo.app.features.basicauth.domain.repository.AuthRepository
import org.jetbrains.compose.resources.getString
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single {
        HttpClient {
            expectSuccess = false
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) {
                level = LogLevel.ALL
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                socketTimeoutMillis = 15000
                connectTimeoutMillis = 15000
            }
            install(AuthTokenInterceptor) {
                authRepository = get(named("authRepository"))
                excludedPaths = listOf(
                    "/api/v1/user/me" to HttpMethod.Get,
                    "/api/v1/user/register" to HttpMethod.Post,
                    "/api/v1/user/verify-email" to HttpMethod.Post,
                    "/api/v1/user/resend-verification-code" to HttpMethod.Post
                )
            }
        }
    }

    single<ErrorMapper> {
        object : ErrorMapper {
            override suspend fun mapError(
                throwable: Throwable?,
                httpResponse: HttpResponse?
            ): String {
                if (httpResponse != null && !httpResponse.status.isSuccess()) {
                    return try {
                        val errorBody = httpResponse.body<JsonObject>()
                        errorBody["message"]?.jsonPrimitive?.content
                            ?: getString(Res.string.unexpected_error)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        getString(Res.string.unexpected_error)
                    }
                }
                return when (throwable) {
                    is HttpRequestTimeoutException -> getString(Res.string.request_timeout)
                    is ConnectTimeoutException -> getString(Res.string.could_not_connect)
                    is SocketTimeoutException -> getString(Res.string.connection_timeout)
                    else -> getString(Res.string.unexpected_error, throwable?.message.toString())
                }
            }
        }
    }
}

interface ErrorMapper {
    suspend fun mapError(throwable: Throwable? = null, httpResponse: HttpResponse? = null): String
}

class AuthTokenInterceptor(config: Config) {

    private val authRepository = config.authRepository
    private val excludedPaths = config.excludedPaths

    class Config {
        lateinit var authRepository: Deferred<AuthRepository>
        var excludedPaths: List<Pair<String, HttpMethod>> = listOf()
    }

    companion object Plugin : HttpClientPlugin<Config, AuthTokenInterceptor> {
        override val key: AttributeKey<AuthTokenInterceptor> = AttributeKey("AuthTokenInterceptor")

        override fun prepare(block: Config.() -> Unit): AuthTokenInterceptor {
            return AuthTokenInterceptor(Config().apply(block))
        }

        override fun install(plugin: AuthTokenInterceptor, scope: HttpClient) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Before) {
                if (plugin.isPathExcluded(context)) {
                    proceed()
                    return@intercept
                }

                val credentials = plugin.authRepository.await().getBasicAuth()
                if (credentials != null) {
                    context.header(HttpHeaders.Authorization, "Basic $credentials")
                    Logger.i("Basic Auth added")
                } else {
                    Logger.i("Basic Auth header empty")
                }
                proceed()
            }
        }
    }

    private fun isPathExcluded(request: HttpRequestBuilder): Boolean {
        val path = request.url.encodedPath
        val method = request.method
        return excludedPaths.any { (excludedPath, excludedMethod) ->
            path == excludedPath && method == excludedMethod
        }
    }
}