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

package com.crrl.beatplayer.models

import android.database.Cursor
import com.crrl.beatplayer.repository.PlaylistRepositoryImplementation.Companion.COLUMN_ID
import com.crrl.beatplayer.repository.PlaylistRepositoryImplementation.Companion.COLUMN_NAME
import com.crrl.beatplayer.repository.PlaylistRepositoryImplementation.Companion.COLUMN_SONG_COUNT
import com.google.gson.Gson

data class Playlist(
    val id: Long = -1,
    val name: String = "",
    val songCount: Int = -1
) : MediaItem(id) {
    companion object {
        fun fromCursor(cursor: Cursor): Playlist {
            return Playlist(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getInt(2)
            )
        }
    }

    override fun compare(other: MediaItem): Boolean {
        other as Playlist
        return id == other.id && name == other.name && songCount == other.songCount
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }

    fun columns(): Array<String> {
        return arrayOf(COLUMN_ID, COLUMN_NAME, COLUMN_SONG_COUNT)
    }

    fun values(): Array<String> {
        return arrayOf("$id", name, "$songCount")
    }
}