package com.fluidgit.app

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.ui.res.stringResource
import com.fluidgit.app.R

class MainActivity : FragmentActivity() {
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        val app = application as FluidGitApplication
        val settingsRepository = app.container.settingsRepository
        val authHandler = AuthHandler(settingsRepository, this.lifecycleScope)
        authHandler.handleAuthIntent(intent.data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        val app = application as FluidGitApplication
        val settingsRepository = app.container.settingsRepository
        val authHandler = AuthHandler(settingsRepository, this.lifecycleScope)
        authHandler.handleAuthIntent(intent.data)

        setContent {
            val globalViewModel: GlobalViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = AppViewModelProvider)
            val themeState by globalViewModel.themeState.collectAsState()
            val githubToken by settingsRepository.githubToken.collectAsState(initial = null)
            var isUnlocked by remember { mutableStateOf(false) }

            LaunchedEffect(themeState.isBiometricLockEnabled, themeState.isOnboardingCompleted) {
                if (!themeState.isOnboardingCompleted) {
                    isUnlocked = true // Don't require biometrics during onboarding
                    return@LaunchedEffect
                }
                
                if (themeState.isBiometricLockEnabled) {
                    val executor = ContextCompat.getMainExecutor(this@MainActivity)
                    val biometricPrompt = BiometricPrompt(this@MainActivity, executor,
                        object : BiometricPrompt.AuthenticationCallback() {
                            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                                isUnlocked = true
                            }
                            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            }
                        })
                    val promptInfo = BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Unlock Fluid Git")
                        .setSubtitle("Confirm identity to access secure repositories")
                        .setNegativeButtonText("Cancel")
                        .build()
                    biometricPrompt.authenticate(promptInfo)
                } else {
                    isUnlocked = true
                }
            }

            FluidGitTheme(
                isLiquidLight = themeState.isLiquidLight,
                isAmoled = themeState.isAmoled
            ) {
                if (!themeState.isOnboardingCompleted) {
                    com.fluidgit.app.ui.screens.onboarding.OnboardingScreen(
                        onFinish = { globalViewModel.setOnboardingCompleted(true) }
                    )
                } else if (githubToken == null) {
                    com.fluidgit.app.ui.screens.login.LoginScreen(
                        onLoginSuccess = { token ->
                            this@MainActivity.lifecycleScope.launch {
                                settingsRepository.setGithubToken(token)
                            }
                        }
                    )
                } else if (isUnlocked) {
                    ImmersiveFluidGitScreen()
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Background)) {
                        Text("Unlock to view", color = Slate400, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}

@Composable
fun ImmersiveFluidGitScreen() {
    val navController = androidx.navigation.compose.rememberNavController()

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

                // Main Viewport replaces static content with NavGraph
                com.fluidgit.app.ui.navigation.FluidGitNavGraph(
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            }

            // Floating Radial Menu at bottom center
            com.fluidgit.app.ui.components.RadialMenu(
                items = listOf(
                    com.fluidgit.app.ui.components.RadialMenuItem(
                        icon = Icons.Rounded.Home,
                        label = "Home",
                        onClick = { navController.navigate(com.fluidgit.app.ui.navigation.Destinations.REPOS) }
                    ),
                    com.fluidgit.app.ui.components.RadialMenuItem(
                        icon = Icons.Rounded.DateRange,
                        label = "Activity",
                        onClick = { navController.navigate(com.fluidgit.app.ui.navigation.Destinations.ACTIVITY) }
                    ),
                    com.fluidgit.app.ui.components.RadialMenuItem(
                        icon = Icons.Rounded.Share,
                        label = "Files",
                        onClick = { navController.navigate(com.fluidgit.app.ui.navigation.Destinations.FILE_MANAGER) }
                    ),
                    com.fluidgit.app.ui.components.RadialMenuItem(
                        icon = Icons.Rounded.Settings,
                        label = "Settings",
                        onClick = { navController.navigate(com.fluidgit.app.ui.navigation.Destinations.SETTINGS) }
                    )
                ),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
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
                text = stringResource(R.string.fluid_git_client),
                color = Cyan400,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(0.8f)
            )
            Text(
                text = stringResource(R.string.fluid_engine_core),
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

// Unused composables removed

