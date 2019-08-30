package com.crrl.beatplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
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
            }
            tabsContainer.apply {
                setupWithViewPager(pagerSortMode)
            }
            executePendingBindings()
        }
    }
}
