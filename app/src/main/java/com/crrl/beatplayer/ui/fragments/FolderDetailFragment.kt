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

package com.crrl.beatplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentFolderDetailBinding
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.safeActivity
import com.crrl.beatplayer.extensions.toFolder
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.adapters.SongAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.FolderViewModel
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.PlayerConstants
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FolderDetailFragment : BaseFragment<Song>() {

    private lateinit var binding: FragmentFolderDetailBinding
    private lateinit var songAdapter: SongAdapter

    private val viewModel: FolderViewModel by viewModel { parametersOf(context) }
    private val songViewModel: SongViewModel by viewModel { parametersOf(context) }

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
        binding.folder = arguments!!.getString(PlayerConstants.FOLDER_KEY)!!.toFolder()

        songAdapter = SongAdapter(context).apply {
            showHeader = true
            isPlaylist = true
            itemClickListener = this@FolderDetailFragment
        }

        viewModel.getSongsByFolder(binding.folder!!.songIds).observe(this) {
            songAdapter.updateDataSet(it)
        }

        binding.apply {
            // Set up RecyclerView
            binding.songList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = songAdapter
            }
        }

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }
    }

    override fun addToList(playListId: Long, song: Song) {
        songViewModel.addToPlaylist(playListId, arrayOf(song.id).toLongArray())
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        (safeActivity as MainActivity).viewModel.update(item)
    }

    override fun onShuffleClick(view: View) {
        Toast.makeText(context, "Shuffle", Toast.LENGTH_LONG).show()
    }

    override fun onPlayAllClick(view: View) {
        Toast.makeText(context, "Play All", Toast.LENGTH_LONG).show()
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Song) {
        powerMenu!!.showAsAnchorRightTop(view)
        songViewModel.playLists().observe(this) {
            buildPlaylistMenu(it, item)
        }
    }
}
