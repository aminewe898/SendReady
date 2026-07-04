package com.example.sendready

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sendready.core.designsystem.components.SRBottomBar
import com.example.sendready.core.designsystem.components.SRBottomNavItem
import com.example.sendready.core.designsystem.components.SRScaffold
import com.example.sendready.core.navigation.Destination
import com.example.sendready.feature.compress.CompressScreen
import com.example.sendready.feature.finished.FinishedScreen
import com.example.sendready.feature.history.HistoryScreen
import com.example.sendready.feature.home.HomeScreen
import com.example.sendready.feature.progress.ProgressScreen
import com.example.sendready.feature.settings.SettingsScreen

@Composable
fun SendReadyApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        SRBottomNavItem(
            route = Destination.Home::class.qualifiedName ?: "",
            label = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        SRBottomNavItem(
            route = Destination.History::class.qualifiedName ?: "",
            label = "History",
            selectedIcon = Icons.AutoMirrored.Filled.List,
            unselectedIcon = Icons.AutoMirrored.Outlined.List
        ),
        SRBottomNavItem(
            route = Destination.Settings::class.qualifiedName ?: "",
            label = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Outlined.Settings
        )
    )

    // Show bottom bar only on top level destinations
    val showBottomBar = currentDestination?.hierarchy?.any { dest ->
        dest.hasRoute(Destination.Home::class) || 
        dest.hasRoute(Destination.History::class) || 
        dest.hasRoute(Destination.Settings::class)
    } == true

    SRScaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                val currentRoute = currentDestination?.route?.substringBefore("?")?.substringBefore("/")
                SRBottomBar(
                    items = bottomNavItems,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo<Destination.Home> {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Home,
            modifier = Modifier.padding(padding),
            enterTransition = { androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) + androidx.compose.animation.slideInVertically(initialOffsetY = { 50 }, animationSpec = androidx.compose.animation.core.tween(300)) },
            exitTransition = { androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300)) },
            popEnterTransition = { androidx.compose.animation.fadeIn(animationSpec = androidx.compose.animation.core.tween(300)) },
            popExitTransition = { androidx.compose.animation.fadeOut(animationSpec = androidx.compose.animation.core.tween(300)) + androidx.compose.animation.slideOutVertically(targetOffsetY = { 50 }, animationSpec = androidx.compose.animation.core.tween(300)) }
        ) {
            composable<Destination.Home> {
                HomeScreen(
                    onNavigateToCompress = { navController.navigate(Destination.Compress) }
                )
            }
            
            composable<Destination.Compress> {
                CompressScreen(
                    onNavigateToProgress = { destId -> navController.navigate(Destination.Progress(destId)) },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable<Destination.Progress> { backStackEntry ->
                val progress = backStackEntry.arguments?.getString("destinationId") ?: ""
                ProgressScreen(
                    destinationId = progress,
                    onNavigateToFinished = { videoId ->
                        navController.navigate(Destination.Finished(videoId)) {
                            popUpTo<Destination.Home>()
                        }
                    }
                )
            }
            
            composable<Destination.Finished> { backStackEntry ->
                val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
                FinishedScreen(
                    videoId = videoId,
                    onNavigateHome = {
                        navController.navigate(Destination.Home) {
                            popUpTo(navController.graph.id)
                        }
                    }
                )
            }
            
            composable<Destination.History> {
                HistoryScreen(
                    onNavigateToFinished = { videoId -> navController.navigate(Destination.Finished(videoId)) }
                )
            }
            
            composable<Destination.Settings> {
                SettingsScreen()
            }
        }
    }
}
