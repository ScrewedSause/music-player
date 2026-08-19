package com.example.musicplayer.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicplayer.data.Track
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class RepeatMode { OFF, ONE, ALL }

data class PlaybackUiState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val queue: List<Track> = emptyList(),
    val queueIndex: Int = -1,
)

/**
 * Thin wrapper around a MediaController connected to [PlaybackService].
 * Keeps a single source of truth for the UI via StateFlow and hides all
 * Media3 plumbing from Compose screens.
 */
class PlayerController(private val context: Context) {

    private var controller: MediaController? = null
    private var queue: List<Track> = emptyList()

    private val _state = MutableStateFlow(PlaybackUiState())
    val state: StateFlow<PlaybackUiState> = _state.asStateFlow()

    fun connect(onReady: () -> Unit = {}) {
        val sessionToken = SessionToken(context, ComponentName(context, PlaybackService::class.java))
        val future = MediaController.Builder(context, sessionToken).buildAsync()
        future.addListener({
            controller = future.get()
            attachListener()
            onReady()
        }, MoreExecutors.directExecutor())
    }

    private fun attachListener() {
        controller?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.value = _state.value.copy(isPlaying = isPlaying)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val index = controller?.currentMediaItemIndex ?: -1
                val track = queue.getOrNull(index)
                _state.value = _state.value.copy(
                    currentTrack = track,
                    queueIndex = index,
                    durationMs = controller?.duration?.coerceAtLeast(0) ?: 0L,
                )
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _state.value = _state.value.copy(
                    durationMs = controller?.duration?.coerceAtLeast(0) ?: 0L,
                )
            }
        })
    }

    /** Poll position periodically from the UI layer (e.g. every 500ms) via this. */
    fun pollPosition() {
        val c = controller ?: return
        _state.value = _state.value.copy(
            positionMs = c.currentPosition.coerceAtLeast(0),
            durationMs = c.duration.coerceAtLeast(0),
        )
    }

    fun playQueue(tracks: List<Track>, startIndex: Int) {
        queue = tracks
        val items = tracks.map { track ->
            MediaItem.Builder()
                .setUri(track.uri)
                .setMediaId(track.id.toString())
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(track.title)
                        .setArtist(track.artist)
                        .setAlbumTitle(track.album)
                        .setArtworkUri(track.albumArtUri)
                        .build(),
                )
                .build()
        }
        controller?.apply {
            setMediaItems(items, startIndex, 0L)
            prepare()
            play()
        }
        _state.value = _state.value.copy(queue = tracks, queueIndex = startIndex, currentTrack = tracks.getOrNull(startIndex))
    }

    fun togglePlayPause() {
        val c = controller ?: return
        if (c.isPlaying) c.pause() else c.play()
    }

    fun skipToNext() = controller?.seekToNextMediaItem()
    fun skipToPrevious() = controller?.seekToPreviousMediaItem()

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
        _state.value = _state.value.copy(positionMs = positionMs)
    }

    fun toggleShuffle() {
        val c = controller ?: return
        val newValue = !c.shuffleModeEnabled
        c.shuffleModeEnabled = newValue
        _state.value = _state.value.copy(shuffleEnabled = newValue)
    }

    fun cycleRepeatMode() {
        val c = controller ?: return
        val next = when (_state.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        c.repeatMode = when (next) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        }
        _state.value = _state.value.copy(repeatMode = next)
    }

    fun release() {
        controller?.release()
        controller = null
    }
}
