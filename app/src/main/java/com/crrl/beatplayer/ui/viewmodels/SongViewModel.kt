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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.SongsRepository

class SongViewModel(private val context: Context) : ViewModel() {

    private val songs: MutableLiveData<List<Song>> = MutableLiveData()
    private val songsSelected: MutableLiveData<MutableList<Song>> = MutableLiveData()

    fun getSongList(): LiveData<List<Song>> {
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

    fun update(list: MutableList<Song>) {
        Thread {
            songsSelected.postValue(list)
        }.start()
    }
}