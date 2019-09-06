/*
 * Copyright 2019 Carlos René Ramos López. All rights reserved.
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
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.playback.MusicService
import com.crrl.beatplayer.ui.activities.MainActivity

class MainViewModel(val safeActivity: MainActivity?) : ViewModel() {

    private val liveSongData: MutableLiveData<Song> = MutableLiveData()
    private val currentSongList: MutableLiveData<List<Song>> = MutableLiveData()
    var musicService = MusicService()

    fun getCurrentSong(): LiveData<Song> {
        return liveSongData
    }

    fun getCurrentSongList(): LiveData<List<Song>> {
        return currentSongList
    }

    fun update(song: Song) {
        liveSongData.value = song
    }

    fun update(newList: List<Song>) {
        currentSongList.value = newList
    }

    // Update the current song for the next one
    fun next(currentSong: Song) {
        if(!currentSongList.value.isNullOrEmpty()) update(musicService.next(currentSong))
    }

    // Update the current song for the previous one
    fun previous(currentSong: Song) {
        if(!currentSongList.value.isNullOrEmpty()) update(musicService.previous(currentSong))
    }
}