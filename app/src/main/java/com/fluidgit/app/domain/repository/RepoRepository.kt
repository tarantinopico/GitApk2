package com.fluidgit.app.domain.repository

import com.fluidgit.app.data.local.db.RepoEntity
import com.fluidgit.app.domain.model.BranchUi
import com.fluidgit.app.domain.model.CommitUi
import com.fluidgit.app.domain.model.GitResult
import kotlinx.coroutines.flow.Flow
import java.io.File

interface RepoRepository {
    fun getAllRepos(): Flow<List<RepoEntity>>
    fun getRepoById(id: String): Flow<RepoEntity?>
    suspend fun cloneRepo(url: String, localPath: String): GitResult<Unit>
    suspend fun addExistingRepo(localPath: String): GitResult<Unit>
    suspend fun refreshRepoStatus(id: String): GitResult<Unit>
    suspend fun deleteRepo(id: String): GitResult<Unit>
    suspend fun getBranches(repoId: String): Flow<List<BranchUi>>
    suspend fun getCommits(repoId: String): Flow<List<CommitUi>>
    suspend fun commit(repoId: String, message: String): GitResult<Unit>
}
