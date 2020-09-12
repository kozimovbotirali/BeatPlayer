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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.alertdialog.actions.AlertItemAction
import com.crrl.beatplayer.alertdialog.enums.AlertItemTheme
import com.crrl.beatplayer.databinding.FragmentArtistBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.ui.adapters.ArtistAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.ArtistViewModel
import com.crrl.beatplayer.utils.BeatConstants
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.SortModes.ArtistModes.Companion.ARTIST_ALBUM_COUNT
import com.crrl.beatplayer.utils.SortModes.ArtistModes.Companion.ARTIST_A_Z
import com.crrl.beatplayer.utils.SortModes.ArtistModes.Companion.ARTIST_SONG_COUNT
import com.crrl.beatplayer.utils.SortModes.ArtistModes.Companion.ARTIST_Z_A
import org.koin.android.ext.android.inject

class ArtistFragment : BaseFragment<Artist>() {

    private val viewModel by inject<ArtistViewModel>()
    private lateinit var binding: FragmentArtistBinding
    private lateinit var artistAdapter: ArtistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_artist, container)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        retainInstance = true
    }

    private fun init() {
        val sc = if (GeneralUtils.getOrientation(requireActivity()) == GeneralUtils.PORTRAIT) 2 else 5

        artistAdapter = ArtistAdapter(context).apply {
            showHeader = true
            itemClickListener = this@ArtistFragment
            spanCount = sc
        }

        binding.list.apply {
            layoutManager = GridLayoutManager(context!!, sc).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return if (position == 0) sc else 1
                    }
                }
            }
            adapter = artistAdapter
        }

        viewModel.getArtists()
            .filter { !artistAdapter.artistList.deepEquals(it) }
            .observe(viewLifecycleOwner) { list ->
                artistAdapter.updateDataSet(list)
            }

        binding.let {
            it.viewModel = viewModel
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
                    context!!.getString(R.string.sort_az),
                    settingsUtility.artistSortOrder == ARTIST_A_Z,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.artistSortOrder = ARTIST_A_Z
                },
                AlertItemAction(
                    context!!.getString(R.string.sort_za),
                    settingsUtility.artistSortOrder == ARTIST_Z_A,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.artistSortOrder = ARTIST_Z_A
                },
                AlertItemAction(
                    context!!.getString(R.string.song_count),
                    settingsUtility.artistSortOrder == ARTIST_SONG_COUNT,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.artistSortOrder = ARTIST_SONG_COUNT
                },
                AlertItemAction(
                    context!!.getString(R.string.album_count),
                    settingsUtility.artistSortOrder == ARTIST_ALBUM_COUNT,
                    AlertItemTheme.DEFAULT
                ) {
                    it.selected = true
                    settingsUtility.artistSortOrder = ARTIST_ALBUM_COUNT
                }
            ))
    }

    override fun onItemClick(view: View, position: Int, item: Artist) {
        activity!!.addFragment(
            R.id.nav_host_fragment,
            ArtistDetailFragment(),
            BeatConstants.ARTIST_DETAIL,
            true,
            bundleOf(BeatConstants.ARTIST_KEY to item.id)
        )
    }

    override fun onSortClick(view: View) {
        dialog.show(activity as AppCompatActivity)
    }
}
