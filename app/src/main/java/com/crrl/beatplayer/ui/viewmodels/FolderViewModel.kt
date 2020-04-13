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

package com.crrl.beatplayer.ui.viewmodels

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.models.Folder
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.FavoritesRepository
import com.crrl.beatplayer.repository.FoldersRepository

class FolderViewModel(private val context: Context?) : ViewModel() {

    private val albums: MutableLiveData<List<Folder>> = MutableLiveData()
    private val songByFolder: MutableLiveData<List<Song>> = MutableLiveData()
    private val isFavLiveData: MutableLiveData<Boolean> = MutableLiveData()

    init {
        Thread {
            albums.postValue(FoldersRepository(context).getFolders())
        }.start()
    }

    fun getFolders(): LiveData<List<Folder>> {
        return albums
    }

    fun getSongsByFolder(ids: List<Long>): LiveData<List<Song>> {
        Thread {
            songByFolder.postValue(FoldersRepository(context).getSongsForIds(ids.toLongArray()))
        }.start()
        return songByFolder
    }

    private fun updateIsFav(id: Long) {
        Thread {
            isFavLiveData.postValue(
                try {
                    FavoritesRepository(context).favExist(id)
                } catch (ex: SQLiteException) {
                    null
                }
            )
        }.start()
    }

    fun isFav(id: Long): LiveData<Boolean> {
        updateIsFav(id)
        return isFavLiveData
    }
}
