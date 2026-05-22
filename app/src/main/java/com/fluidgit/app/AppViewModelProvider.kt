package com.fluidgit.app

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.createSavedStateHandle
import com.fluidgit.app.ui.screens.detail.RepoDetailViewModel
import com.fluidgit.app.ui.screens.repos.ReposViewModel
import com.fluidgit.app.ui.screens.settings.SettingsViewModel
import com.fluidgit.app.ui.screens.activity.ActivityViewModel

fun CreationExtras.fluidGitApplication(): FluidGitApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FluidGitApplication)

val AppViewModelProvider = viewModelFactory {
    initializer {
        GlobalViewModel(fluidGitApplication().container.settingsRepository)
    }
    initializer {
        ReposViewModel(fluidGitApplication().container.gitUseCases)
    }
    initializer {
        SettingsViewModel(fluidGitApplication().container.settingsRepository)
    }
    initializer {
        ActivityViewModel(fluidGitApplication().container.gitUseCases)
    }
    initializer {
        RepoDetailViewModel(this.createSavedStateHandle(), fluidGitApplication().container.gitUseCases)
    }
}
