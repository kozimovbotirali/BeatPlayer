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
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentFolderDetailBinding
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.toIDList
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.FavoritesRepository
import com.crrl.beatplayer.repository.FoldersRepository
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.adapters.SongAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.FolderViewModel
import com.crrl.beatplayer.utils.PlayerConstants
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FolderDetailFragment : BaseFragment<Song>() {

    private lateinit var binding: FragmentFolderDetailBinding
    private lateinit var songAdapter: SongAdapter

    private val viewModel: FolderViewModel by viewModel { parametersOf(context) }

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
        val id = arguments!!.getLong(PlayerConstants.FOLDER_KEY)
        binding.folder = FoldersRepository(context).getFolder(id)

        songAdapter = SongAdapter(context, (activity as MainActivity).viewModel).apply {
            showHeader = true
            isPlaylist = true
            itemClickListener = this@FolderDetailFragment
        }
        viewModel.getSongsByFolder(binding.folder!!.songIds).observe(this) {
            songAdapter.updateDataSet(it)
            if (it.isEmpty()) {
                mainViewModel.favoriteRepository.deleteFavorites(longArrayOf(id))
                activity?.onBackPressed()
            }
        }

        binding.apply {
            songList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = songAdapter
            }
            addFavorites.setOnClickListener { toggleAddFav() }
        }

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        mainViewModel.update(item)
        mainViewModel.update(songAdapter.songList.toIDList())
    }

    override fun onShuffleClick(view: View) {
        mainViewModel.update(songAdapter.songList.toIDList())
        mainViewModel.update(mainViewModel.random(-1))
    }

    override fun onPlayAllClick(view: View) {
        mainViewModel.update(songAdapter.songList.first())
        mainViewModel.update(songAdapter.songList.toIDList())
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Song, itemList: List<Song>) {
        super.onPopupMenuClick(view, position, item, itemList)
        powerMenu!!.showAsAnchorRightTop(view)
        mainViewModel.playLists().observe(this) {
            buildPlaylistMenu(it, item)
        }
    }

    private fun toggleAddFav() {
        val favoritesRepository = FavoritesRepository(context)
        val libType = getString(R.string.folder)
        if (favoritesRepository.favExist(binding.folder!!.id)) {
            val resp = favoritesRepository.deleteFavorites(longArrayOf(binding.folder!!.id))
            showSnackBar(view, resp, 0, libType)
        } else {
            val resp = favoritesRepository.createFavorite(binding.folder!!.toFavorite())
            showSnackBar(view, resp, 1, libType)
        }
    }
}
