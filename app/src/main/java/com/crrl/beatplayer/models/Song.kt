package com.crrl.beatplayer.models

import android.database.Cursor
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.crrl.beatplayer.extensions.fix
import com.google.gson.Gson

@Entity(tableName = "song_table")
data class Song(
    @PrimaryKey var id: Long = 0,
    var albumId: Long = 0,
    var artistId: Long = 0,
    var title: String = "",
    var artist: String = "",
    var album: String = "",
    var duration: Int = 0,
    var trackNumber: Int = 0,
    var path: String,
    var playListId: Long = 0
) : MediaItem() {

    companion object {
        fun createFromCursor(cursor: Cursor, album_id: Long = 0): Song {
            val id = cursor.getLong(0)
            val title = cursor.getString(1)
            val artist = cursor.getString(2)
            val album = cursor.getString(3)
            val duration = cursor.getInt(4)
            val trackNumber = cursor.getInt(5).fix()
            val artistId = cursor.getLong(6)
            val albumId = if (album_id == 0L) cursor.getLong(7) else album_id
            val path = if (album_id == 0L) cursor.getString(8) else cursor.getString(7)
            return Song(id, albumId, artistId, title, artist, album, duration, trackNumber, path)
        }

        fun createFromPlaylistCursor(cursor: Cursor): Song {
            val id = cursor.getLong(1)
            val title = cursor.getString(2)
            val artist = cursor.getString(3)
            val album = cursor.getString(4)
            val duration = cursor.getInt(5)
            val trackNumber = cursor.getInt(6).fix()
            val artistId = cursor.getLong(7)
            val albumId = cursor.getLong(8)
            val path = cursor.getString(9)
            return Song(id, albumId, artistId, title, artist, album, duration, trackNumber, path)
        }
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }
}
