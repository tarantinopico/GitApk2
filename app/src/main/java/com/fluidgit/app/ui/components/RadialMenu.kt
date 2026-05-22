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

data class RadialMenuItem(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit
)

@Composable
fun RadialMenu(
    items: List<RadialMenuItem>,
    modifier: Modifier = Modifier,
    mainIcon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Rounded.Add
) {
    var isExpanded by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    val mainRotation by animateFloatAsState(
        targetValue = if (isExpanded) 45f else 0f,
        animationSpec = spring(stiffness = 300f, dampingRatio = 0.5f),
        label = "main_rotation"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        items.forEachIndexed { index, item ->
            val angle = Math.PI + (Math.PI / (items.size - 1)) * index
            val radius = 100f // roughly 100dp depending on density but we'll use dp
            val targetX = if (isExpanded) (cos(angle) * 250f).toInt() else 0
            val targetY = if (isExpanded) (sin(angle) * 250f).toInt() else 0
            
            val animX by animateFloatAsState(
                targetValue = targetX.toFloat(),
                animationSpec = spring(stiffness = 200f, dampingRatio = 0.6f),
                label = "x_$index"
            )
            val animY by animateFloatAsState(
                targetValue = targetY.toFloat(),
                animationSpec = spring(stiffness = 200f, dampingRatio = 0.6f),
                label = "y_$index"
            )
            val animAlpha by animateFloatAsState(
                targetValue = if (isExpanded) 1f else 0f,
                animationSpec = spring(stiffness = 200f),
                label = "alpha_$index"
            )

            if (animAlpha > 0.01f) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(animX.toInt(), animY.toInt()) }
                        .alpha(animAlpha)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                isExpanded = false
                                item.onClick()
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = "Menu Item",
                        tint = Cyan400,
                        modifier = Modifier.size(24.dp)
                    )
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
                        isExpanded = !isExpanded
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
