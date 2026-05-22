package com.fluidgit.app.di

import android.content.Context
import androidx.room.Room
import com.fluidgit.app.data.local.db.AppDatabase
import com.fluidgit.app.data.local.db.BranchDao
import com.fluidgit.app.data.local.db.CommitDao
import com.fluidgit.app.data.local.db.RemoteDao
import com.fluidgit.app.data.local.db.RepoDao
import com.fluidgit.app.data.local.db.StashDao
import com.fluidgit.app.data.local.db.SubmoduleDao
import com.fluidgit.app.data.local.db.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "fluidgit_database"
        ).build()
    }

    @Provides
    fun provideRepoDao(database: AppDatabase): RepoDao = database.repoDao()

    @Provides
    fun provideBranchDao(database: AppDatabase): BranchDao = database.branchDao()

    @Provides
    fun provideCommitDao(database: AppDatabase): CommitDao = database.commitDao()

    @Provides
    fun provideTagDao(database: AppDatabase): TagDao = database.tagDao()

    @Provides
    fun provideRemoteDao(database: AppDatabase): RemoteDao = database.remoteDao()

    @Provides
    fun provideStashDao(database: AppDatabase): StashDao = database.stashDao()

    @Provides
    fun provideSubmoduleDao(database: AppDatabase): SubmoduleDao = database.submoduleDao()
}
