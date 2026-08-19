package com.example.musicplayer.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicplayer.data.MusicScanner
import com.example.musicplayer.data.SafFileImporter
import com.example.musicplayer.data.SettingsRepository
import com.example.musicplayer.data.Track
import com.example.musicplayer.player.PlaybackUiState
import com.example.musicplayer.player.PlayerController
import com.example.musicplayer.ui.theme.AppTheme
import com.example.musicplayer.ui.theme.AppThemes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)
    val playerController = PlayerController(application)

    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _currentTheme = MutableStateFlow(AppThemes.Dark)
    val currentTheme: StateFlow<AppTheme> = _currentTheme.asStateFlow()

    val playbackState: StateFlow<PlaybackUiState> = playerController.state

    init {
        viewModelScope.launch {
            settingsRepository.selectedThemeId.collect { id ->
                _currentTheme.value = AppThemes.byId(id)
            }
        }
        playerController.connect()
        // Lightweight position polling loop for the mini/full player UI.
        viewModelScope.launch {
            while (true) {
                playerController.pollPosition()
                delay(500)
            }
        }
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch { settingsRepository.setThemeId(theme.id) }
    }

    fun scanLibrary() {
        viewModelScope.launch {
            _isScanning.value = true
            val scanned = withContext(Dispatchers.IO) {
                MusicScanner.scanAll(getApplication())
            }
            _tracks.value = scanned
            _isScanning.value = false
        }
    }

    fun importFolder(treeUri: Uri) {
        viewModelScope.launch {
            _isScanning.value = true
            val imported = withContext(Dispatchers.IO) {
                SafFileImporter.importFromTree(getApplication(), treeUri)
            }
            _tracks.value = (_tracks.value + imported).distinctBy { it.uri }
            _isScanning.value = false
        }
    }

    fun importSingleFile(fileUri: Uri) {
        viewModelScope.launch {
            val track = withContext(Dispatchers.IO) {
                SafFileImporter.importSingleFile(getApplication(), fileUri)
            }
            if (track != null) {
                _tracks.value = (_tracks.value + track).distinctBy { it.uri }
                playTrack(track)
            }
        }
    }

    fun playTrack(track: Track) {
        val list = _tracks.value
        val index = list.indexOfFirst { it.uri == track.uri }.coerceAtLeast(0)
        playerController.playQueue(list, index)
    }

    override fun onCleared() {
        playerController.release()
        super.onCleared()
    }
}
