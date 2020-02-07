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
import android.provider.MediaStore
import android.text.TextUtils
import com.crrl.beatplayer.extensions.toList
import com.crrl.beatplayer.models.Folder
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.utils.SettingsUtility
import java.util.*

interface FoldersRepositoryInterface {
    fun getFolders(): List<Folder>
    fun getSongsForIds(idList: LongArray): List<Song>
}

class FoldersRepository() : FoldersRepositoryInterface {

    private lateinit var contentResolver: ContentResolver
    private lateinit var settingsUtility: SettingsUtility

    constructor(context: Context?) : this() {
        contentResolver = context!!.contentResolver
        settingsUtility = SettingsUtility.getInstance(context)
    }

    override fun getFolders(): List<Folder> {
        val songList = makeFolderCursor(null, null).toList(true, Folder.Companion::createFromCursor)
        val folderList = mutableListOf<Folder>()
        songList.sortBy { it.name.toLowerCase(Locale.ROOT) }
        for ((i, song) in songList.withIndex()) {
            if (i == 0) {
                folderList.add(song)
                folderList[folderList.size - 1].songIds.add(song.id)
            } else {
                if (song.path != songList[i - 1].path) {
                    folderList.add(song)
                    folderList[folderList.size - 1].songIds.add(song.id)
                } else {
                    folderList[folderList.size - 1].songIds.add(song.id)
                }
            }
        }
        return folderList
    }

    override fun getSongsForIds(idList: LongArray): List<Song> {
        var selection = "_id IN ("
        for (id in idList) {
            selection += "$id,"
        }
        if (idList.isNotEmpty()) {
            selection = selection.substring(0, selection.length - 1)
        }
        selection += ")"

        return makeFolderSongCursor(selection, null)
            .toList(true) { Song.createFromCursor(this) }
            .sortedBy { it.title.toLowerCase(Locale.ROOT) }
    }

    private fun makeFolderCursor(
        selection: String?,
        paramArrayOfString: Array<String>?,
        sortOrder: String = ""
    ): Cursor? {
        var selectionStatement = "title != ''"

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = "$selectionStatement AND $selection"
        }
        return contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                "_id",
                "album_id",
                "_data"
            ),
            selectionStatement,
            paramArrayOfString,
            sortOrder
        )
    }

    private fun makeFolderSongCursor(
        selection: String?,
        paramArrayOfString: Array<String>?,
        sortOrder: String = ""
    ): Cursor? {
        var selectionStatement = "title != ''"

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement = "$selectionStatement AND $selection"
        }
        return contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                "_id",
                "title",
                "artist",
                "album",
                "duration",
                "track",
                "artist_id",
                "album_id",
                "_data"
            ),
            selectionStatement,
            paramArrayOfString,
            sortOrder
        )
    }
}