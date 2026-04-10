package com.grappim.taigamobile.feature.login.domain.github

import kotlinx.coroutines.flow.Flow

interface GitHubOAuthCallbackManager {
    val pendingCode: Flow<String>
    fun onCallbackReceived(code: String)
}
