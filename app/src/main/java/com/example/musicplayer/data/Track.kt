package com.example.musicplayer.data

import android.net.Uri

data class Track(
    val id: Long,
    val uri: Uri,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val albumArtUri: Uri? = null,
    val fileFormat: String = "", // e.g. "MP3", "FLAC", "WAV", "OGG", "OPUS", "M4A"
)
