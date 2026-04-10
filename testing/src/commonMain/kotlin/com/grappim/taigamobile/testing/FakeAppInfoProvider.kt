package com.grappim.taigamobile.testing

import com.grappim.taigamobile.core.appinfoapi.AppInfoProvider

class FakeAppInfoProvider(
    private val githubClientId: String = ""
) : AppInfoProvider {
    override fun getAppInfo(): String = "test"
    override fun isDebug(): Boolean = true
    override fun isFdroidBuild(): Boolean = false
    override fun getVersionName(): String = "1.0.0"
    override fun getDebugLocalHost(): String = ""
    override fun getBuildType(): String = "debug"
    override fun getGitHubClientId(): String = githubClientId
}
