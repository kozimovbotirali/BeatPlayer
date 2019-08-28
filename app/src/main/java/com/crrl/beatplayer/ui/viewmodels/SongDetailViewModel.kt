package com.crrl.beatplayer.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.activities.MainActivity

class SongDetailViewModel(private val mainActivity: MainActivity) : ViewModel() {

    fun getCurrentData(): LiveData<Song> {
        return mainActivity.viewModel.getCurrentSong()
    }
}