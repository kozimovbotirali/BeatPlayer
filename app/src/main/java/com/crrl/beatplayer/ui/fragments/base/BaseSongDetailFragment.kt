package com.crrl.beatplayer.ui.fragments.base

import android.os.Bundle
import com.crrl.beatplayer.R
import com.crrl.beatplayer.models.MediaItem
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.fragments.SongDetailFragment

open class BaseSongDetailFragment : BaseFragment<MediaItem>() {


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showHideBottomSheet()
    }

    override fun onPause() {
        showHideBottomSheet()
        super.onPause()
    }

    override fun onResume() {
        showHideBottomSheet()
        super.onResume()
    }

    private fun showHideBottomSheet() {
        val activity = activity as MainActivity
        if (activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment) is SongDetailFragment) {
            activity.hideMiniPlayer()
        } else {
            activity.showMiniPlayer()
        }
    }
}