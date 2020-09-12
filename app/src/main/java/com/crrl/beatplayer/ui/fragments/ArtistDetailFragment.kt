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
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentArtistDetailBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.models.MediaItem
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.adapters.AlbumAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.ArtistViewModel
import com.crrl.beatplayer.ui.viewmodels.FavoriteViewModel
import com.crrl.beatplayer.ui.viewmodels.PlaylistViewModel
import com.crrl.beatplayer.utils.BeatConstants
import kotlinx.android.synthetic.main.layout_recyclerview.*
import org.koin.android.ext.android.inject

class ArtistDetailFragment : BaseFragment<MediaItem>() {

    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var binding: FragmentArtistDetailBinding
    private lateinit var artist: Artist
    private val artistViewModel by inject<ArtistViewModel>()
    private val playlistViewModel by inject<PlaylistViewModel>()
    private val favoriteViewModel by inject<FavoriteViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_artist_detail, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        retainInstance = true
    }

    @Suppress("UNCHECKED_CAST")
    private fun init() {
        val id = arguments!!.getLong(BeatConstants.ARTIST_KEY)
        artist = artistViewModel.getArtist(id)

        albumAdapter = AlbumAdapter(context).apply {
            itemClickListener = this@ArtistDetailFragment as ItemClickListener<Album>
            artistDetail = true
        }

        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = albumAdapter
            clipToOutline = true
        }

        binding.addFavorites.setOnClickListener { toggleAddFav() }

        artistViewModel.getArtistAlbums(artist.id)
            .filter { !albumAdapter.albumList.deepEquals(it) }
            .observe(viewLifecycleOwner) {
                albumAdapter.updateDataSet(it)
            }

        binding.let {
            it.artist = artist
            it.viewModel = mainViewModel
            it.executePendingBindings()

            it.lifecycleOwner = this
        }
    }

    private fun albumClicked(item: Album) {
        activity!!.addFragment(
            R.id.nav_host_fragment,
            AlbumDetailFragment(),
            BeatConstants.ALBUM_DETAIL,
            true,
            bundleOf(BeatConstants.ALBUM_KEY to item.id)
        )
    }

    override fun onItemClick(view: View, position: Int, item: MediaItem) {
        when (item) {
            is Song -> context?.shortToast("Song: ${item.title}")
            is Album -> albumClicked(item)
        }
    }

    override fun onPopupMenuClick(
        view: View,
        position: Int,
        item: MediaItem,
        itemList: List<MediaItem>
    ) {
        item as Song
        powerMenu!!.showAsAnchorRightTop(view)
        playlistViewModel.playLists().observe(viewLifecycleOwner) {
            buildPlaylistMenu(it, item)
        }
    }

    private fun toggleAddFav() {
        if (favoriteViewModel.favExist(artist.id)) {
            val resp = favoriteViewModel.deleteFavorites(longArrayOf(artist.id))
            showSnackBar(view, resp, R.string.artist_no_fav_ok)
        } else {
            val resp = favoriteViewModel.create(artist.toFavorite())
            showSnackBar(view, resp, R.string.artist_fav_ok)
        }
    }
}
