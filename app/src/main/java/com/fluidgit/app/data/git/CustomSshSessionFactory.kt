package com.fluidgit.app.data.git

import com.fluidgit.app.data.local.prefs.SshKeyManager
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.eclipse.jgit.transport.SshSessionFactory
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig
import org.eclipse.jgit.util.FS
import javax.inject.Inject

class CustomSshSessionFactory @Inject constructor(
    private val sshKeyManager: SshKeyManager
) : JschConfigSessionFactory() {

    var activeAlias: String? = null

    override fun configure(hc: OpenSshConfig.Host?, session: Session?) {
        session?.setConfig("StrictHostKeyChecking", "no")
    }

    override fun createDefaultJSch(fs: FS?): JSch {
        val jsch = super.createDefaultJSch(fs)
        activeAlias?.let { alias ->
            sshKeyManager.getPrivateKey(alias)?.let { privateKey ->
                jsch.addIdentity(alias, privateKey.toByteArray(), null, null)
            }
        }
        return jsch
    }
}
