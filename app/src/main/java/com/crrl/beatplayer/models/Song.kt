/*
 * Copyright 2019 Carlos René Ramos López. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.crrl.beatplayer.models

import android.database.Cursor
import com.crrl.beatplayer.extensions.fix
import com.google.gson.Gson

data class Song(
    val id: Long = 0,
    val albumId: Long = 0,
    val artistId: Long = 0,
    val title: String = "Title",
    val artist: String = "Artist",
    val album: String = "Album",
    val duration: Int = 0,
    val trackNumber: Int = 0,
    val path: String = ""
) : MediaItem(id) {

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

    override fun compare(other: MediaItem): Boolean {
        other as Song
        return id == other.id && title == other.title && artist == other.artist && album == other.album
                && duration == other.duration && trackNumber == other.trackNumber && artistId == other.artistId
                && albumId == other.albumId && path == other.path
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }
}
