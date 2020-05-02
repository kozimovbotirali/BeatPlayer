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
import kotlinx.coroutines.withContext

class FolderViewModel(
    private val repositoryFol: FoldersRepository
) : CoroutineViewModel(Main) {

    private val currentFolder = MutableLiveData<Folder>()
    private val foldersData: MutableLiveData<List<Folder>> = MutableLiveData()
    private val songByFolder: MutableLiveData<List<Song>> = MutableLiveData()


    fun getFolders(): LiveData<List<Folder>> {
        launch {
            val folders = withContext(IO) {
                repositoryFol.getFolders()
            }
            foldersData.postValue(folders)
        }
        return foldersData
    }

    fun getSongsByFolder(path: String): LiveData<List<Song>> {
        launch {
            val songs = withContext(IO) {
                repositoryFol.getSongsForIds(path)
            }
            songByFolder.postValue(songs)
        }
        return songByFolder
    }

    fun getFolder(path: String): LiveData<Folder> {
        launch {
            val folder = withContext(IO) {
                repositoryFol.getFolder(path)
            }
            currentFolder.postValue(folder)
        }
        return currentFolder
    }
}
