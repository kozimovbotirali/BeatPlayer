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
import androidx.recyclerview.widget.LinearLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentArtistDetailBinding
import com.crrl.beatplayer.extensions.addFragment
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
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
import com.crrl.beatplayer.utils.PlayerConstants
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
    }

    @Suppress("UNCHECKED_CAST")
    private fun init() {
        val id = arguments!!.getLong(PlayerConstants.ARTIST_KEY)
        artist = artistViewModel.getArtist(id)

        albumAdapter = AlbumAdapter(context, mainViewModel).apply {
            itemClickListener = this@ArtistDetailFragment as ItemClickListener<Album>
            artistDetail = true
        }

        binding.apply {
            albumList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = albumAdapter
            }
            addFavorites.setOnClickListener { toggleAddFav() }
        }

        artistViewModel.getArtistAlbums(artist.id).observe(this) {
            albumAdapter.updateDataSet(it)
        }

        mainViewModel.getCurrentSong().observe(this){
            albumAdapter.notifyDataSetChanged()
        }

        binding.let {
            it.artist = artist
            it.viewModel = mainViewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    private fun albumClicked(item: Album) {
        val extras = Bundle()
        extras.putLong(PlayerConstants.ALBUM_KEY, item.id)
        activity!!.addFragment(
            R.id.nav_host_fragment,
            AlbumDetailFragment(),
            PlayerConstants.ALBUM_DETAIL,
            true,
            extras
        )
    }

    override fun onItemClick(view: View, position: Int, item: MediaItem) {
        when (item) {
            is Song -> Toast.makeText(context, "Song: ${item.title}", Toast.LENGTH_SHORT).show()
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
        playlistViewModel.playLists().observe(this) {
            buildPlaylistMenu(it, item)
        }
    }

    private fun toggleAddFav() {
        if (favoriteViewModel.favExist(artist.id)) {
            val resp = favoriteViewModel.deleteFavorites(longArrayOf(artist.id))
            showSnackBar(view, resp, 0, R.string.artist_no_fav_ok)
        } else {
            val resp = favoriteViewModel.create(artist.toFavorite())
            showSnackBar(view, resp, 1, R.string.artist_fav_ok)
        }
    }
}
