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

package com.crrl.beatplayer.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentPlaylistDetailBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.MediaItemData
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.adapters.SongAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.PlaylistViewModel
import com.crrl.beatplayer.utils.BeatConstants
import com.crrl.beatplayer.utils.BeatConstants.PLAY_ALL_SHUFFLED
import com.crrl.beatplayer.utils.BeatConstants.PLAY_LIST_DETAIL
import kotlinx.android.synthetic.main.layout_recyclerview.*
import org.koin.android.ext.android.inject


class PlaylistDetailFragment : BaseFragment<Song>() {

    lateinit var binding: FragmentPlaylistDetailBinding
    private lateinit var songAdapter: SongAdapter

    private val playlistViewModel by inject<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_playlist_detail, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val id = arguments!!.getLong(PLAY_LIST_DETAIL)

        binding.playlist = playlistViewModel.getPlaylist(id)

        songAdapter = SongAdapter(activity, songDetailViewModel).apply {
            showHeader = true
            isAlbumDetail = true
            itemClickListener = this@PlaylistDetailFragment
        }

        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = songAdapter
            clipToOutline = true
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }

        playlistViewModel.getSongs(binding.playlist!!.id).observe(this) {
            if (!songAdapter.songList.deepEquals(it)) {
                songAdapter.updateDataSet(it)
                mainViewModel.reloadQueueIds(it.toIDList(), binding.playlist!!.name)
            }
        }

        songDetailViewModel.lastData.observe(this) { mediaItemData ->
            val position = songAdapter.songList.indexOfFirst { it.id == mediaItemData.id } + 1
            if(settingsUtility.didStop){
                songAdapter.notifyDataSetChanged()
                settingsUtility.didStop = false
            } else songAdapter.notifyItemChanged(position)
        }

        songDetailViewModel.currentState.observe(this) {
            val mediaItemData = songDetailViewModel.currentData.value ?: MediaItemData()
            val position = songAdapter.songList.indexOfFirst { it.id == mediaItemData.id } + 1
            songAdapter.notifyItemChanged(position)
        }

        songDetailViewModel.currentData.observe(this) { mediaItemData ->
            val position = songAdapter.songList.indexOfFirst { it.id == mediaItemData.id } + 1
            songAdapter.notifyItemChanged(position)
        }

        binding.let {
            it.viewModel = playlistViewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    override fun removeFromList(playListId: Long, item: Song?) {
        playlistViewModel.remove(playListId, item!!.id)
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), binding.playlist!!.name)
        mainViewModel.mediaItemClicked(item.toMediaItem(), extras)
    }

    override fun onShuffleClick(view: View) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), binding.playlist!!.name)
        mainViewModel.transportControls()?.sendCustomAction(PLAY_ALL_SHUFFLED, extras)
    }

    override fun onPlayAllClick(view: View) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), binding.playlist!!.name)
        mainViewModel.mediaItemClicked(songAdapter.songList.first().toMediaItem(), extras)
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Song, itemList: List<Song>) {
        super.onPopupMenuClick(view, position, item, itemList)
        powerMenu!!.showAsAnchorRightTop(view)
        playlistViewModel.playLists().observe(this) {
            buildPlaylistMenu(it, item)
        }
    }
}
