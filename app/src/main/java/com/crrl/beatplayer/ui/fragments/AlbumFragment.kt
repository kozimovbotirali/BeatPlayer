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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.alertdialog.actions.AlertItemAction
import com.crrl.beatplayer.alertdialog.enums.AlertItemTheme
import com.crrl.beatplayer.databinding.FragmentAlbumBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.ui.adapters.AlbumAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.AlbumViewModel
import com.crrl.beatplayer.utils.BeatConstants
import com.crrl.beatplayer.utils.BeatConstants.ALBUM_KEY
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.GeneralUtils.PORTRAIT
import com.crrl.beatplayer.utils.SortModes
import org.koin.android.ext.android.inject

class AlbumFragment : BaseFragment<Album>() {

    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var binding: FragmentAlbumBinding
    private val albumViewModel by inject<AlbumViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_album, container)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        retainInstance = true
    }

    private fun init() {
        val sc = if (GeneralUtils.getOrientation(requireActivity()) == PORTRAIT) 2 else 5

        albumAdapter = AlbumAdapter(context).apply {
            showHeader = true
            itemClickListener = this@AlbumFragment
            spanCount = sc
        }

        binding.list.apply {
            layoutManager = GridLayoutManager(context, sc).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position == 0) sc else 1
                    }
                }
            }
            adapter = albumAdapter
        }

        albumViewModel.getAlbums()
            .filter { !albumAdapter.albumList.deepEquals(it) }
            .observe(viewLifecycleOwner) { list ->
                albumAdapter.updateDataSet(list)
            }

        binding.let {
            it.viewModel = albumViewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }

        createDialog()
    }

    private fun createDialog() {
        dialog = buildDialog(
            getString(R.string.sort_title),
            getString(R.string.sort_msg),
            listOf(
                AlertItemAction(
                    requireContext().getString(R.string.sort_az),
                    settingsUtility.albumSortOrder == SortModes.AlbumModes.ALBUM_A_Z,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.albumSortOrder = SortModes.AlbumModes.ALBUM_A_Z
                    reloadAdapter()
                },
                AlertItemAction(
                    requireContext().getString(R.string.sort_za),
                    settingsUtility.albumSortOrder == SortModes.AlbumModes.ALBUM_Z_A,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.albumSortOrder = SortModes.AlbumModes.ALBUM_Z_A
                    reloadAdapter()
                },
                AlertItemAction(
                    requireContext().getString(R.string.sort_year),
                    settingsUtility.albumSortOrder == SortModes.AlbumModes.ALBUM_YEAR,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.albumSortOrder =
                        SortModes.AlbumModes.ALBUM_YEAR
                    reloadAdapter()
                },
                AlertItemAction(
                    requireContext().getString(R.string.artist),
                    settingsUtility.albumSortOrder == SortModes.AlbumModes.ALBUM_ARTIST,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.albumSortOrder = SortModes.AlbumModes.ALBUM_ARTIST
                    reloadAdapter()
                },
                AlertItemAction(
                    requireContext().getString(R.string.song_count),
                    settingsUtility.albumSortOrder == SortModes.AlbumModes.ALBUM_SONG_COUNT,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.albumSortOrder = SortModes.AlbumModes.ALBUM_SONG_COUNT
                    reloadAdapter()
                }
            )
        )
    }

    private fun reloadAdapter() {
        albumViewModel.update()
    }

    override fun onItemClick(view: View, position: Int, item: Album) {
        activity?.addFragment(
            R.id.nav_host_fragment,
            AlbumDetailFragment(),
            BeatConstants.ALBUM_DETAIL,
            true,
            bundleOf(ALBUM_KEY to item.id)
        )
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Album, itemList: List<Album>) {
        context?.shortToast("Menu of ${item.title}")
    }

    override fun onPlayAllClick(view: View) {
        context?.shortToast("Shuffle")
    }

    override fun onSortClick(view: View) {
        dialog.show(activity as AppCompatActivity)
    }
}
