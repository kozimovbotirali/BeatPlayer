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
import android.widget.Toast.LENGTH_SHORT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentPlaylistBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.repository.PlaylistRepository
import com.crrl.beatplayer.ui.adapters.PlaylistAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.utils.PlayerConstants.PLAY_LIST_DETAIL


class PlaylistFragment : BaseFragment<Playlist>() {

    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var binding: FragmentPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_playlist, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {

        playlistAdapter = PlaylistAdapter().apply {
            itemClickListener = this@PlaylistFragment
        }

        binding.playList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = playlistAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0 || dy < 0 && binding.createPlayList.isShown)
                        binding.createPlayList.hide()
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE)
                        binding.createPlayList.show()
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
        }

        binding.createPlayList.setOnClickListener { createDialog() }

        reloadAdapter()

        binding.let {
            it.viewModel = mainViewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    override fun onItemClick(view: View, position: Int, item: Playlist) {
        val extras = Bundle()
        extras.putLong(PLAY_LIST_DETAIL, item.id)
        activity!!.addFragment(
            R.id.nav_host_fragment, PlaylistDetailFragment(), PLAY_LIST_DETAIL, extras = extras
        )
    }

    override fun onPopupMenuClick(
        view: View,
        position: Int,
        item: Playlist,
        itemList: List<Playlist>
    ) {
        val deleted = PlaylistRepository(context).deletePlaylist(item.id)
        if (deleted != -1)
            mainViewModel.binding.mainContainer.snackbar(
                SUCCESS,
                getString(R.string.playlist_deleted_success, item.name),
                LENGTH_SHORT
            )
        else
            mainViewModel.binding.mainContainer.snackbar(
                ERROR,
                getString(R.string.playlist_deleted_error, item.name),
                LENGTH_SHORT,
                action = getString(R.string.retry),
                clickListener = View.OnClickListener {
                    onPopupMenuClick(view, position, item, itemList)
                })
    }

    private fun reloadAdapter() {
        mainViewModel.playLists().observe(this) {
            playlistAdapter.updateDataSet(it)
        }
    }
}
