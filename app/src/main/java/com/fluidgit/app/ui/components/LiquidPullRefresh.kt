package com.fluidgit.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.fluidgit.app.ui.theme.CyanNeon

@Composable
fun LiquidPullRefreshIndicator(
    isRefreshing: Boolean,
    progress: Float,
    modifier: Modifier = Modifier
) {
    val coerceProgress = progress.coerceIn(0f, 1f)
    
    // Liquid morphing logic based on progress
    val morphProgress by animateFloatAsState(
        targetValue = if (isRefreshing) 1f else coerceProgress,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "LiquidMorph"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val baseRadius = 20.dp.toPx()

            if (isRefreshing) {
                // Spinning ring
                drawArc(
                    color = CyanNeon,
                    startAngle = (System.currentTimeMillis() % 3600L) / 10f, // manual rotation
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(center.x - baseRadius, center.y - baseRadius),
                    size = androidx.compose.ui.geometry.Size(baseRadius * 2, baseRadius * 2),
                    style = Stroke(width = 4.dp.toPx())
                )
            } else {
                // Liquid drop
                val path = Path().apply {
                    val dropY = center.y + (morphProgress * 40.dp.toPx())
                    val radius = baseRadius * morphProgress
                    addOval(androidx.compose.ui.geometry.Rect(
                        left = center.x - radius,
                        top = dropY - radius,
                        right = center.x + radius,
                        bottom = dropY + radius
                    ))
                }
                drawPath(path, color = CyanNeon)
            }
        }
    }
}
