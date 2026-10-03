package com.aruack.music.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aruack.music.core.playback.PlaybackManager
import com.aruack.music.ui.components.MiniPlayer
import com.aruack.music.ui.screens.home.HomeScreen
import com.aruack.music.ui.screens.library.LibraryScreen
import com.aruack.music.ui.screens.player.NowPlayingScreen
import com.aruack.music.ui.screens.playlist.PlaylistDetailScreen
import com.aruack.music.ui.screens.search.SearchScreen
import com.aruack.music.ui.screens.settings.SettingsScreen
import com.aruack.music.ui.theme.TextMuted
import com.aruack.music.ui.theme.TextPrimary
import org.koin.androidx.compose.get

@Composable
fun MainAppNavigation(
    navController: NavHostController = rememberNavController(),
    playbackManager: PlaybackManager = get()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val playbackState by playbackManager.playbackState.collectAsState()

    var showNowPlaying by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                val showBottomBar = Screen.bottomNavItems.any { it.route == currentRoute }
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        Screen.bottomNavItems.forEach { screen ->
                            val selected = currentRoute == screen.route
                            NavigationBarItem(
                                icon = {
                                    val icon = if (selected) screen.selectedIcon else screen.unselectedIcon
                                    icon?.let {
                                        Icon(
                                            imageVector = it,
                                            contentDescription = screen.title
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontSize = 11.sp
                                    )
                                },
                                selected = selected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted,
                                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route,
                    modifier = Modifier.fillMaxSize()
                ) {
                    composable(Screen.Home.route) {
                        HomeScreen(
                            onNavigateToPlayer = { showNowPlaying = true },
                            onNavigateToPlaylist = { playlistId ->
                                navController.navigate(Screen.PlaylistDetail.createRoute(playlistId))
                            }
                        )
                    }

                    composable(Screen.Library.route) {
                        LibraryScreen(
                            onNavigateToPlaylist = { playlistId ->
                                navController.navigate(Screen.PlaylistDetail.createRoute(playlistId))
                            }
                        )
                    }

                    composable(Screen.Search.route) {
                        SearchScreen()
                    }

                    composable(Screen.Settings.route) {
                        SettingsScreen()
                    }

                    composable(
                        route = Screen.PlaylistDetail.route,
                        arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val playlistId = backStackEntry.arguments?.getLong("playlistId") ?: 0L
                        PlaylistDetailScreen(
                            playlistId = playlistId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }

                // Floating MiniPlayer above Bottom Navigation
                if (playbackState.currentSong != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 4.dp)
                    ) {
                        MiniPlayer(
                            playbackState = playbackState,
                            onPlayPauseClick = { playbackManager.togglePlayPause() },
                            onNextClick = { playbackManager.playNext() },
                            onClick = { showNowPlaying = true }
                        )
                    }
                }
            }
        }

        // Full Screen Now Playing Animated Overlay
        AnimatedVisibility(
            visible = showNowPlaying,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            NowPlayingScreen(
                onDismiss = { showNowPlaying = false }
            )
        }
    }
}
