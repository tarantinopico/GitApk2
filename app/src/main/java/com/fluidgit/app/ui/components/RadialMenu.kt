package com.fluidgit.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.fluidgit.app.ui.theme.Cyan400
import com.fluidgit.app.ui.theme.Cyan500
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text

data class RadialMenuItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String,
    val onClick: () -> Unit
)

@Composable
fun RadialMenu(
    items: List<RadialMenuItem>,
    modifier: Modifier = Modifier,
    mainIcon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Rounded.Add
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableStateOf(-1) }
    val haptic = LocalHapticFeedback.current

    val mainRotation by animateFloatAsState(
        targetValue = if (isExpanded) 135f else 0f,
        animationSpec = spring(stiffness = 300f, dampingRatio = 0.5f),
        label = "main_rotation"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        items.forEachIndexed { index, item ->
            val angle = Math.PI + (Math.PI / (items.size - 1)) * index
            // If selected, suck into center (radius 0)
            val suckIn = selectedIndex != -1 && selectedIndex != index
            val radius = if (isExpanded && selectedIndex == -1) 250f else 0f
            val targetX = (cos(angle) * radius).toInt()
            val targetY = (sin(angle) * radius).toInt()
            
            val animX by animateFloatAsState(
                targetValue = targetX.toFloat(),
                animationSpec = spring(stiffness = 200f, dampingRatio = if (selectedIndex != -1) 0.9f else 0.6f),
                label = "x_$index"
            )
            val animY by animateFloatAsState(
                targetValue = targetY.toFloat(),
                animationSpec = spring(stiffness = 200f, dampingRatio = if (selectedIndex != -1) 0.9f else 0.6f),
                label = "y_$index"
            )
            val animAlpha by animateFloatAsState(
                targetValue = if (isExpanded && (selectedIndex == -1 || selectedIndex == index)) 1f else 0f,
                animationSpec = spring(stiffness = 200f),
                label = "alpha_$index"
            )

            if (animAlpha > 0.01f) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(animX.toInt(), animY.toInt()) }
                        .alpha(animAlpha)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                selectedIndex = index
                                // Delay the actual onClick to allow suck-in animation to finish
                                item.onClick()
                            }
                        )
                        .padding(12.dp)
                ) {
                    androidx.compose.foundation.layout.Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = Cyan400,
                            modifier = Modifier.size(24.dp)
                        )
                        if (item.label.isNotEmpty()) {
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(8.dp))
                            Text(text = item.label, color = Cyan400, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Main FAB
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                .drawBehind {
                    drawCircle(color = Cyan500.copy(alpha = 0.2f), radius = size.width * 0.8f) // neon glow
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        if (isExpanded) {
                            selectedIndex = -1
                            isExpanded = false
                        } else {
                            selectedIndex = -1
                            isExpanded = true
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = mainIcon,
                contentDescription = "Main Menu",
                tint = Cyan400,
                modifier = Modifier
                    .size(32.dp)
                    .rotate(mainRotation)
            )
        }
    }
}
