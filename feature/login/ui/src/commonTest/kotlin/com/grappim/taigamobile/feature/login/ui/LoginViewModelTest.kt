@file:OptIn(ExperimentalCoroutinesApi::class)

package com.grappim.taigamobile.feature.login.ui

import app.cash.turbine.test
import com.grappim.taigamobile.feature.login.domain.model.AuthData
import com.grappim.taigamobile.feature.login.domain.model.AuthType
import com.grappim.taigamobile.testing.FakeAppInfoProvider
import com.grappim.taigamobile.testing.MainDispatcherRule
import com.grappim.taigamobile.testing.repo.FakeAuthRepository
import com.grappim.taigamobile.testing.repo.FakeGitHubOAuthCallbackManager
import com.grappim.taigamobile.testing.storage.FakeServerStorage
import com.grappim.taigamobile.testing.utils.getRandomString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class LoginViewModelTest {

    private val mainDispatcherRule = MainDispatcherRule()

    private lateinit var sut: LoginViewModel

    private val authRepository = FakeAuthRepository()
    private val defaultServer = getRandomString()
    private val serverStorage = FakeServerStorage(defaultServer)
    private val appInfoProvider = FakeAppInfoProvider(githubClientId = "test_github_client_id")
    private val gitHubOAuthCallbackManager = FakeGitHubOAuthCallbackManager()

    private val correctServer = "https://10.0.2.2:9000"

    @BeforeTest
    fun setup() {
        mainDispatcherRule.setup()
        sut = LoginViewModel(authRepository, serverStorage, appInfoProvider, gitHubOAuthCallbackManager)
    }

    @AfterTest
    fun tearDown() {
        mainDispatcherRule.tearDown()
    }

    @Test
    fun `on onActionDialogConfirm without error should login`() = runTest {
        val server = getRandomString()
        val authType = AuthType.LDAP
        val password = getRandomString()
        val username = getRandomString()

        val authData = AuthData(server, authType, password, username)

        sut.state.value.onServerValueChange(server)
        sut.state.value.onAuthTypeChange(authType)
        sut.state.value.onPasswordValueChange(password)
        sut.state.value.onLoginValueChange(username)

        sut.loginSuccessful.test {
            sut.state.value.onActionDialogConfirm()

            assertFalse(sut.state.value.isLoading)
            assertFalse(sut.state.value.isAlertVisible)

            assertTrue(awaitItem())
            assertFalse(sut.state.value.isLoading)

            assertEquals(authData, authRepository.authCalledWith)
        }
    }

    @Test
    fun `on validateAuthData with incorrect server should not login`() {
        val authType = AuthType.LDAP
        val password = getRandomString()
        val username = getRandomString()
        val incorrectServer = getRandomString()

        sut.state.value.onServerValueChange(incorrectServer)
        sut.state.value.onAuthTypeChange(authType)
        sut.state.value.onPasswordValueChange(password)
        sut.state.value.onLoginValueChange(username)

        sut.state.value.validateAuthData(authType)

        assertTrue(sut.state.value.isServerInputError)
        assertFalse(sut.state.value.isLoginInputError)
        assertFalse(sut.state.value.isPasswordInputError)

        assertEquals(0, authRepository.authCallCount)
    }

    @Test
    fun `on validateAuthData with empty login should not login`() {
        val authType = AuthType.LDAP
        val password = getRandomString()
        val username = ""

        sut.state.value.onServerValueChange(correctServer)
        sut.state.value.onAuthTypeChange(authType)
        sut.state.value.onPasswordValueChange(password)
        sut.state.value.onLoginValueChange(username)

        sut.state.value.validateAuthData(authType)

        assertFalse(sut.state.value.isServerInputError)
        assertTrue(sut.state.value.isLoginInputError)
        assertFalse(sut.state.value.isPasswordInputError)

        assertEquals(0, authRepository.authCallCount)
    }

    @Test
    fun `on validateAuthData with empty password should not login`() {
        val authType = AuthType.LDAP
        val password = ""
        val username = getRandomString()

        sut.state.value.onServerValueChange(correctServer)
        sut.state.value.onAuthTypeChange(authType)
        sut.state.value.onPasswordValueChange(password)
        sut.state.value.onLoginValueChange(username)

        sut.state.value.validateAuthData(authType)

        assertFalse(sut.state.value.isServerInputError)
        assertFalse(sut.state.value.isLoginInputError)
        assertTrue(sut.state.value.isPasswordInputError)

        assertEquals(0, authRepository.authCallCount)
    }

    @Test
    fun `on validateAuthData with valid data but without https in server should not login`() {
        val authType = AuthType.LDAP
        val password = getRandomString()
        val username = getRandomString()
        val server = "http://10.0.2.2:9000"

        sut.state.value.onServerValueChange(server)
        sut.state.value.onAuthTypeChange(authType)
        sut.state.value.onPasswordValueChange(password)
        sut.state.value.onLoginValueChange(username)

        sut.state.value.validateAuthData(authType)

        assertFalse(sut.state.value.isServerInputError)
        assertFalse(sut.state.value.isLoginInputError)
        assertFalse(sut.state.value.isPasswordInputError)

        assertTrue(sut.state.value.isAlertVisible)

        assertEquals(0, authRepository.authCallCount)
    }

    @Test
    fun `on onAuthTypeChange should change the authType`() {
        assertEquals(AuthType.NORMAL, sut.state.value.authType)

        sut.state.value.onAuthTypeChange(AuthType.LDAP)

        assertEquals(AuthType.LDAP, sut.state.value.authType)
    }

    @Test
    fun `on setIsAlertVisible should change the isAlertVisible`() {
        assertFalse(sut.state.value.isAlertVisible)

        sut.state.value.setIsAlertVisible(true)

        assertTrue(sut.state.value.isAlertVisible)
    }

    @Test
    fun `on changePasswordVisibility should change the isPasswordVisible`() {
        assertFalse(sut.state.value.isPasswordVisible)

        sut.state.value.setIsPasswordVisible(true)

        assertTrue(sut.state.value.isPasswordVisible)
    }

    @Test
    fun `on setPassword should change the password`() {
        assertFalse(sut.state.value.isPasswordInputError)
        assertEquals("", sut.state.value.password)

        sut.state.value.onPasswordValueChange("password")

        assertEquals("password", sut.state.value.password)
        assertFalse(sut.state.value.isPasswordInputError)
    }

    @Test
    fun `on setLogin should change the login`() {
        assertFalse(sut.state.value.isLoginInputError)
        assertEquals("", sut.state.value.login)

        sut.state.value.onLoginValueChange("login")

        assertEquals("login", sut.state.value.login)
        assertFalse(sut.state.value.isLoginInputError)
    }

    @Test
    fun `on setServer should change the server`() {
        val newServerValue = getRandomString()

        assertFalse(sut.state.value.isServerInputError)
        assertEquals(defaultServer, sut.state.value.server)

        sut.state.value.onServerValueChange(newServerValue)

        assertEquals(newServerValue, sut.state.value.server)
        assertFalse(sut.state.value.isServerInputError)
    }

    @Test
    fun `on onGitHubLogin with invalid server shows server error`() {
        sut.state.value.onServerValueChange("not-a-valid-server")

        sut.state.value.onGitHubLogin()

        assertTrue(sut.state.value.isServerInputError)
        assertEquals(0, authRepository.githubAuthCallCount)
    }

    @Test
    fun `on onGitHubLogin with empty client id shows error`() = runTest {
        val vmWithoutClientId = LoginViewModel(
            authRepository,
            serverStorage,
            FakeAppInfoProvider(githubClientId = ""),
            gitHubOAuthCallbackManager
        )

        vmWithoutClientId.state.value.onServerValueChange(correctServer)
        vmWithoutClientId.state.value.onGitHubLogin()

        assertFalse(vmWithoutClientId.state.value.isServerInputError)
        assertTrue(vmWithoutClientId.state.value.error.isNotEmpty())
        assertEquals(0, authRepository.githubAuthCallCount)
    }

    @Test
    fun `on onGitHubLogin with valid server emits auth url`() = runTest {
        sut.state.value.onServerValueChange(correctServer)

        sut.gitHubAuthUrl.test {
            sut.state.value.onGitHubLogin()

            val url = awaitItem()
            assertTrue(url.startsWith("https://github.com/login/oauth/authorize"))
            assertTrue(url.contains("client_id=test_github_client_id"))
        }
    }

    @Test
    fun `on github callback received login succeeds`() = runTest {
        sut.state.value.onServerValueChange(correctServer)
        // Initiate GitHub login to set the pending server
        sut.gitHubAuthUrl.test {
            sut.state.value.onGitHubLogin()
            awaitItem() // consume the auth URL
        }

        sut.loginSuccessful.test {
            gitHubOAuthCallbackManager.emitCode("test_code")

            assertTrue(awaitItem())
            assertEquals(correctServer, authRepository.githubAuthCalledWithServer)
            assertEquals("test_code", authRepository.githubAuthCalledWithCode)
        }
    }
}
