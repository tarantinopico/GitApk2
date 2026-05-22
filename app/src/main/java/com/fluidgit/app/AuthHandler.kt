package com.fluidgit.app

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.fluidgit.app.data.api.GithubAuthApi
import com.fluidgit.app.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import android.util.Log

class AuthHandler(
    private val settingsRepository: SettingsRepository,
    private val scope: CoroutineScope
) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://github.com/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        
    private val api = retrofit.create(GithubAuthApi::class.java)
    
    fun handleAuthIntent(uri: Uri?) {
        if (uri != null && uri.scheme == "fluidgit" && uri.host == "callback") {
            val code = uri.getQueryParameter("code")
            if (code != null) {
                // Exchange code for token
                exchangeCode(code)
            }
        }
    }
    
    private fun exchangeCode(code: String) {
        scope.launch {
            try {
                // Warning: Without a real client_secret this will fail against real GitHub OAuth Apps.
                // However, users can provide their own via BuildConfig or Secrets.
                val response = withContext(Dispatchers.IO) {
                    api.getAccessToken(
                        clientId = "Iv23ligl85a5Fv", // Dummy
                        clientSecret = null,
                        code = code
                    )
                }
                
                settingsRepository.setGithubToken(response.access_token)
            } catch (e: Exception) {
                Log.e("AuthHandler", "Failed to exchange token", e)
            }
        }
    }
}
