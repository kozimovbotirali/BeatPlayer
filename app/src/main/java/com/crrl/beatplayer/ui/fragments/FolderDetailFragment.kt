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
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentFolderDetailBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.MediaItemData
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.adapters.SongAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.FavoriteViewModel
import com.crrl.beatplayer.ui.viewmodels.FolderViewModel
import com.crrl.beatplayer.ui.viewmodels.PlaylistViewModel
import com.crrl.beatplayer.utils.BeatConstants
import com.crrl.beatplayer.utils.BeatConstants.FAVORITE_NAME
import com.crrl.beatplayer.utils.BeatConstants.FOLDER_KEY
import kotlinx.android.synthetic.main.layout_recyclerview.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FolderDetailFragment : BaseFragment<Song>() {

    private lateinit var binding: FragmentFolderDetailBinding
    private lateinit var songAdapter: SongAdapter

    private val folderViewModel by viewModel<FolderViewModel>()
    private val favoriteViewModel by inject<FavoriteViewModel>()
    private val playlistViewModel by inject<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_folder_detail, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    fun init() {
        val id = arguments?.getString(FOLDER_KEY)!!
        binding.isLoading = true
        binding.name = arguments?.getString(FAVORITE_NAME)

        songAdapter = SongAdapter(context, songDetailViewModel).apply {
            showHeader = true
            isAlbumDetail = true
            itemClickListener = this@FolderDetailFragment
        }
        postponeEnterTransition()
        folderViewModel.getFolder(id).observe(this){ folder ->
            initNeeded(Song(), emptyList(), folder.id)
            binding.folder = folder
            folderViewModel.getSongsByFolder(folder.realPath).observe(this) {
                if (!songAdapter.songList.deepEquals(it)) {
                    mainViewModel.reloadQueueIds(it.toIDList(), binding.folder!!.name)
                    songAdapter.updateDataSet(it)
                }
                binding.isLoading = false
                if (it.isEmpty()) {
                    favoriteViewModel.deleteFavorites(longArrayOf(folder.id))
                    activity?.onBackPressed()
                }
                (view?.parent as? ViewGroup)?.doOnPreDraw {
                    startPostponedEnterTransition()
                }
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

        binding.apply {
            list.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = songAdapter
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            addFavorites.setOnClickListener { toggleAddFav() }
        }

        binding.let {
            it.mainViewModel = mainViewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), binding.folder!!.name)
        mainViewModel.mediaItemClicked(item.toMediaItem(), extras)
    }

    override fun onShuffleClick(view: View) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), binding.folder!!.name)
        mainViewModel.transportControls()?.sendCustomAction(BeatConstants.PLAY_ALL_SHUFFLED, extras)
    }

    override fun onPlayAllClick(view: View) {
        val extras = getExtraBundle(songAdapter.songList.toIDList(), binding.folder!!.name)
        mainViewModel.mediaItemClicked(songAdapter.songList.first().toMediaItem(), extras)
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Song, itemList: List<Song>) {
        super.onPopupMenuClick(view, position, item, itemList)
        powerMenu!!.showAsAnchorRightTop(view)
        playlistViewModel.playLists().observe(this) {
            buildPlaylistMenu(it, item)
        }
    }

    private fun toggleAddFav() {
        if (favoriteViewModel.favExist(binding.folder!!.id)) {
            val resp = favoriteViewModel.deleteFavorites(longArrayOf(binding.folder!!.id))
            showSnackBar(view, resp, 0, R.string.folder_no_fav_ok)
        } else {
            val resp = favoriteViewModel.create(binding.folder!!.toFavorite())
            showSnackBar(view, resp, 1, R.string.folder_fav_ok)
        }
    }
}
