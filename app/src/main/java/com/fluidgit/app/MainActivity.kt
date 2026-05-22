package com.fluidgit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fluidgit.app.ui.theme.Amber400
import com.fluidgit.app.ui.theme.Background
import com.fluidgit.app.ui.theme.Cyan400
import com.fluidgit.app.ui.theme.Cyan500
import com.fluidgit.app.ui.theme.Cyan600
import com.fluidgit.app.ui.theme.FluidGitTheme
import com.fluidgit.app.ui.theme.Green400
import com.fluidgit.app.ui.theme.Indigo500
import com.fluidgit.app.ui.theme.Indigo600
import com.fluidgit.app.ui.theme.Slate100
import com.fluidgit.app.ui.theme.Slate300
import com.fluidgit.app.ui.theme.Slate400
import com.fluidgit.app.ui.theme.Slate500
import com.fluidgit.app.ui.theme.Slate600
import com.fluidgit.app.ui.theme.Slate700

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FluidGitTheme {
                ImmersiveFluidGitScreen()
            }
        }
    }
}

@Composable
fun ImmersiveFluidGitScreen() {
    Scaffold(
        containerColor = Background,
        contentWindowInsets = androidx.compose.foundation.layout.WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Background Glows
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawBehind {
                        drawCircle(
                            color = Cyan500.copy(alpha = 0.2f),
                            radius = 400f,
                            center = Offset(-100f, 100f)
                        )
                        drawCircle(
                            color = Indigo600.copy(alpha = 0.15f),
                            radius = 600f,
                            center = Offset(size.width + 100f, size.height - 100f)
                        )
                    }
                    .blur(radius = 100.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            )

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top App Bar
                TopAppBar()

                // Main Viewport
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Branch & Status Glass Card
                    StatusCard()

                    // Recent Activity Section
                    RecentActivitySection(modifier = Modifier.weight(1f))
                }

                // Action Bar & FAB
                ActionBar()

                // Bottom Navigation
                BottomNavBar()
            }
        }
    }
}

@Composable
fun TopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "FLUIDGIT CLIENT",
                color = Cyan400,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(0.8f)
            )
            Text(
                text = "fluid-engine-core",
                color = Slate100,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = (-0.5).sp
            )
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.05f), CircleShape)
                .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Menu,
                contentDescription = "Menu",
                tint = Slate100,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun StatusCard() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(32.dp))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(32.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                            .alpha(alpha)
                            .background(Cyan400, CircleShape)
                    )
                    Text(
                        text = "main",
                        color = Cyan400,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "Last sync: 2m ago",
                    color = Slate400,
                    fontSize = 12.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "12",
                            color = Slate100,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "PENDING",
                            color = Slate500,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(
                            text = "4 staged",
                            color = Green400.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                        Text(
                            text = "8 modified",
                            color = Amber400.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy((-8).dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Slate700, CircleShape)
                            .border(2.dp, Background, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Cyan600, CircleShape)
                            .border(2.dp, Background, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+3",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RecentActivitySection(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "RECENT COMMITS",
                color = Slate400,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
            Text(
                text = "VIEW GRAPH",
                color = Cyan400,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            CommitItem(
                title = "feat: implements liquid glass shaders",
                subtitle = "#a92f1c · 4h ago",
                color = Indigo500,
                isTop = true
            )
            CommitItem(
                title = "chore: update gradle and dependencies",
                subtitle = "#77b1e2 · Yesterday",
                color = Slate600
            )
            CommitItem(
                title = "fix: resolve merge conflicts in app module",
                subtitle = "#42c90d · Oct 24",
                color = Slate600,
                isBottom = true
            )
        }
    }
}

@Composable
fun CommitItem(
    title: String,
    subtitle: String,
    color: Color,
    isTop: Boolean = false,
    isBottom: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(if (isTop) color else Color.Transparent, CircleShape)
                    .border(if (isTop) 0.dp else 1.dp, color, CircleShape)
            )
            if (!isBottom) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(30.dp)
                        .padding(top = 4.dp)
                        .background(color.copy(alpha = if (isTop) 0.2f else 0.5f))
                )
            }
        }
        Column {
            Text(
                text = title,
                color = if (isTop) Slate100 else Slate300,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(
                text = subtitle,
                color = Slate500,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
fun ActionBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp), // Replaced padding bottom 32 with 24
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(16.dp))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(8.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
                    .clip(CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                            val brush = Brush.horizontalGradient(
                                colors = listOf(Cyan500, Indigo500),
                                startX = 0f,
                                endX = size.width * 0.65f
                            )
                            drawRect(
                                brush = brush,
                                size = Size(size.width * 0.65f, size.height)
                            )
                        }
                )
            }
            Text(
                text = "FETCHING",
                color = Slate400,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        IconButton(
            onClick = { },
            modifier = Modifier
                .size(56.dp)
                .background(Cyan500, RoundedCornerShape(16.dp))
        ) {
            Icon(
                imageVector = Icons.Rounded.Add, // fallback icon
                contentDescription = "Action",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun BottomNavBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(Color.Black.copy(alpha = 0.4f))
            .border(1.dp, Color.White.copy(alpha = 0.05f))
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavItem(icon = Icons.Rounded.Home, label = "Repos", isActive = true)
        NavItem(icon = Icons.Rounded.DateRange, label = "History", isActive = false)
        NavItem(icon = Icons.Rounded.Share, label = "Branches", isActive = false)
        NavItem(icon = Icons.Rounded.Settings, label = "Settings", isActive = false)
    }
}

@Composable
fun NavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isActive: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.alpha(if (isActive) 1f else 0.5f)
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (isActive) Cyan500.copy(alpha = 0.2f) else Color.Transparent,
                    RoundedCornerShape(12.dp)
                )
                .padding(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Cyan400 else Slate400,
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = label,
            color = if (isActive) Cyan400 else Slate400,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

