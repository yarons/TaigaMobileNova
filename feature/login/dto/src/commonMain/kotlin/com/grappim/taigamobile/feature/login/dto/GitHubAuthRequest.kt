package com.grappim.taigamobile.feature.login.dto

import kotlinx.serialization.Serializable

@Serializable
data class GitHubAuthRequest(val code: String, val type: String = "github")
