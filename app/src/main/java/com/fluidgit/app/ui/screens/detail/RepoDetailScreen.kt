package com.fluidgit.app.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fluidgit.app.ui.theme.Slate100

@Composable
fun RepoDetailScreen(
    repoId: String,
    viewModel: RepoDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Repo Detail: ${uiState.repoId}",
                color = Slate100
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.isLoading) {
                Text(text = "Loading branches...", color = Slate100)
            } else {
                Text(text = "Branches: ${uiState.branches.size}", color = Slate100)
            }
        }
    }
}
