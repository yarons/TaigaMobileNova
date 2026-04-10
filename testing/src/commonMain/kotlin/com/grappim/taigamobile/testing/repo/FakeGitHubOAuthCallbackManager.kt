package com.grappim.taigamobile.testing.repo

import com.grappim.taigamobile.feature.login.domain.github.GitHubOAuthCallbackManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class FakeGitHubOAuthCallbackManager : GitHubOAuthCallbackManager {

    private val _pendingCode = MutableSharedFlow<String>(extraBufferCapacity = 1)
    override val pendingCode: Flow<String> = _pendingCode.asSharedFlow()

    var lastReceivedCode: String? = null

    override fun onCallbackReceived(code: String) {
        lastReceivedCode = code
        _pendingCode.tryEmit(code)
    }

    suspend fun emitCode(code: String) {
        _pendingCode.emit(code)
    }
}
