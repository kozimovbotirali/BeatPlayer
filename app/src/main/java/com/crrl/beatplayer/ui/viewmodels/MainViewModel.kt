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

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.ActivityMainBinding
import com.crrl.beatplayer.extensions.ERROR
import com.crrl.beatplayer.extensions.snackbar
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.models.SearchData
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.playback.MusicService
import com.crrl.beatplayer.repository.*
import com.crrl.beatplayer.utils.GeneralUtils

class MainViewModel(private val context: Context) : ViewModel() {

    private val liveSongData = MutableLiveData<Song>()
    private val timeLiveData = MutableLiveData<Int>()
    private val rawLiveData = MutableLiveData<ByteArray>()
    private val currentSongList = MutableLiveData<LongArray>()
    private val playlistLiveData = MutableLiveData<List<Playlist>>()
    private val songsByPlaylist = MutableLiveData<List<Song>>()
    private val songsByAlbum = MutableLiveData<List<Song>>()
    private val artistLiveData = MutableLiveData<List<Album>>()
    private val searchLiveData = MutableLiveData<SearchData>()
    private val isFavLiveData = MutableLiveData<Boolean>()
    private val liveColorAccent = MutableLiveData<Int>()
    private val searchData = SearchData()
    private val songRepository = SongsRepository(context)
    val albumRepository = AlbumsRepository(context)
    val artistRepository = ArtistsRepository(context)
    val favoriteRepository = FavoritesRepository(context)
    val playlistRepository = PlaylistRepository(context)
    val folderRepository = FoldersRepository(context)

    val musicService = MusicService()
    lateinit var binding: ActivityMainBinding

    fun playLists(): LiveData<List<Playlist>> {
        Thread {
            playlistLiveData.postValue(playlistRepository.getPlayLists())
        }.start()
        return playlistLiveData
    }

    fun songsByPlayList(id: Long): LiveData<List<Song>> {
        Thread {
            songsByPlaylist.postValue(
                playlistRepository.getSongsInPlaylist(id)
            )
        }.start()
        return songsByPlaylist
    }

    fun getSongsByAlbum(id: Long): LiveData<List<Song>>? {
        Thread {
            val list = AlbumsRepository.getInstance(context)!!.getSongsForAlbum(id)
            songsByAlbum.postValue(list)
        }.start()
        return songsByAlbum
    }

    fun getArtistAlbums(artistId: Long): LiveData<List<Album>> {
        Thread {
            artistLiveData.postValue(
                ArtistsRepository.getInstance(context)!!.getAlbumsForArtist(artistId)
            )
        }.start()
        return artistLiveData
    }

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
                searchLiveData.postValue(searchData)
            }.start()
        } else {
            searchLiveData.postValue(searchData.flush())
        }
    }

    fun isFav(id: Long): LiveData<Boolean> {
        Thread {
            isFavLiveData.postValue(
                try {
                    FavoritesRepository(context).favExist(id)
                } catch (ex: SQLiteException) {
                    null
                }
            )
        }.start()
        return isFavLiveData
    }

    fun next(currentSong: Long) {
        currentSongList.value ?: return
        val song = songRepository.getSongForId(musicService.next(currentSong))
        update(song)
    }

    fun previous(currentSong: Long) {
        currentSongList.value ?: return
        val song = songRepository.getSongForId(musicService.previous(currentSong))
        update(song)
    }

    fun random(currentSong: Long): Song {
        currentSongList.value ?: return Song()
        return songRepository.getSongForId(musicService.random(currentSong))
    }

    fun update(newTime: Int) {
        timeLiveData.postValue(if (newTime == -1) newTime else newTime / 1000 * 1000)
    }

    fun update(song: Song) {
        if (liveSongData.value?.id != song.id) {
            liveSongData.value = song
            Thread {
                update(GeneralUtils.audio2Raw(context, Uri.parse(song.path)))
            }.start()
            update(-1)
        }
    }

    fun update(newList: LongArray) {
        currentSongList.value = newList
    }

    fun update(raw: ByteArray?) {
        if (raw == null) {
            if (getCurrentSong().value?.id != -1L) {
                (context as Activity).runOnUiThread {
                    binding.mainContainer.snackbar(
                        ERROR,
                        context.getString(R.string.unavailable),
                        Toast.LENGTH_SHORT
                    )
                }
            }
            return
        } else {
            rawLiveData.postValue(raw)
        }
    }

    fun getCurrentSong(): LiveData<Song> = liveSongData
    fun getCurrentSongList(): LiveData<LongArray> = currentSongList
    fun getTime(): LiveData<Int> = timeLiveData
    fun getRawData(): LiveData<ByteArray> = rawLiveData
    fun searchLiveData() = searchLiveData
}

