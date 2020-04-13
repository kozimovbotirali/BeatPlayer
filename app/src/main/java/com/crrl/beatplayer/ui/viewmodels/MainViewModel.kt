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
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.ActivityMainBinding
import com.crrl.beatplayer.extensions.toast
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.playback.MusicService
import com.crrl.beatplayer.repository.*
import com.crrl.beatplayer.utils.GeneralUtils

class MainViewModel(private val context: Context) : ViewModel() {

    private val liveSongData = MutableLiveData<Song>()
    private val timeLiveData = MutableLiveData<Int>()
    private val rawLiveData = MutableLiveData<ByteArray>()
    private val currentSongList = MutableLiveData<LongArray>()
    private val liveColorAccent = MutableLiveData<Int>()
    private val songRepository = SongsRepository(context)
    val albumRepository = AlbumsRepository(context)
    val artistRepository = ArtistsRepository(context)
    val favoriteRepository = FavoritesRepository(context)
    val folderRepository = FoldersRepository(context)

    val musicService = MusicService()
    lateinit var binding: ActivityMainBinding

    fun getCurrentSong(): LiveData<Song> {
        return liveSongData
    }

    fun getCurrentSongList(): LiveData<LongArray> {
        return currentSongList
    }

    fun getTime(): LiveData<Int> {
        return timeLiveData
    }

    fun getRawData(): LiveData<ByteArray> {
        return rawLiveData
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
                    context.toast(
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
}