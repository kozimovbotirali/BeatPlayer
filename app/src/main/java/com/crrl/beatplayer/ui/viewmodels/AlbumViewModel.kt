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
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.AlbumsRepository
import com.crrl.beatplayer.ui.viewmodels.base.CoroutineViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class AlbumViewModel(
    private val repository: AlbumsRepository
) : CoroutineViewModel(Main) {
    private val albumData = MutableLiveData<List<Album>>()
    private val songsByAlbum = MutableLiveData<List<Song>>()

    fun getAlbum(id: Long): Album {
        return repository.getAlbum(id)
    }

    fun getAlbums(): LiveData<List<Album>> {
        update()
        return albumData
    }

    fun getSongsByAlbum(id: Long): LiveData<List<Song>> {
        launch {
            val list = withContext(IO) {
                repository.getSongsForAlbum(id)
            }
            songsByAlbum.postValue(list)
        }
        return songsByAlbum
    }

    fun update() {
        launch {
            val albums = withContext(IO) {
                repository.getAlbums()
            }
            albumData.postValue(albums)
        }
    }
}