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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.crrl.beatplayer.models.Folder
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.FoldersRepository
import com.crrl.beatplayer.ui.viewmodels.base.CoroutineViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class FolderViewModel(
    private val repository: FoldersRepository
) : CoroutineViewModel(Main) {

    private val foldersData: MutableLiveData<List<Folder>> = MutableLiveData()
    private val songByFolder: MutableLiveData<List<Song>> = MutableLiveData()


    fun getFolders(): LiveData<List<Folder>> {
        launch {
            val folders = withContext(IO) {
                repository.getFolders()
            }
            foldersData.postValue(folders)
        }
        return foldersData
    }

    fun getSongsByFolder(ids: LongArray): LiveData<List<Song>> {
        launch {
            while (true) {
                val songs = withContext(IO) {
                    repository.getSongs(ids)
                }
                songByFolder.postValue(songs)
                delay(1000)
            }
        }
        return songByFolder
    }

    fun getFolder(id: Long): Folder {
        return repository.getFolder(id)
    }
}
