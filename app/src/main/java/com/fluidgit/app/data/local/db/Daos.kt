package com.fluidgit.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface RepoDao {
    @Query("SELECT * FROM repos ORDER BY lastUpdated DESC")
    fun getAllRepos(): Flow<List<RepoEntity>>

    @Query("SELECT * FROM repos WHERE id = :id")
    fun getRepoById(id: String): Flow<RepoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepo(repo: RepoEntity)

    @Query("DELETE FROM repos WHERE id = :id")
    suspend fun deleteRepoById(id: String)
}

@Dao
interface BranchDao {
    @Query("SELECT * FROM branches WHERE repoId = :repoId")
    fun getBranchesForRepo(repoId: String): Flow<List<BranchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranches(branches: List<BranchEntity>)

    @Query("DELETE FROM branches WHERE repoId = :repoId")
    suspend fun deleteBranchesForRepo(repoId: String)
}

@Dao
interface RemoteDao {
    @Query("SELECT * FROM remotes WHERE repoId = :repoId")
    fun getRemotesForRepo(repoId: String): Flow<List<RemoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemotes(remotes: List<RemoteEntity>)

    @Query("DELETE FROM remotes WHERE repoId = :repoId")
    suspend fun deleteRemotesForRepo(repoId: String)
}

@Dao
interface CommitDao {
    @Query("SELECT * FROM commits WHERE repoId = :repoId ORDER BY timestamp DESC")
    fun getCommitsForRepo(repoId: String): Flow<List<CommitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommits(commits: List<CommitEntity>)
    
    @Query("DELETE FROM commits WHERE repoId = :repoId")
    suspend fun deleteCommitsForRepo(repoId: String)
}

@Dao
interface TagDao {
    @Query("SELECT * FROM tags WHERE repoId = :repoId")
    fun getTagsForRepo(repoId: String): Flow<List<TagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<TagEntity>)
    
    @Query("DELETE FROM tags WHERE repoId = :repoId")
    suspend fun deleteTagsForRepo(repoId: String)
}

@Dao
interface SubmoduleDao {
    @Query("SELECT * FROM submodules WHERE repoId = :repoId")
    fun getSubmodulesForRepo(repoId: String): Flow<List<SubmoduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmodules(submodules: List<SubmoduleEntity>)

    @Query("DELETE FROM submodules WHERE repoId = :repoId")
    suspend fun deleteSubmodulesForRepo(repoId: String)
}

@Dao
interface StashDao {
    @Query("SELECT * FROM stashes WHERE repoId = :repoId")
    fun getStashesForRepo(repoId: String): Flow<List<StashEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStashes(stashes: List<StashEntity>)

    @Query("DELETE FROM stashes WHERE repoId = :repoId")
    suspend fun deleteStashesForRepo(repoId: String)
}
