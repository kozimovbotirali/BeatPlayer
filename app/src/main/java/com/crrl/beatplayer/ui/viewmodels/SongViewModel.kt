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
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.models.SearchData
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.*

class SongViewModel(private val context: Context) : ViewModel() {

    private val songs: MutableLiveData<List<Song>> = MutableLiveData()
    private val _songsByPlaylist: MutableLiveData<List<Song>> = MutableLiveData()
    private val _playlistLiveData: MutableLiveData<List<Playlist>> = MutableLiveData()
    private val _songsByAlbum: MutableLiveData<List<Song>> = MutableLiveData()
    private val _artistLiveData: MutableLiveData<List<Album>> = MutableLiveData()
    private val searchData = SearchData()
    private val _searchLiveData: MutableLiveData<SearchData> = MutableLiveData()
    private val songsSelected: MutableLiveData<MutableList<Song>> = MutableLiveData()
    private val isFavLiveData: MutableLiveData<Boolean> = MutableLiveData()

    val searchLiveData = _searchLiveData

    fun search(searchString: String) {
        if (searchString.isNotEmpty()) {
            Thread {
                searchData.apply {
                    val songs =
                        SongsRepository(context).searchSongs(searchString, 10).toMutableList()
                    val albums = AlbumsRepository.getInstance(context)!!.search(searchString, 10)
                        .toMutableList()
                    val artists = ArtistsRepository.getInstance(context)!!.search(searchString, 10)
                        .toMutableList()
                    songList = songs
                    albumList = albums
                    artistList = artists
                }
                _searchLiveData.postValue(searchData)
            }.start()
        } else {
            _searchLiveData.postValue(searchData.flush())
        }
    }

    fun liveData(): LiveData<List<Song>> {
        update()
        return songs
    }

    fun selectedSongs(): LiveData<MutableList<Song>> {
        if (songsSelected.value == null) songsSelected.value = mutableListOf()
        return songsSelected
    }

    fun update() {
        Thread {
            songs.postValue(SongsRepository(context).loadSongs())
        }.start()
    }

    fun update(id: Long) {
        Thread {
            val list = AlbumsRepository.getInstance(context)!!.getSongsForAlbum(id)
            _songsByAlbum.postValue(list)
        }.start()
    }

    fun update(list: MutableList<Song>) {
        Thread {
            songsSelected.postValue(list)
        }.start()
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

    fun songsByPlayList(id: Long): LiveData<List<Song>> {
        Thread {
            _songsByPlaylist.postValue(
                PlaylistRepository(context).getSongsInPlaylist(
                    id
                )
            )
        }.start()
        return _songsByPlaylist
    }

    fun addToPlaylist(playlistId: Long, songs: List<Song>) {
        PlaylistRepository(context).addToPlaylist(playlistId, songs)
    }

    fun removeFromPlaylist(playlistId: Long, id: Long) {
        PlaylistRepository(context).removeFromPlaylist(playlistId, id)
    }

    fun playLists(): LiveData<List<Playlist>> {
        Thread {
            _playlistLiveData.postValue(PlaylistRepository(context).getPlayLists())
        }.start()
        return _playlistLiveData
    }

    fun getSongsByAlbum(id: Long): LiveData<List<Song>>? {
        update(id)
        return _songsByAlbum
    }

    fun getArtistAlbums(artistId: Long): LiveData<List<Album>> {
        Thread {
            _artistLiveData.postValue(
                ArtistsRepository.getInstance(context)!!.getAlbumsForArtist(artistId)
            )
        }.start()
        return _artistLiveData
    }

    fun isFav(id: Long): LiveData<Boolean> {
        updateIsFav(id)
        return isFavLiveData
    }
}