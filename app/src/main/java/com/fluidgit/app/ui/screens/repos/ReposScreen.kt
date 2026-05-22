package com.fluidgit.app.ui.screens.repos

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fluidgit.app.data.local.db.RepoEntity
import com.fluidgit.app.ui.components.FluidCard
import com.fluidgit.app.ui.components.ShimmerLoading
import com.fluidgit.app.ui.theme.Cyan400
import com.fluidgit.app.ui.theme.Cyan500
import com.fluidgit.app.ui.theme.Slate100
import com.fluidgit.app.ui.theme.Slate400
import com.fluidgit.app.ui.theme.Slate600

@Composable
fun ReposScreen(
    onNavigateToDetail: (String) -> Unit,
    viewModel: ReposViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp) // Leave space for top bar if any
    ) {
        if (uiState.isLoading) {
            LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(3) {
                    FluidCard {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            ShimmerLoading(height = 24.dp)
                            Spacer(modifier = Modifier.height(12.dp))
                            ShimmerLoading(height = 14.dp, modifier = Modifier.fillMaxWidth(0.5f))
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(uiState.repos, key = { it.id }) { repo ->
                    RepoItem(
                        repo = repo,
                        onClick = { onNavigateToDetail(repo.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun RepoItem(
    repo: RepoEntity,
    onClick: () -> Unit
) {
    FluidCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = repo.name,
                    color = Slate100,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = repo.localPath,
                    color = Slate400,
                    fontSize = 12.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .background(Cyan500.copy(alpha = 0.1f), CircleShape)
                        .border(1.dp, Cyan500.copy(alpha = 0.2f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Cyan400, CircleShape)
                    )
                    Text(
                        text = repo.currentBranch ?: "unknown",
                        color = Cyan400,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Status Indicator dots can go here
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Slate600, CircleShape)
            )
        }
    }
}
