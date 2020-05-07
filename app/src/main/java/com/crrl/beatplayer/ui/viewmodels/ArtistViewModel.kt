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
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.repository.ArtistsRepository
import com.crrl.beatplayer.ui.viewmodels.base.CoroutineViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class ArtistViewModel(
    private val repository: ArtistsRepository
) : CoroutineViewModel(Main) {

    private val artistsData: MutableLiveData<List<Artist>> = MutableLiveData()
    private val albumLiveData = MutableLiveData<List<Album>>()

    fun update() {
        launch {
            val artists = withContext(IO){
                repository.getAllArtist()
            }
            artistsData.postValue(artists)
        }
    }

    fun getArtists(): LiveData<List<Artist>> {
        update()
        return artistsData
    }

    fun getArtistAlbums(artistId: Long): LiveData<List<Album>> {
        launch {
            val albums = withContext(IO) {
                repository.getAlbumsForArtist(artistId)
            }
            albumLiveData.postValue(albums)
        }
        return albumLiveData
    }

    fun getArtist(id: Long): Artist {
        return repository.getArtist(id)
    }
}
