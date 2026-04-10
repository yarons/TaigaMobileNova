package com.grappim.taigamobile.feature.login.data.github

import com.grappim.taigamobile.feature.login.domain.github.GitHubOAuthCallbackManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import org.koin.core.annotation.Single

@Single(binds = [GitHubOAuthCallbackManager::class])
class GitHubOAuthCallbackManagerImpl : GitHubOAuthCallbackManager {

    private val _pendingCode = Channel<String>(capacity = Channel.BUFFERED)
    override val pendingCode: Flow<String> = _pendingCode.receiveAsFlow()

    override fun onCallbackReceived(code: String) {
        _pendingCode.trySend(code)
    }
}
