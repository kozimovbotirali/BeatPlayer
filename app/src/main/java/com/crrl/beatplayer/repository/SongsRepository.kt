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

import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Context
import android.content.OperationApplicationException
import android.database.Cursor
import android.os.RemoteException
import android.provider.MediaStore.AUTHORITY
import android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
import com.crrl.beatplayer.extensions.toList
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.utils.GeneralUtils.getSongUri
import com.crrl.beatplayer.utils.SettingsUtility


interface SongsRepository {
    fun loadSongs(): List<Song>
    fun getSongForId(id: Long): Song
    fun search(searchString: String, limit: Int = Int.MAX_VALUE): List<Song>
    fun deleteTracks(ids: LongArray): Int
}

class SongsRepositoryImplementation(context: Context) : SongsRepository {

    private val contentResolver = context.contentResolver
    private val settingsUtility = SettingsUtility(context)

    override fun loadSongs(): List<Song> {
        return makeSongCursor(null, null)
            .toList(true) {
                Song.createFromCursor(this)
            }
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

    override fun search(searchString: String, limit: Int): List<Song> {
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

    override fun deleteTracks(ids: LongArray): Int {
        if (ids.isEmpty()) {
            return -1
        }
        val operations = ids.map { song ->
            ContentProviderOperation
                .newDelete(getSongUri(song))
                .withSelection("_ID = ?", arrayOf("$song"))
                .build()

        }.toCollection(ArrayList())
        return try {
            contentResolver.applyBatch(AUTHORITY, operations)
            ids.size
        } catch (e: RemoteException) {
            -1
        } catch (e: OperationApplicationException) {
            -1
        }
    }

    @SuppressLint("Recycle")
    private fun makeSongCursor(
        selection: String?,
        paramArrayOfString: Array<String>?,
        sortOrder: String?
    ): Cursor {
        val selectionStatement = StringBuilder("is_music=1 AND title != ''")
        if (!selection.isNullOrEmpty()) {
            selectionStatement.append(" AND $selection")
        }
        val projection =
            arrayOf("_id", "title", "artist", "album", "duration", "track", "artist_id", "album_id")

        return contentResolver.query(
            EXTERNAL_CONTENT_URI,
            projection,
            selectionStatement.toString(),
            paramArrayOfString,
            sortOrder
        )
            ?: throw IllegalStateException("Unable to query $EXTERNAL_CONTENT_URI, system returned null.")
    }

    private fun makeSongCursor(selection: String?, paramArrayOfString: Array<String>?): Cursor? {
        val songSortOrder = settingsUtility.songSortOrder
        return makeSongCursor(selection, paramArrayOfString, songSortOrder)
    }
}
