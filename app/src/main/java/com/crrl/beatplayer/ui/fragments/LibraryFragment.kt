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
import androidx.viewpager.widget.ViewPager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentLibraryBinding
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.safeActivity
import com.crrl.beatplayer.models.MediaItemData
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.adapters.ViewPagerAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseSongDetailFragment
import com.crrl.beatplayer.utils.AutoClearBinding
import com.crrl.beatplayer.utils.LyricsExtractor
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class LibraryFragment : BaseSongDetailFragment() {

    private var binding by AutoClearBinding<FragmentLibraryBinding>(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_library, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if ((activity as MainActivity).isPermissionsGranted()) init()
        binding.apply {
            viewModel = mainViewModel
            isPermissionsGranted = (safeActivity as MainActivity).isPermissionsGranted()
            executePendingBindings()

            lifecycleOwner = this@LibraryFragment
        }
    }

    private fun init() {
        songDetailViewModel.currentData.observe(this) {
            loadLyrics(it)
        }

        binding.apply {
            initViewPager(binding.pagerSortMode)
            tabsContainer.setupWithViewPager(pagerSortMode)
        }
    }

    private fun initViewPager(viewPager: ViewPager) {
        val listSortModeAdapter = ViewPagerAdapter(safeActivity.supportFragmentManager).apply {
            addFragment(FavoriteFragment(), getString(R.string.favorites))
            addFragment(PlaylistFragment(), getString(R.string.playlists))
            addFragment(SongFragment(), getString(R.string.songs))
            addFragment(AlbumFragment(), getString(R.string.albums))
            addFragment(ArtistFragment(), getString(R.string.artists))
            addFragment(FolderFragment(), getString(R.string.folders))
        }

        viewPager.apply {
            adapter = listSortModeAdapter
            offscreenPageLimit = 1
            addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(s: Int) = Unit
                override fun onPageScrolled(p: Int, po: Float, pop: Int) = Unit
                override fun onPageSelected(p: Int) {
                    mainViewModel.settingsUtility.startPageIndexSelected = p
                }
            })
            setCurrentItem(mainViewModel.settingsUtility.startPageIndexSelected, false)
        }
    }

    private fun loadLyrics(mediaItemData: MediaItemData) {
        songDetailViewModel.updateLyrics()
        GlobalScope.launch {
            val lyric = withContext(IO) {
                LyricsExtractor.getLyric(mediaItemData) ?: getString(R.string.no_lyrics)
            }
            songDetailViewModel.updateLyrics(lyric)
        }
    }
}
