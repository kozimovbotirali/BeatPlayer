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

package com.crrl.beatplayer.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import android.support.v4.media.session.PlaybackStateCompat.*
import android.view.View
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.PlaybackState
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.activities.base.BaseActivity
import com.crrl.beatplayer.ui.fragments.*
import com.crrl.beatplayer.ui.viewmodels.*
import com.crrl.beatplayer.utils.BeatConstants
import com.crrl.beatplayer.utils.BeatConstants.BIND_STATE_BOUND
import com.crrl.beatplayer.utils.BeatConstants.FAVORITE_ID
import com.crrl.beatplayer.utils.BeatConstants.NOW_PLAYING
import com.crrl.beatplayer.utils.BeatConstants.PLAY_LIST_DETAIL
import com.crrl.beatplayer.utils.SettingsUtility
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.gson.Gson
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : BaseActivity() {

    private val playListViewModel by inject<PlaylistViewModel>()
    private val favoriteViewModel by inject<FavoriteViewModel>()
    private val songViewModel by inject<SongViewModel>()
    private val songDetailViewModel by inject<SongDetailViewModel>()
    private val settingsUtility by inject<SettingsUtility>()

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)
    }

    override fun onStop() {
        super.onStop()
        settingsUtility.didStop = true
    }

    private fun init(savedInstanceState: Bundle?) {
        viewModel.binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        if (savedInstanceState == null) {
            replaceFragment(
                R.id.nav_host_fragment,
                LibraryFragment(),
                BeatConstants.LIBRARY
            )
        }
        if (!isPermissionsGranted()) return

        songDetailViewModel.time.observe(this) {
            val total = songDetailViewModel.currentData.value?.duration ?: 0
            viewModel.binding.progressCircular.apply {
                val t = progress.percentToMs(total).fixToStep(1000)
                if (t != it) {
                    progress = it.fixToPercent(total).fixPercentBounds()
                }
            }
        }

        songDetailViewModel.currentState.observe(this) {
            songDetailViewModel.update(it.position)
            if (it.state == PlaybackStateCompat.STATE_PLAYING) {
                songDetailViewModel.update(BIND_STATE_BOUND)
            } else songDetailViewModel.update()
        }

        viewModel.binding.let {
            it.viewModel = songDetailViewModel
            it.executePendingBindings()

            it.lifecycleOwner = this
        }

        viewModel.binding.title.isSelected = true

        songDetailViewModel.currentData
            .observe(this) {
                val fragment = supportFragmentManager.findFragmentByTag(NOW_PLAYING)
                if (it.id !in arrayOf(0L, -1L)) {
                    if (fragment == null) {
                        viewModel.showMiniPlayer()
                    } else viewModel.hideMiniPlayer()
                } else viewModel.hideMiniPlayer()
            }
    }

    fun onSongLyricClick(v: View) {
        addFragment(
            R.id.nav_host_fragment,
            LyricFragment(),
            BeatConstants.LYRIC,
            true
        )
    }

    fun onSongInfoClick(v: View) {
        addFragment(
            R.id.nav_host_fragment,
            SongDetailFragment(),
            NOW_PLAYING,
            true
        )
    }

    override fun onBackPressed() {
        var isDismiss = true
        supportFragmentManager.fragments.forEach {
            isDismiss = when (it) {
                is AlbumDetailFragment -> it.onBackPressed()
                is ArtistDetailFragment -> it.onBackPressed()
                is PlaylistDetailFragment -> it.onBackPressed()
                is FolderDetailFragment -> it.onBackPressed()
                else -> true
            }
        }
        if (isDismiss) super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data ?: return
        data.extras ?: return
        val name = data.extras!!.getString(PLAY_LIST_DETAIL)
        val songs = Gson().fromJson<List<Song>>(data.extras?.getString("SONGS")!!)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_CANCELED) {
                createPlayList(name, songs)
            } else {
                createPlayList(name, songs, true)
            }
        }
    }

    private fun createPlayList(
        name: String?,
        selectedSong: List<Song>,
        showOnEnd: Boolean = false
    ) {
        val id = playListViewModel.create(name!!, selectedSong)
        if (id > 0) {
            if (!showOnEnd) {
                viewModel.binding.mainContainer.snackbar(
                    SUCCESS,
                    getString(R.string.playlist_added_success),
                    LENGTH_SHORT
                )
                return
            }
            addFragment(
                R.id.nav_host_fragment,
                PlaylistDetailFragment(),
                PLAY_LIST_DETAIL,
                extras = bundleOf(PLAY_LIST_DETAIL to id)
            )
        } else {
            viewModel.binding.mainContainer.snackbar(
                ERROR,
                getString(R.string.playlist_added_error),
                LENGTH_SHORT
            )
        }
    }

    fun playPauseClick(view: View) {
        val mediaItemData = songDetailViewModel.currentState.value ?: PlaybackState()
        when (mediaItemData.state) {
            STATE_PLAYING -> viewModel.transportControls()?.pause()
            else -> viewModel.transportControls()?.play()
        }
    }

    fun shuffleModeClick(view: View) {
        val mediaItemData = songDetailViewModel.currentState.value ?: PlaybackState()
        when (mediaItemData.shuffleMode) {
            SHUFFLE_MODE_ALL -> viewModel.transportControls()?.setShuffleMode(SHUFFLE_MODE_NONE)
            else -> viewModel.transportControls()?.setShuffleMode(SHUFFLE_MODE_ALL)
        }
    }

    fun repeatModeClick(view: View) {
        val mediaItemData = songDetailViewModel.currentState.value ?: PlaybackState()
        when (mediaItemData.repeatMode) {
            REPEAT_MODE_ONE -> viewModel.transportControls()?.setRepeatMode(REPEAT_MODE_ALL)
            REPEAT_MODE_ALL -> viewModel.transportControls()?.setRepeatMode(REPEAT_MODE_NONE)
            else -> viewModel.transportControls()?.setRepeatMode(REPEAT_MODE_ONE)
        }
    }

    fun toggleAddToFav(v: View) {
        val mediaItemData = songDetailViewModel.currentData.value ?: return
        val song = songViewModel.getSongById(mediaItemData.id)
        if (!favoriteViewModel.favExist(FAVORITE_ID)) return
        if (favoriteViewModel.songExist(song.id)) {
            val resp = favoriteViewModel.deleteSongByFavorite(FAVORITE_ID, longArrayOf(song.id))
            showSnackBar(v, resp, 0)
        } else {
            val resp = favoriteViewModel.addToFavorite(FAVORITE_ID, listOf(song))
            showSnackBar(v, resp, 1)
        }
    }

    private fun showSnackBar(view: View, resp: Int, type: Int) {
        val ok = when (type) {
            0 -> getString(R.string.song_no_fav_ok)
            else -> getString(R.string.song_fav_ok)
        }
        val custom = when (type) {
            1 -> R.drawable.ic_success
            else -> R.drawable.ic_dislike
        }
        if (resp > 0) view.snackbar(CUSTOM, ok, LENGTH_SHORT, custom = custom)
    }

    fun isPermissionsGranted() = permissionsGranted
}
