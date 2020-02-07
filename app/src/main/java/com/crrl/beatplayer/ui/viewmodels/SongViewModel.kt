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

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.models.SearchData
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.AlbumsRepository
import com.crrl.beatplayer.repository.ArtistsRepository
import com.crrl.beatplayer.repository.PlaylistRepository
import com.crrl.beatplayer.repository.SongsRepository
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class SongViewModel(private val context: Context?) : ViewModel() {

    private val songs: MutableLiveData<List<Song>> = MutableLiveData()
    private val _songsByPlaylist: MutableLiveData<List<Song>> = MutableLiveData()
    private val _playlistLiveData: MutableLiveData<List<Playlist>> = MutableLiveData()
    private val _songsByAlbum: MutableLiveData<List<Song>> = MutableLiveData()
    private val _artistLiveData: MutableLiveData<List<Album>> = MutableLiveData()
    private val searchData = SearchData()
    private val _searchLiveData: MutableLiveData<SearchData> = MutableLiveData()

    val searchLiveData = _searchLiveData

    fun search(searchString: String) {
        if (searchString.length >= 0) {
            Thread {
                searchData.apply {
                    val songs =
                        SongsRepository(context).searchSongs(searchString, 10).toMutableList()
                    val albums = AlbumsRepository.getInstance(context)!!.search(searchString, 10)
                        .toMutableList()
                    val artists = ArtistsRepository.getInstance(context)!!.search(searchString, 10)
                        .toMutableList()
                    if (songs.isNotEmpty())
                        songList = songs
                    if (albums.isNotEmpty())
                        albumList = albums
                    if (artists.isNotEmpty())
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

    fun update() {
        Thread {
            songs.postValue(SongsRepository(context).loadSongs())
        }.start()
    }

    fun songsByPlayList(id: Long): LiveData<List<Song>> {
        Thread {
            _songsByPlaylist.postValue(
                PlaylistRepository.getInstance(context)!!.getSongsInPlaylist(
                    id
                )
            )
        }.start()
        return _songsByPlaylist
    }

    fun addToPlaylist(playlistId: Long, ids: LongArray) {
        PlaylistRepository.getInstance(context)!!.addToPlaylist(playlistId, ids)
    }

    fun removeFromPlaylist(playlistId: Long, id: Long) {
        PlaylistRepository.getInstance(context)!!.removeFromPlaylist(playlistId, id)
    }

    fun playLists(): LiveData<List<Playlist>> {
        Observable.fromCallable { PlaylistRepository.getInstance(context)!!.getPlayLists() }
            .observeOn(Schedulers.newThread()).subscribeOn(Schedulers.newThread())
            .doOnError { Log.println(Log.ERROR, "Error", it.message!!) }
            .subscribe { _playlistLiveData.postValue(it) }
        return _playlistLiveData
    }

    fun getSongsByAlbum(id: Long): LiveData<List<Song>>? {
        Observable.fromCallable { AlbumsRepository.getInstance(context)!!.getSongsForAlbum(id) }
            .subscribeOn(Schedulers.newThread()).subscribe { _songsByAlbum.postValue(it) }
        return _songsByAlbum
    }

    fun getArtistAlbums(artistId: Long): LiveData<List<Album>> {
        Observable.fromCallable {
            ArtistsRepository.getInstance(context)!!.getAlbumsForArtist(artistId)
        }.observeOn(Schedulers.newThread()).subscribeOn(Schedulers.newThread())
            .subscribe { _artistLiveData.postValue(it) }
        return _artistLiveData
    }

}