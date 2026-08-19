package com.example.musicplayer.data

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore

/**
 * Scans the device for audio files via MediaStore.
 *
 * MediaStore.Audio indexes essentially any file the system recognizes as audio —
 * MP3, AAC/M4A, FLAC, WAV, OGG Vorbis, Opus, AMR, MIDI (on supported OEMs), etc. —
 * as long as it lives in a public media directory. We deliberately do NOT filter
 * on IS_MUSIC, so ringtones-folder tracks, podcasts, voice memos, or anything
 * else classified as audio still shows up, since the goal is "play any audio file".
 *
 * For files outside MediaStore's reach (e.g. a folder the user opens directly),
 * pair this with the Storage Access Framework folder/file picker (see
 * SafFileImporter) which hands ExoPlayer a content:// Uri directly regardless
 * of format.
 */
object MusicScanner {

    fun scanAll(context: Context): List<Track> {
        val tracks = mutableListOf<Track>()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.MIME_TYPE,
        )

        // No IS_MUSIC restriction — capture every audio mime type MediaStore knows about.
        val selection = "${MediaStore.Audio.Media.DURATION} > 0"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            null,
            "${MediaStore.Audio.Media.TITLE} ASC",
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                val contentUri = ContentUris.withAppendedId(collection, id)
                val albumId = cursor.getLong(albumIdCol)
                val artUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId,
                )
                val mime = cursor.getString(mimeCol) ?: ""

                tracks += Track(
                    id = id,
                    uri = contentUri,
                    title = cursor.getString(titleCol) ?: "Unknown",
                    artist = cursor.getString(artistCol) ?: "Unknown Artist",
                    album = cursor.getString(albumCol) ?: "Unknown Album",
                    durationMs = cursor.getLong(durationCol),
                    albumArtUri = artUri,
                    fileFormat = mimeToFormatLabel(mime),
                )
            }
        }

        return tracks
    }

    private fun mimeToFormatLabel(mime: String): String = when {
        mime.contains("mpeg") -> "MP3"
        mime.contains("flac") -> "FLAC"
        mime.contains("wav") -> "WAV"
        mime.contains("ogg") -> "OGG"
        mime.contains("opus") -> "OPUS"
        mime.contains("mp4") || mime.contains("m4a") -> "M4A/AAC"
        mime.contains("aac") -> "AAC"
        mime.contains("amr") -> "AMR"
        mime.contains("midi") -> "MIDI"
        mime.contains("x-ms-wma") -> "WMA"
        mime.isBlank() -> "Unknown"
        else -> mime.substringAfterLast("/").uppercase()
    }
}
