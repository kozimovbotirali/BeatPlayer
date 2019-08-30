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


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentSearchBinding
import com.crrl.beatplayer.extensions.addFragment
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.safeActivity
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.models.MediaItem
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.adapters.AlbumAdapter
import com.crrl.beatplayer.ui.adapters.ArtistAdapter
import com.crrl.beatplayer.ui.adapters.SongAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.GeneralUtils.toggleShowKeyBoard
import com.crrl.beatplayer.utils.PlayerConstants
import kotlinx.android.synthetic.main.fragment_search.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

@Suppress("UNCHECKED_CAST")
class SearchFragment : BaseFragment<MediaItem>() {

    private val viewModel: SongViewModel by viewModel { parametersOf(context) }
    private lateinit var binding: FragmentSearchBinding

    private lateinit var songAdapter: SongAdapter
    private lateinit var albumAdapter: AlbumAdapter
    private lateinit var artistAdapter: ArtistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_search, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        val sc = if (GeneralUtils.getRotation(safeActivity) == GeneralUtils.VERTICAL) 3 else 5

        songAdapter = SongAdapter(activity).apply {
            itemClickListener = this@SearchFragment as ItemClickListener<Song>
        }

        albumAdapter = AlbumAdapter(context).apply {
            itemClickListener = this@SearchFragment as ItemClickListener<Album>
            spanCount = sc
        }

        artistAdapter = ArtistAdapter(context).apply {
            itemClickListener = this@SearchFragment as ItemClickListener<Artist>
            spanCount = sc
        }

        binding.apply {
            searchSrcText.apply {
                addTextChangedListener(object : TextWatcher {
                    override fun onTextChanged(
                        src: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        viewModel!!.search(src.toString())
                        status = src.isNotEmpty()
                    }

                    override fun afterTextChanged(s: Editable?) = Unit

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) = Unit
                })
                toggleShowKeyBoard(context, this, true)
            }

            songList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = songAdapter
            }

            albumList.apply {
                layoutManager = GridLayoutManager(context, sc)
                adapter = albumAdapter
            }

            artistList.apply {
                layoutManager = GridLayoutManager(context, sc)
                adapter = artistAdapter
            }

            back.setOnClickListener {
                toggleShowKeyBoard(context, view.search_src_text, false)
                activity!!.onBackPressed()
            }

            close.setOnClickListener {
                searchSrcText.setText("")
            }
        }

        viewModel.searchLiveData.observe(this) {
            songAdapter.updateDataSet(it.songList)
            albumAdapter.updateDataSet(it.albumList)
            artistAdapter.updateDataSet(it.artistList)
        }

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
            it.status = false
        }
    }

    override fun addToList(playListId: Long, song: Song) {
        viewModel.addToPlaylist(playListId, arrayOf(song.id).toLongArray())
    }

    override fun onItemClick(view: View, position: Int, item: MediaItem) {
        super.onItemClick(view, position, item)
        toggleShowKeyBoard(context, this@SearchFragment.view!!.search_src_text, false)
        when (item) {
            is Song -> (safeActivity as MainActivity).viewModel.update(item)
            is Album -> albumClicked(item)
            is Artist -> artistClicked(item)
        }
    }

    override fun onPopupMenuClick(view: View, position: Int, item: MediaItem) {
        super.onPopupMenuClick(view, position, item)
        when (item) {
            is Song -> songPopup(item, view)
            is Album -> Toast.makeText(
                context,
                "Album Menu of ${item.title}",
                Toast.LENGTH_SHORT
            ).show()
            is Artist -> Toast.makeText(
                context,
                "Artist Menu of ${item.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun songPopup(song: Song, view: View) {
        powerMenu!!.showAsAnchorRightTop(view)
        viewModel.playLists().observe(this) {
            buildPlaylistMenu(it, song)
        }
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

    private fun artistClicked(item: Artist) {
        val extras = Bundle()
        extras.putString(PlayerConstants.ARTIST_KEY, item.toString())
        activity!!.addFragment(
            R.id.nav_host_fragment,
            ArtistDetailFragment(),
            PlayerConstants.ARTIST_DETAIL,
            true,
            extras
        )
    }
}
