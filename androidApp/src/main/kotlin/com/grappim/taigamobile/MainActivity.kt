package com.grappim.taigamobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.grappim.taigamobile.feature.login.domain.github.GitHubOAuthCallbackManager
import com.grappim.taigamobile.main.TaigaAppContent
import com.grappim.taigamobile.uikit.utils.ScreenReadySignalController
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val screenReadySignalController =
        ScreenReadySignalController()

    private val gitHubOAuthCallbackManager: GitHubOAuthCallbackManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().apply {
            setKeepOnScreenCondition {
                screenReadySignalController.isReady.value.not()
            }
        }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        FileKit.init(this)

        handleIntent(intent)

        setContent {
            TaigaAppContent(screenReadySignalController)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            val code = intent.data?.getQueryParameter("code")
            if (code != null) {
                gitHubOAuthCallbackManager.onCallbackReceived(code)
            }
        }
    }
}
