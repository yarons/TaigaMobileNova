package com.grappim.taigamobile.testing.repo

import com.grappim.taigamobile.feature.login.domain.model.AuthData
import com.grappim.taigamobile.feature.login.domain.repo.AuthRepository

class FakeAuthRepository : AuthRepository {
    var authResult: Result<Unit> = Result.success(Unit)
    var authCalledWith: AuthData? = null
    var authCallCount: Int = 0

    var githubAuthResult: Result<Unit> = Result.success(Unit)
    var githubAuthCalledWithServer: String? = null
    var githubAuthCalledWithCode: String? = null
    var githubAuthCallCount: Int = 0

    override suspend fun auth(authData: AuthData): Result<Unit> {
        authCalledWith = authData
        authCallCount++
        return authResult
    }

    override suspend fun githubAuth(taigaServer: String, code: String): Result<Unit> {
        githubAuthCalledWithServer = taigaServer
        githubAuthCalledWithCode = code
        githubAuthCallCount++
        return githubAuthResult
    }
}
