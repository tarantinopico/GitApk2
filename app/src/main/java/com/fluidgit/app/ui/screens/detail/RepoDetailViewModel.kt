package com.fluidgit.app.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fluidgit.app.domain.model.BranchUi
import com.fluidgit.app.domain.usecase.GitUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RepoDetailUiState(
    val repoId: String = "",
    val isLoading: Boolean = true,
    val branches: List<BranchUi> = emptyList(),
    val error: String? = null
)

class RepoDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val gitUseCases: GitUseCases
) : ViewModel() {

    private val repoId: String = checkNotNull(savedStateHandle["repoId"])
    private val _uiState = MutableStateFlow(RepoDetailUiState(repoId = repoId))
    val uiState: StateFlow<RepoDetailUiState> = _uiState.asStateFlow()

    init {
        loadBranches()
    }

    private fun loadBranches() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            gitUseCases.getBranches(repoId).collect { branches ->
                _uiState.value = _uiState.value.copy(branches = branches, isLoading = false)
            }
        }
    }

    fun commit(message: String) {
        viewModelScope.launch {
            gitUseCases.commit(repoId, message)
            gitUseCases.refreshRepo(repoId)
            loadBranches()
        }
    }
}
