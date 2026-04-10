package com.grappim.taigamobile.feature.login.domain.github

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class GitHubOAuthUrlGeneratorTest {

    @Test
    fun `buildUrl contains correct base url`() {
        val url = GitHubOAuthUrlGenerator.buildUrl("my_client_id")
        assertTrue(url.startsWith("https://github.com/login/oauth/authorize"))
    }

    @Test
    fun `buildUrl includes client_id query parameter`() {
        val clientId = "test_client_123"
        val url = GitHubOAuthUrlGenerator.buildUrl(clientId)
        assertTrue(url.contains("client_id=$clientId"))
    }

    @Test
    fun `buildUrl includes user email scope`() {
        val url = GitHubOAuthUrlGenerator.buildUrl("any_id")
        assertTrue(url.contains("scope=user:email"))
    }

    @Test
    fun `buildUrl formats correctly`() {
        val clientId = "abc123"
        val expected = "https://github.com/login/oauth/authorize?client_id=$clientId&scope=user:email"
        assertEquals(expected, GitHubOAuthUrlGenerator.buildUrl(clientId))
    }
}
