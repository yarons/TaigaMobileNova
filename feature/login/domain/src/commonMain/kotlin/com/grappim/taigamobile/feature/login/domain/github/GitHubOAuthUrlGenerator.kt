package com.grappim.taigamobile.feature.login.domain.github

object GitHubOAuthUrlGenerator {

    private const val GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize"
    private const val SCOPE = "user:email"

    fun buildUrl(clientId: String): String =
        "$GITHUB_AUTH_URL?client_id=$clientId&scope=$SCOPE"
}
