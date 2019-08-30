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

import android.content.ContentUris
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.PlayerConstants
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@Suppress("UNCHECKED_CAST")
class ArtistDetailFragment : BaseFragment<MediaItem>() {

    companion object {
        fun newInstance() = ArtistDetailFragment()
    }

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
        val sc = if (GeneralUtils.getRotation(safeActivity) == GeneralUtils.VERTICAL) 3 else 5

        artist = arguments!!.getString(PlayerConstants.ARTIST_KEY)!!.toArtist()

        albumAdapter = AlbumAdapter(context).apply {
            itemClickListener = this@ArtistDetailFragment as ItemClickListener<Album>
            spanCount = sc
        }

        binding.apply {
            albumList.apply {
                layoutManager = GridLayoutManager(context, sc)
                adapter = albumAdapter
                isNestedScrollingEnabled = false
                setHasFixedSize(true)
            }
            cover.clipToOutline = true
        }

        viewModel.getArtistAlbums(artist.id).observe(this) {
            albumAdapter.updateDataSet(it)
            val uri = ContentUris.withAppendedId(PlayerConstants.ARTWORK_URI, it[0].id)
            Glide.with(context!!)
                .load(uri)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.song_cover_frame)
                .error(R.drawable.ic_empty_cover)
                .into(binding.cover)
        }

        binding.artist = artist
    }

    override fun addToList(playListId: Long, song: Song) {
        viewModel.addToPlaylist(playListId, arrayOf(song.id).toLongArray())
    }

    private fun albumClicked(item: Album) {
        val extras = Bundle()
        extras.putString(PlayerConstants.ALBUM_KEY, item.toString())
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

    override fun onPopupMenuClick(view: View, position: Int, item: MediaItem) {
        item as Song
        powerMenu!!.showAsAnchorRightTop(view)
        viewModel.playLists().observe(this) {
            buildPlaylistMenu(it, item)
        }
    }
}
