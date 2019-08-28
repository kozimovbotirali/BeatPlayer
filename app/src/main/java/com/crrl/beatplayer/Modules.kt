package com.crrl.beatplayer

import android.content.Context
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.viewmodels.*
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val mainModule = module {
    viewModel { (context: Context) ->
        SongViewModel(context)
    }

    viewModel { (context: Context) ->
        AlbumViewModel(context)
    }

    viewModel { (context: Context) ->
        FolderViewModel(context)
    }

    viewModel { (safeActivity: MainActivity) ->
        MainViewModel(safeActivity)
    }

    viewModel { (safeActivity: MainActivity) ->
        SongDetailViewModel(safeActivity)
    }
}