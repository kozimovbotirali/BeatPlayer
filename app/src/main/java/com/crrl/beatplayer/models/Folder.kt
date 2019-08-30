package com.crrl.beatplayer.models

import android.database.Cursor
import com.crrl.beatplayer.extensions.fixedName
import com.crrl.beatplayer.extensions.fixedPath
import com.google.gson.Gson
import java.io.File

class Folder(
    val id: Long,
    val name: String,
    val albumId: Long,
    val path: String,
    val songIds: MutableList<Long> = mutableListOf()
) : MediaItem(id) {
    companion object {
        fun createFromCursor(cursor: Cursor): Folder {
            return Folder(
                cursor.getLong(0),
                File(File(cursor.getString(2)).parent!!).fixedName(),
                cursor.getLong(1),
                File(File(cursor.getString(2)).parent!!).fixedPath()
            )
        }
    }

    override fun compare(other: MediaItem): Boolean {
        other as Folder
        return id == other.id && name == other.name && albumId == other.albumId && path == other.path && songIds.size == other.songIds.size
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }
}