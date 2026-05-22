package com.fluidgit.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.fluidgit.app.data.local.db.AppDatabase
import com.fluidgit.app.domain.repository.RepoRepository
import com.fluidgit.app.domain.repository.SettingsRepository
import com.fluidgit.app.domain.usecase.GitUseCases
import com.fluidgit.app.data.local.prefs.SshKeyManager
import com.fluidgit.app.data.git.CustomSshSessionFactory
import com.fluidgit.app.data.git.GitManager
import com.fluidgit.app.data.repository.RepoRepositoryImpl
import com.fluidgit.app.di.dataStore

class AppContainer(private val applicationContext: Context) {
    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(applicationContext.dataStore)
    }

    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "fluidgit_database"
        ).build()
    }

    val sshKeyManager by lazy {
        SshKeyManager(applicationContext)
    }
    
    val customSshSessionFactory by lazy {
        CustomSshSessionFactory(sshKeyManager)
    }
    
    val gitManager by lazy {
        GitManager(customSshSessionFactory)
    }

    val repoRepository: RepoRepository by lazy {
        RepoRepositoryImpl(database.repoDao(), database.branchDao(), database.commitDao(), gitManager)
    }

    val gitUseCases: GitUseCases by lazy {
        GitUseCases(repoRepository)
    }
}
