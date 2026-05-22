package com.fluidgit.app.ui.screens.repos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fluidgit.app.data.local.db.RepoEntity
import com.fluidgit.app.domain.usecase.GitUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReposUiState(
    val repos: List<RepoEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ReposViewModel @Inject constructor(
    private val gitUseCases: GitUseCases
) : ViewModel() {

    val uiState: StateFlow<ReposUiState> = gitUseCases.getAllRepos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
        .let { reposFlow ->
            val mutableState = MutableStateFlow(ReposUiState(isLoading = true))
            viewModelScope.launch {
                reposFlow.collect { repos ->
                    mutableState.value = mutableState.value.copy(repos = repos, isLoading = false)
                }
            }
            mutableState.asStateFlow()
        }

    fun removeRepo(id: String) {
        viewModelScope.launch {
            gitUseCases.removeRepository(id)
        }
    }
    
    fun refreshRepo(id: String) {
        viewModelScope.launch {
            gitUseCases.refreshRepo(id)
        }
    }
}
