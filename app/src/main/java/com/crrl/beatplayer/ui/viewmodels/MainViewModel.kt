package com.crrl.beatplayer.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.activities.MainActivity

class MainViewModel(val safeActivity: MainActivity?) : ViewModel() {

    private val liveSongData: MutableLiveData<Song> = MutableLiveData()
    private val currentSongList: MutableLiveData<List<Song>> = MutableLiveData()

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
}