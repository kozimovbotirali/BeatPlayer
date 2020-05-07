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
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.PlaylistRepository
import com.crrl.beatplayer.ui.viewmodels.base.CoroutineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext

class PlaylistViewModel(
    private val repository: PlaylistRepository
): CoroutineViewModel(Main){

    private val playlistLiveData = MutableLiveData<List<Playlist>>()
    private val songsByPlaylist = MutableLiveData<List<Song>>()

    val count: Int
        get() = repository.getPlayListsCount()

    fun playLists(): LiveData<List<Playlist>> {
        launch {
            val playlists = withContext(Dispatchers.IO) {
                repository.getPlayLists()
            }
            playlistLiveData.postValue(playlists)
        }
        return playlistLiveData
    }

    fun getSongs(id: Long): LiveData<List<Song>> {
        launch {
            val songs = withContext(Dispatchers.IO) {
                repository.getSongsInPlaylist(id)
            }
            songsByPlaylist.postValue(songs)
        }
        return songsByPlaylist
    }

    fun remove(playListId: Long, id: Long) {
        repository.removeFromPlaylist(playListId, id)
    }

    fun exists(name: String): Boolean {
        return repository.existPlaylist(name)
    }

    fun create(name: String, songs: List<Song>): Long {
        return repository.createPlaylist(name, songs)
    }

    fun addToPlaylist(playListId: Long, song: List<Song>): Long {
        return repository.addToPlaylist(playListId, song)
    }

    fun getPlaylist(id: Long): Playlist {
        return repository.getPlaylist(id)
    }

    fun delete(id: Long): Int {
        return repository.deletePlaylist(id)
    }
}