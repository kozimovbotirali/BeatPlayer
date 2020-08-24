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
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.GridLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentFavoriteBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.Favorite
import com.crrl.beatplayer.ui.adapters.FavoriteAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.FavoriteViewModel
import com.crrl.beatplayer.utils.BeatConstants
import com.crrl.beatplayer.utils.BeatConstants.ALBUM_DETAIL
import com.crrl.beatplayer.utils.BeatConstants.ALBUM_KEY
import com.crrl.beatplayer.utils.BeatConstants.ALBUM_TYPE
import com.crrl.beatplayer.utils.BeatConstants.ARTIST_DETAIL
import com.crrl.beatplayer.utils.BeatConstants.ARTIST_TYPE
import com.crrl.beatplayer.utils.BeatConstants.FAVORITE_KEY
import com.crrl.beatplayer.utils.BeatConstants.FOLDER_KEY
import com.crrl.beatplayer.utils.BeatConstants.FOLDER_TYPE
import com.crrl.beatplayer.utils.GeneralUtils
import org.koin.android.ext.android.inject

class FavoriteFragment : BaseFragment<Favorite>() {

    private val viewModel by inject<FavoriteViewModel>()
    private lateinit var binding: FragmentFavoriteBinding
    private lateinit var favoriteAdapter: FavoriteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_favorite, container)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
        retainInstance = true
    }

    private fun init() {
        val sc = if (GeneralUtils.getOrientation(requireActivity()) == GeneralUtils.PORTRAIT) 2 else 5

        favoriteAdapter = FavoriteAdapter(context).apply {
            itemClickListener = this@FavoriteFragment
            spanCount = sc
        }

        viewModel.getFavorites()
            .filter { !favoriteAdapter.favoriteList.deepEquals(it) }
            .observe(viewLifecycleOwner) {
            favoriteAdapter.updateDataSet(it)
        }

        binding.list.apply {
            layoutManager = GridLayoutManager(context, sc)
            adapter = favoriteAdapter
        }

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    override fun onItemClick(view: View, position: Int, item: Favorite) {
        when (item.type) {
            ARTIST_TYPE -> {

                activity!!.addFragment(
                    R.id.nav_host_fragment,
                    ArtistDetailFragment(),
                    ARTIST_DETAIL,
                    true,
                    bundleOf(BeatConstants.ARTIST_KEY to item.id)
                )
            }
            ALBUM_TYPE -> {
                activity!!.addFragment(
                    R.id.nav_host_fragment,
                    AlbumDetailFragment(),
                    ALBUM_DETAIL,
                    true,
                    bundleOf(ALBUM_KEY to item.id)
                )
            }
            FOLDER_TYPE -> {
                activity!!.addFragment(
                    R.id.nav_host_fragment,
                    FolderDetailFragment(),
                    FOLDER_KEY,
                    true,
                    bundleOf(FOLDER_KEY to item.id)
                )
            }
            else -> {
                activity!!.addFragment(
                    R.id.nav_host_fragment,
                    FavoriteDetailFragment(),
                    FAVORITE_KEY,
                    true,
                    bundleOf(FAVORITE_KEY to item.id)
                )
            }
        }
    }
}
