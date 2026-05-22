package com.fluidgit.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.fluidgit.app.ui.theme.Cyan500
import com.fluidgit.app.ui.theme.PrimaryShape
import com.fluidgit.app.ui.theme.Slate100
import com.fluidgit.app.ui.theme.Slate500

@Composable
fun FluidTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    textStyle: TextStyle = LocalTextStyle.current.copy(color = Slate100),
    maxLines: Int = 1
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .shadow(
                elevation = if (isFocused) 12.dp else 4.dp,
                shape = PrimaryShape,
                ambientColor = if (isFocused) Cyan500.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.5f),
                spotColor = if (isFocused) Cyan500.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f)
            )
            .clip(PrimaryShape)
            .background(Color.White.copy(alpha = if (isFocused) 0.05f else 0.02f))
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = if (isFocused) {
                        listOf(Cyan500.copy(alpha = 0.8f), Cyan500.copy(alpha = 0.2f))
                    } else {
                        listOf(Color.White.copy(alpha = 0.15f), Color.Transparent)
                    }
                ),
                shape = PrimaryShape
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused },
            textStyle = textStyle,
            cursorBrush = SolidColor(Cyan500),
            maxLines = maxLines,
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(text = placeholder, color = Slate500, style = textStyle)
                    }
                    innerTextField()
                }
            }
        )
    }
}
