package org.ailingo.app.features.login.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val name: String,
    val password: String,
    val email: String,
    val avatar: String?,
    val role: String,
    val coins: Int,
    val xp: Int,
    val streak: Int,
    val verificationCode: String?,
    val isEmailVerified: Boolean?,
    val registrationTime: String,
    val lastLoginTime: String
)