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
import androidx.compose.runtime.setValue
import androidx.compose.foundation.border
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fluidgit.app.ui.theme.Slate100
import com.fluidgit.app.AppViewModelProvider

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.graphics.Color
import com.fluidgit.app.domain.model.CommitUi
import com.fluidgit.app.ui.components.FluidCard
import com.fluidgit.app.ui.theme.Cyan400
import com.fluidgit.app.ui.theme.Cyan500
import com.fluidgit.app.ui.theme.Slate400
import com.fluidgit.app.ui.theme.Slate600
import com.fluidgit.app.ui.theme.SurfaceElevated
import com.fluidgit.app.ui.theme.Background

@Composable
fun InteractiveRebaseDialog(
    commits: List<CommitUi>,
    onDismiss: () -> Unit,
    onSubmit: (List<Pair<CommitUi, String>>) -> Unit
) {
    var rebaseList by androidx.compose.runtime.remember { 
        androidx.compose.runtime.mutableStateOf(commits.map { it to "pick" }) 
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceElevated, androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .border(1.dp, Slate600, androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("Interactive Rebase", color = Slate100, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.height(300.dp)) {
                    items(rebaseList.size) { index ->
                        val (commit, action) = rebaseList[index]
                        FluidCard(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(commit.message.take(30), color = Slate100)
                                    Text(commit.id.take(7), color = Slate400, fontSize = 12.sp)
                                }
                                var expanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                                Box {
                                    Text(
                                        text = action,
                                        color = Cyan400,
                                        modifier = Modifier.clickable { expanded = true }.padding(8.dp)
                                    )
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier.background(Background)
                                    ) {
                                        listOf("pick", "squash", "drop").forEach { actionOption ->
                                            DropdownMenuItem(
                                                text = { Text(actionOption, color = Slate100) },
                                                onClick = {
                                                    val newList = rebaseList.toMutableList()
                                                    newList[index] = commit to actionOption
                                                    rebaseList = newList
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = Slate400)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onSubmit(rebaseList) },
                        colors = ButtonDefaults.buttonColors(containerColor = Cyan500, contentColor = Slate100)
                    ) {
                        Text("Rebase")
                    }
                }
            }
        }
    }
}

@Composable
fun RepoDetailScreen(
    repoId: String,
    viewModel: RepoDetailViewModel = viewModel(factory = AppViewModelProvider)
) {
    val uiState by viewModel.uiState.collectAsState()
    var showRebaseDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    if (showRebaseDialog) {
        // Mock commits for UI
        val mockCommits = listOf(
            CommitUi("abc1234", "Initial commit", "Author", "me@home.com", 0L),
            CommitUi("def5678", "Add feature X", "Author", "me@home.com", 0L),
            CommitUi("ghi9012", "Fix bug Y", "Author", "me@home.com", 0L),
        )
        InteractiveRebaseDialog(
            commits = mockCommits,
            onDismiss = { showRebaseDialog = false },
            onSubmit = { /* TODO implement rebase */ showRebaseDialog = false }
        )
    }

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
            Button(onClick = { showRebaseDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Slate600)) {
                Text("Rebase Interactively", color = Slate100)
            }
            Spacer(modifier = Modifier.height(16.dp))
            var commitMessage by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
            androidx.compose.material3.OutlinedTextField(
                value = commitMessage,
                onValueChange = { commitMessage = it },
                label = { Text("Commit Message", color = Slate400) },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Slate100,
                    unfocusedTextColor = Slate100,
                    focusedBorderColor = Cyan400,
                    unfocusedBorderColor = Slate600
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (commitMessage.isNotBlank()) {
                        viewModel.commit(commitMessage)
                        commitMessage = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Cyan500),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Commit All Changes", color = Slate100, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            if (uiState.isLoading) {
                Text(text = "Loading branches...", color = Slate100)
            } else {
                Text(text = "Branches: ${uiState.branches.size}", color = Slate100)
            }
        }
    }
}
