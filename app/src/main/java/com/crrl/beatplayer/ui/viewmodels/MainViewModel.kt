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

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.databinding.ActivityMainBinding
import com.crrl.beatplayer.extensions.toast
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.playback.MusicService
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.utils.GeneralUtils

class MainViewModel(val safeActivity: MainActivity?) : ViewModel() {

    private val liveSongData: MutableLiveData<Song> = MutableLiveData()
    private val timeLiveData: MutableLiveData<Int> = MutableLiveData()
    private val rawLiveData: MutableLiveData<ByteArray> = MutableLiveData()
    private val currentSongList: MutableLiveData<List<Song>> = MutableLiveData()
    var musicService = MusicService()
    lateinit var binding: ActivityMainBinding

    fun getCurrentSong(): LiveData<Song> {
        return liveSongData
    }

    fun getCurrentSongList(): LiveData<List<Song>> {
        return currentSongList
    }

    fun getTime(): LiveData<Int>{
        return timeLiveData
    }

    fun getRawData(): LiveData<ByteArray>{
        return rawLiveData
    }

    fun update(newTime: Int){
        timeLiveData.postValue(if(newTime == -1) newTime else newTime / 1000 * 1000)
    }

    fun update(song: Song) {
        if(liveSongData.value != song) {
            liveSongData.value = song
            Thread {
                update(GeneralUtils.audio2Raw(song.path))
            }.start()
            update(-1)
        }
    }

    fun update(newList: List<Song>) {
        currentSongList.value = newList
    }

    fun update(raw: ByteArray?) {
        if (raw == null) {
            if(getCurrentSong().value?.id != -1L) {
                safeActivity?.runOnUiThread {
                    safeActivity.toast("File Not Found", Toast.LENGTH_SHORT)
                }
                next(getCurrentSong().value!!)
            }
            return
        }else{
            rawLiveData.postValue(raw)
        }
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