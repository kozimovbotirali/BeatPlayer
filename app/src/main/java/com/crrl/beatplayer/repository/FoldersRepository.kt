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

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.text.TextUtils
import com.crrl.beatplayer.extensions.toList
import com.crrl.beatplayer.models.Folder
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.utils.SettingsUtility
import java.util.*

interface FoldersRepository {
    fun getFolder(path: String): Folder
    fun getFolders(): List<Folder>
    fun getSongsForIds(path: String): List<Song>
}

class FoldersRepositoryImplementation(context: Context) : FoldersRepository {

    private val contentResolver = context.contentResolver
    private val settingsUtility = SettingsUtility(context)


    override fun getFolder(path: String): Folder {
        val songList = makeFolderCursor("rtrim(_data, replace(_data, '/', '')) = ?", arrayOf(path)).toList(true, Folder.Companion::createFromCursor)
        val folderList = mutableListOf<Folder>()
        songList.groupBy { it.fakePath }.map { pair ->
            folderList.add(pair.value.first().apply {
                songCount = pair.value.size
            })
        }
        return folderList.first()
    }

    override fun getFolders(): List<Folder> {
        val songList = makeFolderCursor(null, null).toList(true, Folder.Companion::createFromCursor)
        val folderList = mutableListOf<Folder>()
        songList.sortBy { it.name.toLowerCase(Locale.ROOT) }
        songList.groupBy { it.fakePath }.map { pair ->
            folderList.add(pair.value.first().apply {
                songCount = pair.value.size
            })
        }
        return folderList
    }

    override fun getSongsForIds(path: String): List<Song> {
        return makeFolderSongCursor("rtrim(_data, replace(_data, '/', '')) = ?", arrayOf(path))
            .toList(true) { Song.createFromCursor(this) }
    }

    private fun makeFolderCursor(
        selection: String?,
        paramArrayOfString: Array<String>?,
        sortOrder: String = ""
    ): Cursor? {
        val selectionStatement = StringBuilder("is_music=1 AND title != ''")

        if (!TextUtils.isEmpty(selection)) {
            selectionStatement.append(" AND $selection")
        }
        return contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                "_id",
                "album_id",
                "_data"
            ),
            selectionStatement.toString(),
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