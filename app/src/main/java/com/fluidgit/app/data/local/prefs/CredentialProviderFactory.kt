package com.fluidgit.app.data.local.prefs

import com.fluidgit.app.domain.model.PasswordCredentials
import com.fluidgit.app.domain.model.TokenCredentials
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CredentialProviderFactory @Inject constructor() {
    
    fun createPasswordProvider(credentials: PasswordCredentials): CredentialsProvider {
        return UsernamePasswordCredentialsProvider(credentials.username, credentials.password)
    }

    fun createTokenProvider(credentials: TokenCredentials): CredentialsProvider {
        // Many Git hosts allow using the token as the password, sometimes with a dummy username
        return UsernamePasswordCredentialsProvider(credentials.username ?: "token", credentials.token)
    }
}
