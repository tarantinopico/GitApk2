package com.fluidgit.app.data.repository

import com.fluidgit.app.data.git.GitManager
import com.fluidgit.app.data.local.db.BranchDao
import com.fluidgit.app.data.local.db.BranchEntity
import com.fluidgit.app.data.local.db.RepoDao
import com.fluidgit.app.data.local.db.RepoEntity
import com.fluidgit.app.domain.model.BranchUi
import com.fluidgit.app.domain.model.CommitUi
import com.fluidgit.app.domain.model.GitResult
import com.fluidgit.app.domain.repository.RepoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import org.eclipse.jgit.lib.PersonIdent
import java.io.File
import java.util.Date
import java.util.UUID
import javax.inject.Inject

import com.fluidgit.app.data.local.db.CommitDao
import com.fluidgit.app.data.local.db.CommitEntity

class RepoRepositoryImpl @Inject constructor(
    private val repoDao: RepoDao,
    private val branchDao: BranchDao,
    private val commitDao: CommitDao,
    private val gitManager: GitManager,
    private val settingsRepository: com.fluidgit.app.domain.repository.SettingsRepository
) : RepoRepository {

    private suspend fun getCredentialsProvider(): org.eclipse.jgit.transport.CredentialsProvider? {
        val token = settingsRepository.githubToken.firstOrNull() ?: return null
        return org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider(token, "")
    }

    override fun getAllRepos(): Flow<List<RepoEntity>> = repoDao.getAllRepos()
    
    override fun getAllRecentCommits(): Flow<List<CommitEntity>> = commitDao.getAllCommits()

    override fun getRepoById(id: String): Flow<RepoEntity?> = repoDao.getRepoById(id)

    override suspend fun cloneRepo(url: String, localPath: String): GitResult<Unit> {
        val file = File(localPath)
        val cloneResult = gitManager.clone(
            uri = url,
            directory = file,
            credentials = getCredentialsProvider(),
            branch = null,
            depth = null,
            progress = null
        )

        if (cloneResult is GitResult.Success) {
            val repoId = UUID.randomUUID().toString()
            repoDao.insertRepo(
                RepoEntity(
                    id = repoId,
                    name = file.name,
                    localPath = localPath,
                    currentBranch = "main", // Will be updated by status refresh
                    uncommittedChangesCount = 0,
                    aheadCount = 0,
                    behindCount = 0,
                    lastUpdated = Date()
                )
            )
            refreshRepoStatus(repoId)
        }
        return cloneResult
    }

    override suspend fun addExistingRepo(localPath: String): GitResult<Unit> {
        val file = File(localPath)
        val openResult = gitManager.open(file)
        if (openResult is GitResult.Success) {
            val git = openResult.data
            val repoId = UUID.randomUUID().toString()
            repoDao.insertRepo(
                RepoEntity(
                    id = repoId,
                    name = file.name,
                    localPath = localPath,
                    currentBranch = git.repository.branch,
                    uncommittedChangesCount = 0,
                    lastUpdated = Date()
                )
            )
            return GitResult.Success(Unit)
        } else {
            return GitResult.Error(Exception("Not a valid Git repository"))
        }
    }

    override suspend fun refreshRepoStatus(id: String): GitResult<Unit> {
        val repo = repoDao.getRepoByIdSync(id) ?: return GitResult.Error(Exception("Repo not found"))
        val file = File(repo.localPath)
        if (!file.exists()) return GitResult.Error(Exception("Path not found"))
        val gitResult = gitManager.open(file)
        if (gitResult is GitResult.Success) {
            val git = gitResult.data
            val statusResult = gitManager.status(git)
            val trackingStatusResult = gitManager.getTrackingStatus(git)
            if (statusResult is GitResult.Success) {
                val status = statusResult.data
                val uncommittedCount = status.uncommittedChanges.size
                var ahead = 0
                var behind = 0
                if (trackingStatusResult is GitResult.Success && trackingStatusResult.data != null) {
                    ahead = trackingStatusResult.data.aheadCount
                    behind = trackingStatusResult.data.behindCount
                }
                repoDao.updateRepo(
                    repo.copy(
                        uncommittedChangesCount = uncommittedCount,
                        aheadCount = ahead,
                        behindCount = behind,
                        lastUpdated = Date()
                    )
                )
                return GitResult.Success(Unit)
            }
        }
        return GitResult.Error(Exception("Failed to get status"))
    }

    override suspend fun deleteRepo(id: String): GitResult<Unit> {
        repoDao.deleteRepoById(id)
        return GitResult.Success(Unit)
    }

    override suspend fun getBranches(repoId: String): Flow<List<BranchUi>> {
        return branchDao.getBranchesForRepo(repoId).map { entities ->
            entities.map { BranchUi(it.name, it.isRemote, it.commitId) }
        }
    }

    override suspend fun getCommits(repoId: String): Flow<List<CommitUi>> {
        // Simulated
        return kotlinx.coroutines.flow.flowOf(emptyList())
    }

    override suspend fun commit(repoId: String, message: String): GitResult<Unit> {
        val repo = repoDao.getRepoByIdSync(repoId) ?: return GitResult.Error(Exception("Repo not found"))
        val file = File(repo.localPath)
        val openResult = gitManager.open(file)
        if (openResult is GitResult.Success) {
            val git = openResult.data
            // Simplified author handling
            val author = PersonIdent("Fluid Git User", "user@fluidgit.app")
            
            // Add all files
            git.add().addFilepattern(".").call()
            
            // Commit
            val commitResult = gitManager.commit(git, message, author, false)
            if (commitResult is GitResult.Success) {
                refreshRepoStatus(repoId)
                return GitResult.Success(Unit)
            } else if (commitResult is GitResult.Error) {
                return GitResult.Error(commitResult.exception)
            }
        }
        return GitResult.Error(Exception("Failed to commit"))
    }
}
