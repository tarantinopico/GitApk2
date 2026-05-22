package com.fluidgit.app.domain.model

sealed class GitResult<out T> {
    data class Success<T>(val data: T) : GitResult<T>()
    data class Error(val exception: Exception, val message: String? = null) : GitResult<Nothing>()
    data class Loading(val progress: Float) : GitResult<Nothing>()
}

data class BlameLine(
    val commitId: String,
    val authorName: String,
    val authorEmail: String,
    val date: Long,
    val lineContent: String
)

data class DiffEntryUi(
    val changeType: String,
    val oldPath: String,
    val newPath: String
)

data class CommitUi(
    val id: String,
    val message: String,
    val authorName: String,
    val authorEmail: String,
    val date: Long
)

data class BranchUi(
    val name: String,
    val isRemote: Boolean,
    val commitId: String
)

data class StashUi(
    val name: String,
    val message: String,
    val commitId: String
)

data class TagUi(
    val name: String,
    val message: String?,
    val commitId: String
)

data class SubmoduleUi(
    val path: String,
    val uri: String,
    val commitId: String
)

data class RemoteUi(
    val name: String,
    val fetchUri: String,
    val pushUri: String
)

sealed class GitCredentials
data class PasswordCredentials(val username: String, val password: String): GitCredentials()
data class TokenCredentials(val username: String?, val token: String): GitCredentials()
data class SshCredentials(val alias: String): GitCredentials()

enum class ConflictResolution {
    OURS,
    THEIRS,
    MARK_RESOLVED
}
