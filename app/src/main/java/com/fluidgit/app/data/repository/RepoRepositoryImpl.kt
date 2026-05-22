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
import org.eclipse.jgit.lib.PersonIdent
import java.io.File
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class RepoRepositoryImpl @Inject constructor(
    private val repoDao: RepoDao,
    private val branchDao: BranchDao,
    private val gitManager: GitManager
) : RepoRepository {

    override fun getAllRepos(): Flow<List<RepoEntity>> = repoDao.getAllRepos()

    override fun getRepoById(id: String): Flow<RepoEntity?> = repoDao.getRepoById(id)

    override suspend fun cloneRepo(url: String, localPath: String): GitResult<Unit> {
        val file = File(localPath)
        val cloneResult = gitManager.clone(
            uri = url,
            directory = file,
            credentials = null, // In reality, we'd fetch these
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
            openResult.data.use {
                val repoId = UUID.randomUUID().toString()
                repoDao.insertRepo(
                    RepoEntity(
                        id = repoId,
                        name = file.name,
                        localPath = localPath,
                        currentBranch = it.repository.branch,
                        uncommittedChangesCount = 0,
                        lastUpdated = Date()
                    )
                )
            }
            return GitResult.Success(Unit)
        } else {
            return GitResult.Error(Exception("Not a valid Git repository"))
        }
    }

    override suspend fun refreshRepoStatus(id: String): GitResult<Unit> {
        // Find repo, open git, get status, update DB
        return GitResult.Success(Unit) // Simplified
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
        // Simplified
        return GitResult.Success(Unit)
    }
}
