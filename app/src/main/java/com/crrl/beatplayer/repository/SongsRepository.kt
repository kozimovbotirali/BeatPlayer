/*
 * Copyright (c) 2020. Carlos René Ramos López. All rights reserved.
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

package com.crrl.beatplayer.repository

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.BaseColumns._ID
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media.*
import com.crrl.beatplayer.extensions.toList
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.utils.SettingsUtility
import com.crrl.beatplayer.utils.SortModes
import java.io.File

interface SongsRepositoryInterface {
    fun loadSongs(): List<Song>
    fun getSongForId(id: Long): Song
    fun searchSongs(searchString: String, limit: Int): List<Song>
    fun deleteTracks(ids: LongArray): Int
}

class SongsRepository() : SongsRepositoryInterface {

    private lateinit var contentResolver: ContentResolver
    private lateinit var settingsUtility: SettingsUtility
    private lateinit var context: Context

    constructor(context: Context?) : this() {
        contentResolver = context!!.contentResolver
        this.context = context
        settingsUtility = SettingsUtility.getInstance(context)
    }

    override fun loadSongs(): List<Song> {
        val sl = makeSongCursor(null, null)
            .toList(true) {
                Song.createFromCursor(this)
            }
        SortModes.sortSongList(sl, settingsUtility.songSortOrder)
        return sl
    }

    override fun getSongForId(id: Long): Song {
        val cursor = makeSongCursor("_id=$id", null)!!
        cursor.use {
            return if (it.moveToFirst()) {
                Song.createFromCursor(it)
            } else {
                Song()
            }
        }
    }

    override fun searchSongs(searchString: String, limit: Int): List<Song> {
        val result = makeSongCursor("title LIKE ?", arrayOf("$searchString%"))
            .toList(true) { Song.createFromCursor(this) }
        if (result.size < limit) {
            val moreSongs = makeSongCursor("title LIKE ?", arrayOf("%_$searchString%"))
                .toList(true) { Song.createFromCursor(this) }
            result += moreSongs
        }
        return if (result.size < limit) {
            result
        } else {
            result.subList(0, limit)
        }
    }

    // TODO a lot of operations are done here without verifying results,
    // TODO e.g. if moveToFirst() returns true...
    override fun deleteTracks(ids: LongArray): Int {
        val projection = arrayOf(
            _ID,
            MediaStore.MediaColumns.DATA,
            MediaStore.Audio.AudioColumns.ALBUM_ID
        )
        val selection = StringBuilder().apply {
            append("$_ID IN (")
            for (i in ids.indices) {
                append(ids[i])
                if (i < ids.size - 1) {
                    append(",")
                }
            }
            append(")")
        }

        contentResolver.query(
            EXTERNAL_CONTENT_URI,
            projection,
            selection.toString(),
            null,
            null
        )?.use {
            it.moveToFirst()
            // Step 2: Remove selected tracks from the database
            contentResolver.delete(EXTERNAL_CONTENT_URI, selection.toString(), null)

            // Step 3: Remove files from card
            it.moveToFirst()
            while (!it.isAfterLast) {
                val name = it.getString(1)
                val f = File(name)
                try { // File.delete can throw a security exception
                    if (!f.delete()) {
                        // I'm not sure if we'd ever get here (deletion would
                        // have to fail, but no exception thrown)
                        print("Failed to delete file: $name")
                    }
                } catch (_: SecurityException) {
                }
                it.moveToNext()
            }
        }

        return ids.size
    }

    private fun makeSongCursor(
        selection: String?,
        paramArrayOfString: Array<String>?,
        sortOrder: String
    ): Cursor? {
        return contentResolver.query(
            EXTERNAL_CONTENT_URI,
            arrayOf(
                _ID,
                TITLE,
                ARTIST,
                ALBUM,
                DURATION,
                TRACK,
                ARTIST_ID,
                ALBUM_ID
            ),
            selection,
            paramArrayOfString,
            sortOrder
        )
    }

    private fun makeSongCursor(selection: String?, paramArrayOfString: Array<String>?): Cursor? {
        val songSortOrder = settingsUtility.songSortOrder
        return makeSongCursor(selection, paramArrayOfString, songSortOrder)
    }
}
