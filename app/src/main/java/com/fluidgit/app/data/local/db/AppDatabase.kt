package com.fluidgit.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        RepoEntity::class,
        BranchEntity::class,
        RemoteEntity::class,
        StashEntity::class,
        CommitEntity::class,
        TagEntity::class,
        SubmoduleEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repoDao(): RepoDao
    abstract fun branchDao(): BranchDao
    abstract fun remoteDao(): RemoteDao
    abstract fun stashDao(): StashDao
    abstract fun commitDao(): CommitDao
    abstract fun tagDao(): TagDao
    abstract fun submoduleDao(): SubmoduleDao
}
