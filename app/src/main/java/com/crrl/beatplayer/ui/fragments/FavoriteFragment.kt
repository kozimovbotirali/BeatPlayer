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
import androidx.recyclerview.widget.GridLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentFavoriteBinding
import com.crrl.beatplayer.extensions.addFragment
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.safeActivity
import com.crrl.beatplayer.models.Favorite
import com.crrl.beatplayer.ui.adapters.FavoriteAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.FavoriteViewModel
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.PlayerConstants
import com.crrl.beatplayer.utils.PlayerConstants.ALBUM_DETAIL
import com.crrl.beatplayer.utils.PlayerConstants.ALBUM_KEY
import com.crrl.beatplayer.utils.PlayerConstants.ALBUM_TYPE
import com.crrl.beatplayer.utils.PlayerConstants.ARTIST_DETAIL
import com.crrl.beatplayer.utils.PlayerConstants.ARTIST_TYPE
import com.crrl.beatplayer.utils.PlayerConstants.FAVORITE_KEY
import com.crrl.beatplayer.utils.PlayerConstants.FAVORITE_NAME
import com.crrl.beatplayer.utils.PlayerConstants.FOLDER_KEY
import com.crrl.beatplayer.utils.PlayerConstants.FOLDER_TYPE
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

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
    }

    private fun init() {
        val sc = if (GeneralUtils.getRotation(safeActivity) == GeneralUtils.VERTICAL) 2 else 5

        favoriteAdapter = FavoriteAdapter(context).apply {
            itemClickListener = this@FavoriteFragment
            spanCount = sc
        }

        viewModel.getFavorites().observe(this) {
            favoriteAdapter.updateDataSet(it)
        }

        binding.apply {
            favoriteList.apply {
                layoutManager = GridLayoutManager(context, sc)
                adapter = favoriteAdapter
            }
        }

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    override fun onItemClick(view: View, position: Int, item: Favorite) {
        val extras = Bundle()

        when (item.type) {
            ARTIST_TYPE -> {
                extras.putLong(PlayerConstants.ARTIST_KEY, item.id)
                activity!!.addFragment(
                    R.id.nav_host_fragment,
                    ArtistDetailFragment(),
                    ARTIST_DETAIL,
                    true,
                    extras
                )
            }
            ALBUM_TYPE -> {
                extras.putLong(ALBUM_KEY, item.id)
                activity!!.addFragment(
                    R.id.nav_host_fragment,
                    AlbumDetailFragment(),
                    ALBUM_DETAIL,
                    true,
                    extras
                )
            }
            FOLDER_TYPE -> {
                extras.putString(FOLDER_KEY, item.artist)
                extras.putString(FAVORITE_NAME, item.title)
                activity!!.addFragment(
                    R.id.nav_host_fragment,
                    FolderDetailFragment(),
                    FOLDER_KEY,
                    true,
                    extras
                )
            }
            else -> {
                extras.putLong(FAVORITE_KEY, item.id)
                activity!!.addFragment(
                    R.id.nav_host_fragment,
                    FavoriteDetailFragment(),
                    FAVORITE_KEY,
                    true,
                    extras
                )
            }
        }
    }
}
