package com.fluidgit.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.fluidgit.app.ui.screens.repos.ReposScreen

object Destinations {
    const val REPOS = "repos"
    const val REPO_DETAIL = "repoDetail/{repoId}"
    const val SETTINGS = "settings"
    const val ACTIVITY = "activityFeed"
    const val FILE_MANAGER = "fileManager"
    
    fun createRepoDetailRoute(repoId: String) = "repoDetail/$repoId"
}

@Composable
fun FluidGitNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Destinations.REPOS,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = androidx.compose.animation.core.spring(
                    dampingRatio = 0.6f,
                    stiffness = 300f
                )
            ) + scaleIn(
                initialScale = 0.9f, 
                animationSpec = tween(400)
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 2 },
                animationSpec = tween(400)
            ) + fadeOut(animationSpec = tween(400))
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 2 },
                animationSpec = tween(400)
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(400)
            ) + fadeOut(animationSpec = tween(400))
        }
    ) {
        composable(Destinations.REPOS) {
            ReposScreen(
                onNavigateToDetail = { repoId ->
                    navController.navigate(Destinations.createRepoDetailRoute(repoId))
                }
            )
        }
        composable(
            route = Destinations.REPO_DETAIL,
            arguments = listOf(navArgument("repoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val repoId = backStackEntry.arguments?.getString("repoId") ?: ""
            com.fluidgit.app.ui.screens.detail.RepoDetailScreen(repoId = repoId)
        }
        composable(Destinations.SETTINGS) {
            com.fluidgit.app.ui.screens.settings.SettingsScreen()
        }
        composable(Destinations.ACTIVITY) {
            com.fluidgit.app.ui.screens.activity.ActivityFeedScreen()
        }
        composable(Destinations.FILE_MANAGER) {
            com.fluidgit.app.ui.screens.FileManagerScreen()
        }
    }
}
