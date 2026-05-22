package com.fluidgit.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fluidgit.app.ui.theme.Cyan400
import com.fluidgit.app.ui.theme.Slate100
import com.fluidgit.app.ui.theme.Slate400
import com.fluidgit.app.ui.theme.Slate700
import com.fluidgit.app.ui.theme.Background
import kotlin.random.Random

data class MockCodeLine(val content: String, val author: String, val ageIndex: Int)

@Composable
fun FileManagerScreen() {
    var blameEnabled by remember { mutableStateOf(false) }

    val mockFileContent = remember {
        listOf(
            MockCodeLine("package com.fluidgit.app.ui.screens", "Alice", 0),
            MockCodeLine("", "Alice", 0),
            MockCodeLine("import androidx.compose.foundation.layout.Box", "Bob", 2),
            MockCodeLine("import androidx.compose.runtime.Composable", "Alice", 0),
            MockCodeLine("", "Alice", 0),
            MockCodeLine("@Composable", "Charlie", 1),
            MockCodeLine("fun LiquidWave() {", "Charlie", 1),
            MockCodeLine("    // Implementation", "Alice", 4),
            MockCodeLine("}", "Charlie", 1)
        )
    }

    // Colors mapping to age
    val blameColors = listOf(
        Color(0xFF2E3440), // fresh
        Color(0xFF3B4252),
        Color(0xFF434C5E),
        Color(0xFF4C566A),
        Color(0xFF88C0D0)  // very old
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("FileEditor.kt", color = Slate100, fontSize = 20.sp, modifier = Modifier.weight(1f))
            Text("Blame", color = Slate400, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = blameEnabled,
                onCheckedChange = { blameEnabled = it },
                colors = SwitchDefaults.colors(checkedThumbColor = Cyan400, checkedTrackColor = Slate700)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            LazyColumn {
                itemsIndexed(mockFileContent) { index, line ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (blameEnabled) blameColors[line.ageIndex.coerceIn(0, 4)] else Color.Transparent)
                            .padding(end = 8.dp)
                    ) {
                        if (blameEnabled) {
                            Text(
                                text = line.author.take(4),
                                color = Slate400,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .width(40.dp)
                                    .padding(start = 4.dp, top = 2.dp)
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                color = Slate400,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier
                                    .width(30.dp)
                                    .padding(start = 4.dp, top = 2.dp)
                            )
                        }
                        Text(
                            text = line.content,
                            color = Slate100,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

