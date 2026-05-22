package com.fluidgit.app.domain.usecase

import com.fluidgit.app.data.local.db.RepoEntity
import com.fluidgit.app.domain.model.BranchUi
import com.fluidgit.app.domain.model.GitResult
import com.fluidgit.app.domain.repository.RepoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GitUseCases @Inject constructor(
    private val repoRepository: RepoRepository
) {
    fun getAllRepos(): Flow<List<RepoEntity>> = repoRepository.getAllRepos()
    
    suspend fun cloneRepository(url: String, localPath: String): GitResult<Unit> {
        return repoRepository.cloneRepo(url, localPath)
    }

    suspend fun addExistingRepository(localPath: String): GitResult<Unit> {
        return repoRepository.addExistingRepo(localPath)
    }

    suspend fun removeRepository(id: String): GitResult<Unit> {
        return repoRepository.deleteRepo(id)
    }

    suspend fun getBranches(repoId: String): Flow<List<BranchUi>> {
        return repoRepository.getBranches(repoId)
    }

    suspend fun refreshRepo(repoId: String): GitResult<Unit> {
        return repoRepository.refreshRepoStatus(repoId)
    }
}
