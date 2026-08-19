package com.example.musicplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.ui.components.MiniPlayerBar
import com.example.musicplayer.ui.screens.LibraryScreen
import com.example.musicplayer.ui.screens.PlayerScreen
import com.example.musicplayer.ui.screens.ThemesScreen
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import com.example.musicplayer.viewmodel.LibraryViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val theme by viewModel.currentTheme.collectAsStateWithLifecycle()

            MusicPlayerTheme(appTheme = theme) {
                val navController = rememberNavController()
                AppNavHost(navController, viewModel)
            }
        }
    }
}

private const val ROUTE_LIBRARY = "library"
private const val ROUTE_PLAYER = "player"
private const val ROUTE_THEMES = "themes"

@androidx.compose.runtime.Composable
private fun AppNavHost(navController: NavHostController, viewModel: LibraryViewModel) {
    val tracks by viewModel.tracks.collectAsStateWithLifecycle()
    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()

    NavHost(navController = navController, startDestination = ROUTE_LIBRARY) {
        composable(ROUTE_LIBRARY) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
                    LibraryScreen(
                        tracks = tracks,
                        isScanning = isScanning,
                        playbackState = playbackState,
                        onScan = viewModel::scanLibrary,
                        onImportFolder = viewModel::importFolder,
                        onImportFile = viewModel::importSingleFile,
                        onTrackClick = { track ->
                            viewModel.playTrack(track)
                            navController.navigate(ROUTE_PLAYER)
                        },
                        onOpenThemes = { navController.navigate(ROUTE_THEMES) },
                    )
                }
                if (playbackState.currentTrack != null) {
                    MiniPlayerBar(
                        state = playbackState,
                        onClick = { navController.navigate(ROUTE_PLAYER) },
                        onPlayPause = viewModel.playerController::togglePlayPause,
                        onNext = viewModel.playerController::skipToNext,
                    )
                }
            }
        }
        composable(ROUTE_PLAYER) {
            PlayerScreen(
                state = playbackState,
                onBack = { navController.popBackStack() },
                onPlayPause = viewModel.playerController::togglePlayPause,
                onNext = viewModel.playerController::skipToNext,
                onPrevious = viewModel.playerController::skipToPrevious,
                onSeek = viewModel.playerController::seekTo,
                onShuffleToggle = viewModel.playerController::toggleShuffle,
                onRepeatCycle = viewModel.playerController::cycleRepeatMode,
            )
        }
        composable(ROUTE_THEMES) {
            ThemesScreen(
                currentTheme = currentTheme,
                onSelect = { viewModel.setTheme(it) },
                onBack = { navController.popBackStack() },
            )
        }
    }
}
