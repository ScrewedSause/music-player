package com.example.musicplayer.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.musicplayer.data.Track
import com.example.musicplayer.player.PlaybackUiState
import com.example.musicplayer.ui.components.TrackRow
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LibraryScreen(
    tracks: List<Track>,
    isScanning: Boolean,
    playbackState: PlaybackUiState,
    onScan: () -> Unit,
    onImportFolder: (android.net.Uri) -> Unit,
    onImportFile: (android.net.Uri) -> Unit,
    onTrackClick: (Track) -> Unit,
    onOpenThemes: () -> Unit,
) {
    val permission = if (Build.VERSION.SDK_INT >= 33) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }
    val permissionState = rememberPermissionState(permission)

    val folderPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocumentTree(),
    ) { uri -> uri?.let(onImportFolder) }

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument(),
    ) { uri -> uri?.let(onImportFile) }

    LaunchedEffect(permissionState.status) {
        if (permissionState.status.isGranted && tracks.isEmpty()) onScan()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Library") },
                actions = {
                    IconButton(onClick = { filePicker.launch(arrayOf("audio/*")) }) {
                        Icon(Icons.Filled.InsertDriveFile, contentDescription = "Add file")
                    }
                    IconButton(onClick = { folderPicker.launch(null) }) {
                        Icon(Icons.Filled.CreateNewFolder, contentDescription = "Add folder")
                    }
                    IconButton(onClick = onOpenThemes) {
                        Icon(Icons.Filled.Palette, contentDescription = "Themes")
                    }
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                !permissionState.status.isGranted -> {
                    PermissionRequestState(
                        onRequest = { permissionState.launchPermissionRequest() },
                    )
                }
                isScanning -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Scanning your library…")
                        }
                    }
                }
                tracks.isEmpty() -> {
                    EmptyLibraryState(
                        onScan = onScan,
                        onImportFolder = { folderPicker.launch(null) },
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        item {
                            Text(
                                text = "${tracks.size} tracks · any format supported",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp),
                            )
                        }
                        items(tracks, key = { it.uri.toString() }) { track ->
                            TrackRow(
                                track = track,
                                isActive = playbackState.currentTrack?.uri == track.uri,
                                onClick = { onTrackClick(track) },
                            )
                        }
                        item { Spacer(modifier = Modifier.height(88.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionRequestState(onRequest: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.LibraryMusic,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Allow access to your audio files to build your library",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onRequest) { Text("Grant permission") }
        }
    }
}

@Composable
private fun EmptyLibraryState(onScan: () -> Unit, onImportFolder: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Filled.MusicOff,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("No tracks found yet", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Rescan your device, or import a specific folder — every audio format is supported, indexed or not.",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                OutlinedButton(onClick = onScan) { Text("Rescan device") }
                Spacer(modifier = Modifier.width(12.dp))
                Button(onClick = onImportFolder) { Text("Import folder") }
            }
        }
    }
}
