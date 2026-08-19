package com.example.musicplayer.data

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import androidx.documentfile.provider.DocumentFile

/**
 * Storage Access Framework helper: lets the user pick a single file or an
 * entire folder and imports every file inside as a Track, regardless of
 * whether MediaStore recognizes/indexes that particular audio format.
 * ExoPlayer's extractors handle demuxing once given the content:// Uri, so
 * as long as it's audio, playback works even if MediaStore never scanned it.
 */
object SafFileImporter {

    private val AUDIO_EXTENSIONS = setOf(
        "mp3", "flac", "wav", "wave", "ogg", "oga", "opus", "m4a", "aac",
        "wma", "aiff", "aif", "ape", "alac", "amr", "mid", "midi", "dsf",
        "dff", "tta", "wv", "mka", "3gp", "caf",
    )

    fun importFromTree(context: Context, treeUri: Uri): List<Track> {
        val results = mutableListOf<Track>()
        val root = DocumentFile.fromTreeUri(context, treeUri) ?: return results
        walk(root, results)
        return results
    }

    fun importSingleFile(context: Context, fileUri: Uri): Track? {
        val doc = DocumentFile.fromSingleUri(context, fileUri) ?: return null
        return toTrackOrNull(doc)
    }

    private fun walk(dir: DocumentFile, out: MutableList<Track>) {
        for (child in dir.listFiles()) {
            if (child.isDirectory) {
                walk(child, out)
            } else {
                toTrackOrNull(child)?.let { out += it }
            }
        }
    }

    private fun toTrackOrNull(doc: DocumentFile): Track? {
        val name = doc.name ?: return null
        val ext = name.substringAfterLast('.', "").lowercase()
        val isAudioMime = doc.type?.startsWith("audio/") == true
        if (!isAudioMime && ext !in AUDIO_EXTENSIONS) return null

        return Track(
            id = doc.uri.hashCode().toLong(),
            uri = doc.uri,
            title = name.substringBeforeLast('.'),
            artist = "Unknown Artist",
            album = "Imported Files",
            durationMs = 0L, // resolved by the player once loaded
            albumArtUri = null,
            fileFormat = ext.uppercase().ifBlank { "AUDIO" },
        )
    }

    fun queryDisplayName(context: Context, uri: Uri): String? {
        context.contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
            ?.use { c -> if (c.moveToFirst()) return c.getString(0) }
        return null
    }
}
