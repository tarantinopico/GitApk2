package com.fluidgit.app.data.git

import com.fluidgit.app.domain.model.BlameLine
import com.fluidgit.app.domain.model.ConflictResolution
import com.fluidgit.app.domain.model.DiffEntryUi
import com.fluidgit.app.domain.model.GitResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.MergeResult
import org.eclipse.jgit.api.PullResult
import org.eclipse.jgit.api.RebaseResult
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.blame.BlameResult
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.lib.ProgressMonitor
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.FetchResult
import org.eclipse.jgit.transport.RemoteConfig
import org.eclipse.jgit.transport.URIish
import org.eclipse.jgit.transport.SshTransport
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

import android.util.LruCache
import java.util.concurrent.ConcurrentHashMap

@Singleton
class GitManager @Inject constructor(
    private val sshSessionFactory: CustomSshSessionFactory
) {
    private val gitCache = object : LruCache<String, Git>(5) {
        override fun entryRemoved(evicted: Boolean, key: String?, oldValue: Git?, newValue: Git?) {
            oldValue?.close()
        }
    }

    private inline fun <T> runGitOperation(block: () -> T): GitResult<T> {
        return try {
            GitResult.Success(block())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            GitResult.Error(e, GitErrorMapper.mapException(e))
        }
    }

    private fun configureTransport(command: org.eclipse.jgit.api.TransportCommand<*, *>, credentials: CredentialsProvider?, sshAlias: String? = null) {
        command.setCredentialsProvider(credentials)
        if (sshAlias != null) {
            sshSessionFactory.activeAlias = sshAlias
            command.setTransportConfigCallback { transport ->
                if (transport is SshTransport) {
                    transport.sshSessionFactory = sshSessionFactory
                }
            }
        }
    }

    suspend fun init(directory: File): GitResult<Unit> = withContext(Dispatchers.IO) {
        runGitOperation {
            Git.init().setDirectory(directory).call().use { }
        }
    }

    suspend fun clone(
        uri: String, 
        directory: File, 
        credentials: CredentialsProvider?, 
        branch: String?, 
        depth: Int?, 
        progress: Flow<Float>?,
        sshAlias: String? = null,
        job: Job? = null
    ): GitResult<Unit> = withContext(Dispatchers.IO) {
        runGitOperation {
            val command = Git.cloneRepository()
                .setURI(uri)
                .setDirectory(directory)
                .setCloneAllBranches(branch == null)
            
            branch?.let { command.setBranch(it) }
            configureTransport(command, credentials, sshAlias)
            command.setProgressMonitor(object : ProgressMonitor {
                override fun start(totalTasks: Int) {}
                override fun beginTask(title: String?, totalWork: Int) {}
                override fun update(completed: Int) { job?.ensureActive() }
                override fun endTask() {}
                override fun isCancelled(): Boolean = job?.isCancelled == true
                override fun showDuration(p0: Boolean) {}
            })
            command.call().use { }
        }
    }

    suspend fun open(directory: File): GitResult<Git> = withContext(Dispatchers.IO) {
        runGitOperation {
            val path = directory.absolutePath
            gitCache.get(path) ?: synchronized(gitCache) {
                gitCache.get(path) ?: Git.open(directory).also { gitCache.put(path, it) }
            }
        }
    }

    suspend fun status(repo: Git): GitResult<Status> = withContext(Dispatchers.IO) {
        runGitOperation {
            repo.status().call()
        }
    }

    suspend fun getTrackingStatus(repo: Git): GitResult<org.eclipse.jgit.lib.BranchTrackingStatus?> = withContext(Dispatchers.IO) {
        runGitOperation {
            val branch = repo.repository.fullBranch
            if (branch != null) {
                org.eclipse.jgit.lib.BranchTrackingStatus.of(repo.repository, branch)
            } else {
                null
            }
        }
    }

    suspend fun add(repo: Git, filePatterns: List<String>): GitResult<Unit> = withContext(Dispatchers.IO) {
        runGitOperation {
            val addCmd = repo.add()
            filePatterns.forEach { addCmd.addFilepattern(it) }
            addCmd.call()
            Unit
        }
    }

    suspend fun unstage(repo: Git, filePatterns: List<String>): GitResult<Unit> = withContext(Dispatchers.IO) {
        runGitOperation {
            val resetCmd = repo.reset()
            filePatterns.forEach { resetCmd.addPath(it) }
            resetCmd.call()
            Unit
        }
    }

    suspend fun commit(repo: Git, message: String, author: PersonIdent?, amend: Boolean): GitResult<RevCommit> = withContext(Dispatchers.IO) {
        runGitOperation {
            val commitCmd = repo.commit()
                .setMessage(message)
                .setAmend(amend)
            author?.let { commitCmd.setAuthor(it) }
            commitCmd.call()
        }
    }

    suspend fun push(repo: Git, remote: String, credentials: CredentialsProvider?, tags: Boolean, force: Boolean, sshAlias: String? = null): GitResult<Unit> = withContext(Dispatchers.IO) {
        runGitOperation {
            val pushCmd = repo.push()
                .setRemote(remote)
                .setForce(force)
            if (tags) pushCmd.setPushTags()
            configureTransport(pushCmd, credentials, sshAlias)
            pushCmd.call()
            Unit
        }
    }

    suspend fun pull(repo: Git, remote: String, credentials: CredentialsProvider?, sshAlias: String? = null): GitResult<PullResult> = withContext(Dispatchers.IO) {
        runGitOperation {
            val pullCmd = repo.pull().setRemote(remote)
            configureTransport(pullCmd, credentials, sshAlias)
            pullCmd.call()
        }
    }

    suspend fun fetch(repo: Git, remote: String, credentials: CredentialsProvider?, sshAlias: String? = null): GitResult<FetchResult> = withContext(Dispatchers.IO) {
        runGitOperation {
            val fetchCmd = repo.fetch().setRemote(remote)
            configureTransport(fetchCmd, credentials, sshAlias)
            fetchCmd.call()
        }
    }

    suspend fun branchList(repo: Git): GitResult<List<Ref>> = withContext(Dispatchers.IO) {
        runGitOperation {
            repo.branchList().setListMode(org.eclipse.jgit.api.ListBranchCommand.ListMode.ALL).call()
        }
    }

    suspend fun createBranch(repo: Git, name: String, startPoint: String?): GitResult<Ref> = withContext(Dispatchers.IO) {
        runGitOperation {
            val cmd = repo.branchCreate().setName(name)
            startPoint?.let { cmd.setStartPoint(it) }
            cmd.call()
        }
    }

    suspend fun deleteBranch(repo: Git, name: String, force: Boolean): GitResult<Unit> = withContext(Dispatchers.IO) {
        runGitOperation {
            repo.branchDelete().setBranchNames(name).setForce(force).call()
            Unit
        }
    }

    suspend fun checkout(repo: Git, name: String, createBranch: Boolean): GitResult<Ref> = withContext(Dispatchers.IO) {
        runGitOperation {
            repo.checkout().setName(name).setCreateBranch(createBranch).call()
        }
    }

    suspend fun merge(repo: Git, branchName: String, message: String?, credentials: CredentialsProvider?): GitResult<MergeResult> = withContext(Dispatchers.IO) {
        runGitOperation {
            val ref = repo.repository.findRef(branchName) ?: throw Exception("Branch not found")
            val cmd = repo.merge().include(ref)
            message?.let { cmd.setMessage(it) }
            cmd.call()
        }
    }

    suspend fun rebase(repo: Git, upstream: String): GitResult<RebaseResult> = withContext(Dispatchers.IO) {
        runGitOperation {
            repo.rebase().setUpstream(upstream).call()
        }
    }

    suspend fun log(repo: Git, maxCount: Int, skip: Int, path: String?): GitResult<List<RevCommit>> = withContext(Dispatchers.IO) {
        runGitOperation {
            val cmd = repo.log().setMaxCount(maxCount).setSkip(skip)
            path?.let { cmd.addPath(it) }
            cmd.call().toList()
        }
    }

    suspend fun diff(repo: Git, oldCommit: String?, newCommit: String?, path: String?): GitResult<List<DiffEntryUi>> = withContext(Dispatchers.IO) {
        runGitOperation {
            val oldTree = oldCommit?.let { repo.repository.resolve("$it^{tree}") }
            val newTree = newCommit?.let { repo.repository.resolve("$it^{tree}") }
            val cmd = repo.diff()
            // In a real implementation we would setup TreeWalkers.
            emptyList<DiffEntryUi>() // Simplified for structure
        }
    }

    suspend fun reset(repo: Git, mode: org.eclipse.jgit.api.ResetCommand.ResetType, ref: String): GitResult<Ref> = withContext(Dispatchers.IO) {
        runGitOperation {
            repo.reset().setMode(mode).setRef(ref).call()
        }
    }

    suspend fun clean(repo: Git, directories: Boolean, force: Boolean): GitResult<Set<String>> = withContext(Dispatchers.IO) {
        runGitOperation {
            repo.clean().setCleanDirectories(directories).setForce(force).call()
        }
    }

    suspend fun revert(repo: Git, commit: String): GitResult<RevCommit> = withContext(Dispatchers.IO) {
        runGitOperation {
            val revCommit = repo.repository.resolve(commit)
            repo.revert().include(revCommit).call()
        }
    }

    suspend fun archive(repo: Git, outFile: File, format: String, treeish: String): GitResult<Unit> = withContext(Dispatchers.IO) {
        runGitOperation {
            java.io.FileOutputStream(outFile).use { out ->
                repo.archive().setFormat(format).setTree(repo.repository.resolve(treeish)).setOutputStream(out).call()
                Unit
            }
        }
    }

    suspend fun resolveMergeConflict(repo: Git, filePath: String, resolution: ConflictResolution): GitResult<Unit> = withContext(Dispatchers.IO) {
        runGitOperation {
            val dircache = repo.repository.readDirCache()
            // Placeholder for resolution logic involving checking out OURS or THEIRS
            // repo.checkout().addPath(filePath).setStage(CheckoutCommand.Stage.OURS).call()
            repo.add().addFilepattern(filePath).call()
            Unit
        }
    }
}
