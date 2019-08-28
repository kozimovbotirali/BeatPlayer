package com.crrl.beatplayer.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.activities.MainActivity

class MainViewModel(val safeActivity: MainActivity?) : ViewModel() {
    private val liveSongData: MutableLiveData<Song> = MutableLiveData()

    fun getCurrentSong(): LiveData<Song> {
        return liveSongData
    }

    fun update(song: Song) {
        liveSongData.value = song
    }
}