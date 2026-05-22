package com.fluidgit.app.ui.screens.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fluidgit.app.data.local.db.CommitEntity
import com.fluidgit.app.domain.usecase.GitUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActivityUiState(
    val commits: List<CommitEntity> = emptyList(),
    val isLoading: Boolean = false
)

class ActivityViewModel(
    private val gitUseCases: GitUseCases
) : ViewModel() {
    val uiState: StateFlow<ActivityUiState> = gitUseCases.getAllRecentCommits()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        .let { flow ->
            val mutableState = MutableStateFlow(ActivityUiState(isLoading = true))
            viewModelScope.launch {
                flow.collect { commits ->
                    mutableState.value = mutableState.value.copy(commits = commits, isLoading = false)
                }
            }
            mutableState.asStateFlow()
        }
}
