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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentLibraryBinding
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.safeActivity
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.adapters.ViewPagerAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseSongDetailFragment
import com.crrl.beatplayer.ui.viewmodels.LibraryViewModel
import com.crrl.beatplayer.utils.SettingsUtility


class LibraryFragment : BaseSongDetailFragment() {

    private lateinit var viewModel: LibraryViewModel
    private lateinit var binding: FragmentLibraryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_library, container)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if ((activity as MainActivity).isPermissionsGranted()) init()
        binding.apply {
            lifecycleOwner = this@LibraryFragment
            isPermissionsGranted = (safeActivity as MainActivity).isPermissionsGranted()
        }
    }

    private fun init() {

        viewModel = ViewModelProviders.of(this).get(LibraryViewModel::class.java)

        val listSortModeAdapter = ViewPagerAdapter(safeActivity.supportFragmentManager)

        listSortModeAdapter.apply {
            addFragment(SongFragment(), getString(R.string.songs))
            addFragment(AlbumFragment(), getString(R.string.albums))
            addFragment(ArtistFragment(), getString(R.string.artists))
            addFragment(PlaylistFragment(), getString(R.string.playlists))
            addFragment(FolderFragment(), getString(R.string.folders))
        }

        binding.apply {
            pagerSortMode.apply {
                adapter = listSortModeAdapter
                offscreenPageLimit = listSortModeAdapter.count
                currentItem = SettingsUtility.getInstance(safeActivity).startPageIndexSelected
                addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
                    override fun onPageScrollStateChanged(state: Int) = Unit
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) = Unit
                    override fun onPageSelected(position: Int) {
                        SettingsUtility.getInstance(safeActivity).startPageIndexSelected = position
                    }
                })
            }
            tabsContainer.apply {
                setupWithViewPager(pagerSortMode)
            }
            executePendingBindings()
        }
    }
}
