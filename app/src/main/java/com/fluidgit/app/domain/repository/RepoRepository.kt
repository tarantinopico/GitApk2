package com.fluidgit.app.domain.repository

import com.fluidgit.app.data.local.db.RepoEntity
import com.fluidgit.app.domain.model.BranchUi
import com.fluidgit.app.domain.model.CommitUi
import com.fluidgit.app.domain.model.GitResult
import com.fluidgit.app.data.local.db.CommitEntity
import kotlinx.coroutines.flow.Flow

interface RepoRepository {
    fun getAllRepos(): Flow<List<RepoEntity>>
    fun getAllRecentCommits(): Flow<List<CommitEntity>>
    fun getRepoById(id: String): Flow<RepoEntity?>
    suspend fun cloneRepo(url: String, localPath: String): GitResult<Unit>
    suspend fun addExistingRepo(localPath: String): GitResult<Unit>
    suspend fun refreshRepoStatus(id: String): GitResult<Unit>
    suspend fun deleteRepo(id: String): GitResult<Unit>
    suspend fun getBranches(repoId: String): Flow<List<BranchUi>>
    suspend fun getCommits(repoId: String): Flow<List<CommitUi>>
    suspend fun commit(repoId: String, message: String): GitResult<Unit>
}
