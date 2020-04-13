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
import com.crrl.beatplayer.repository.ArtistsRepository
import com.crrl.beatplayer.repository.FavoritesRepository
import com.crrl.beatplayer.ui.adapters.AlbumAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.PlayerConstants
import com.dgreenhalgh.android.simpleitemdecoration.linear.EndOffsetItemDecoration
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@Suppress("UNCHECKED_CAST")
class ArtistDetailFragment : BaseFragment<MediaItem>() {

    private val viewModel: SongViewModel by viewModel { parametersOf(context) }
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var binding: FragmentArtistDetailBinding
    private lateinit var artist: Artist

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

    private fun init() {
        val id = arguments!!.getLong(PlayerConstants.ARTIST_KEY)
        artist = ArtistsRepository.getInstance(context)?.getArtist(id)!!

        albumAdapter = AlbumAdapter(context).apply {
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

        val decor =
            EndOffsetItemDecoration(resources.getDimensionPixelOffset(R.dimen.song_item_size))
        viewModel.getArtistAlbums(artist.id).observe(this) {
            binding.albumList.apply {
                if (it.size > 1) {
                    removeItemDecoration(decor)
                    albumAdapter.updateDataSet(it)
                    addItemDecoration(decor)
                } else {
                    removeItemDecoration(decor)
                    albumAdapter.updateDataSet(it)
                }
            }
        }

        binding.let {
            it.artist = artist
            it.viewModel = viewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    override fun addToList(playListId: Long, song: Song) {
        viewModel.addToPlaylist(playListId, listOf(song))
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
        viewModel.playLists().observe(this) {
            buildPlaylistMenu(it, item)
        }
    }

    private fun toggleAddFav() {
        val favoritesRepository = FavoritesRepository(context)
        if (favoritesRepository.favExist(artist.id)) {
            favoritesRepository.deleteFavorites(longArrayOf(artist.id))
        } else {
            favoritesRepository.createFavorite(artist.toFavorite())
        }
        println(favoritesRepository.favExist(artist.id))
    }
}
