package com.crrl.beatplayer

import android.content.Context
import com.crrl.beatplayer.ui.viewmodels.AlbumViewModel
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val mainModule = module {
    viewModel { (context: Context) ->
        SongViewModel(context)
    }

    viewModel { (context: Context) ->
        AlbumViewModel(context)
    }
}