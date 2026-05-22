package com.fluidgit.app.data.local.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "repos")
data class RepoEntity(
    @PrimaryKey val id: String, // Can be path or a unique ID
    val name: String,
    val localPath: String,
    val currentBranch: String?,
    val uncommittedChangesCount: Int = 0,
    val aheadCount: Int = 0,
    val behindCount: Int = 0,
    val lastUpdated: Date
)

@Entity(
    tableName = "branches",
    foreignKeys = [
        ForeignKey(
            entity = RepoEntity::class,
            parentColumns = ["id"],
            childColumns = ["repoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("repoId")]
)
data class BranchEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repoId: String,
    val name: String,
    val isRemote: Boolean,
    val commitId: String
)

@Entity(
    tableName = "remotes",
    foreignKeys = [
        ForeignKey(
            entity = RepoEntity::class,
            parentColumns = ["id"],
            childColumns = ["repoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("repoId")]
)
data class RemoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repoId: String,
    val name: String,
    val fetchUri: String,
    val pushUri: String
)

@Entity(
    tableName = "stashes",
    foreignKeys = [
        ForeignKey(
            entity = RepoEntity::class,
            parentColumns = ["id"],
            childColumns = ["repoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("repoId")]
)
data class StashEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repoId: String,
    val stashRef: String,
    val message: String,
    val commitId: String
)

@Entity(
    tableName = "commits",
    foreignKeys = [
        ForeignKey(
            entity = RepoEntity::class,
            parentColumns = ["id"],
            childColumns = ["repoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("repoId")]
)
data class CommitEntity(
    @PrimaryKey val commitId: String,
    val repoId: String,
    val message: String,
    val authorName: String,
    val authorEmail: String,
    val timestamp: Date
)

@Entity(
    tableName = "tags",
    foreignKeys = [
        ForeignKey(
            entity = RepoEntity::class,
            parentColumns = ["id"],
            childColumns = ["repoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("repoId")]
)
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repoId: String,
    val name: String,
    val message: String?,
    val commitId: String
)

@Entity(
    tableName = "submodules",
    foreignKeys = [
        ForeignKey(
            entity = RepoEntity::class,
            parentColumns = ["id"],
            childColumns = ["repoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("repoId")]
)
data class SubmoduleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repoId: String,
    val path: String,
    val uri: String,
    val commitId: String
)
