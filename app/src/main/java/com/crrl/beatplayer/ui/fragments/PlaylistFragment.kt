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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.crrl.beatplayer.R
import com.crrl.beatplayer.alertdialog.AlertDialog
import com.crrl.beatplayer.alertdialog.dialogs.AlertItemAction
import com.crrl.beatplayer.alertdialog.stylers.AlertItemTheme
import com.crrl.beatplayer.alertdialog.stylers.AlertType
import com.crrl.beatplayer.alertdialog.stylers.InputStyle
import com.crrl.beatplayer.databinding.FragmentPlaylistBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.repository.PlaylistRepository
import com.crrl.beatplayer.ui.activities.SelectSongActivity
import com.crrl.beatplayer.ui.adapters.PlaylistAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.PlayerConstants
import com.crrl.beatplayer.utils.PlayerConstants.PLAY_LIST_DETAIL
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class PlaylistFragment : BaseFragment<Playlist>() {

    private val viewModel: SongViewModel by viewModel { parametersOf(context) }
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

        binding.createPlayList.setOnClickListener { createPlayList() }

        reloadAdapter()

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
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
        PlaylistRepository.getInstance(context).deletePlaylist(item.id)
        safeActivity.toast("${item.name} Deleted ", Toast.LENGTH_SHORT)
    }

    private fun createPlayList() {
        createDialog().show(safeActivity as AppCompatActivity)
    }

    private fun createDialog(): AlertDialog {
        val style = InputStyle(
            activity?.getColorByTheme(R.attr.colorPrimarySecondary, "colorPrimarySecondary")!!,
            activity!!.getColorByTheme(R.attr.colorPrimarySecondary2, "colorPrimarySecondary2"),
            activity!!.getColorByTheme(R.attr.titleTextColor, "titleTextColor"),
            activity!!.getColorByTheme(R.attr.bodyTextColor, "bodyTextColor"),
            safeActivity.getColorByTheme(R.attr.colorAccent, "colorAccent"),
            "${safeActivity.getString(R.string.playlist)} ${GeneralUtils.addZeros(
                PlaylistRepository.getInstance(
                    context
                ).getPlayListsCount() + 1
            )}"
        )
        return AlertDialog(
            getString(R.string.new_playlist),
            getString(R.string.create_playlist),
            style,
            AlertType.INPUT,
            getString(R.string.input_hint)
        ).apply {
            addItem(AlertItemAction("Cancel", false, AlertItemTheme.CANCEL) {
            })
            addItem(AlertItemAction("OK", false, AlertItemTheme.ACCEPT) {
                safeActivity.startActivityForResult(
                    Intent(
                        safeActivity,
                        SelectSongActivity::class.java
                    ).apply { putExtra(PLAY_LIST_DETAIL, it.input) },
                    1
                )
            })
        }
    }

    private fun reloadAdapter() {
        viewModel.playLists().observe(this) {
            playlistAdapter.updateDataSet(it)
        }
    }
}
