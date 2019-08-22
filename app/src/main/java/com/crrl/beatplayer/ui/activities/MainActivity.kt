package com.crrl.beatplayer.ui.activities

import android.os.Bundle
import android.view.View
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.addFragment
import com.crrl.beatplayer.extensions.replaceFragment
import com.crrl.beatplayer.ui.activities.base.BaseActivity
import com.crrl.beatplayer.ui.fragments.LibraryFragment
import com.crrl.beatplayer.ui.fragments.SongDetailFragment
import com.crrl.beatplayer.utils.PlayerConstants
import com.github.florent37.kotlin.pleaseanimate.please
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            replaceFragment(
                R.id.nav_host_fragment,
                LibraryFragment(),
                PlayerConstants.LIBRARY
            )
        }
    }

    fun isPermissionsGranted(): Boolean {
        return permissionsGranted
    }

    override fun onResume() {
        super.onResume()
        showMiniPlayer()
    }

    fun onSongInfoClick(v: View) {
        addFragment(
            R.id.nav_host_fragment,
            SongDetailFragment(),
            PlayerConstants.NOW_PLAYING,
            true
        )
    }

    fun hideMiniPlayer() {
        if (bottom_controls != null) {
            bottom_controls.isEnabled = false
            please(100) {
                animate(bottom_controls) {
                    belowOf(main_container)
                    alpha(0f)
                }
            }.start()
        }
    }

    fun showMiniPlayer() {
        if (bottom_controls != null) {
            bottom_controls.isEnabled = true
            please(100) {
                animate(bottom_controls) {
                    bottomOfItsParent()
                    alpha(1f)
                }
            }.start()
        }
    }
}
