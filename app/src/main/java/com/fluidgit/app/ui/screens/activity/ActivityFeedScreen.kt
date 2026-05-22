package com.fluidgit.app.ui.screens.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fluidgit.app.AppViewModelProvider
import com.fluidgit.app.data.local.db.CommitEntity
import com.fluidgit.app.ui.components.FluidCard
import com.fluidgit.app.ui.theme.Slate100
import com.fluidgit.app.ui.theme.Slate400
import com.fluidgit.app.ui.theme.CyanNeon
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.runtime.setValue
import androidx.compose.foundation.clickable

@Composable
fun ActivityFeedScreen(
    viewModel: ActivityViewModel = viewModel(factory = AppViewModelProvider)
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading && uiState.commits.isEmpty()) {
            Text(
                "Loading Activity...",
                color = Slate400,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (uiState.commits.isEmpty()) {
            Text(
                "No recent commits found across repos.",
                color = Slate400,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.commits) { commit ->
                    CommitFeedItem(commit)
                }
            }
        }
    }
}

@Composable
fun CommitFeedItem(commit: CommitEntity) {
    var expanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    FluidCard(
        modifier = Modifier.fillMaxWidth().clickable { expanded = true }
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                val df = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                Text(
                    text = "${commit.authorName} on ${df.format(commit.timestamp)}",
                    color = CyanNeon,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = commit.message,
                    color = Slate100,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)
                )
                Text(
                    text = commit.commitId.take(7),
                    color = Slate400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            androidx.compose.material3.DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                androidx.compose.material3.DropdownMenuItem(
                    text = { Text("Cherry-Pick") },
                    onClick = {
                        expanded = false
                        // TODO: Implement Cherry Pick
                    }
                )
            }
        }
    }
}
