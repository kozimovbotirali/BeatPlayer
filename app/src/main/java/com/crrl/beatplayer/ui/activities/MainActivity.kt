package com.crrl.beatplayer.ui.activities

import android.content.ContentUris
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.ActivityMainBinding
import com.crrl.beatplayer.extensions.addFragment
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.replaceFragment
import com.crrl.beatplayer.extensions.toast
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.activities.base.BaseActivity
import com.crrl.beatplayer.ui.fragments.*
import com.crrl.beatplayer.ui.viewmodels.MainViewModel
import com.crrl.beatplayer.utils.PlayerConstants
import com.github.florent37.kotlin.pleaseanimate.please
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : BaseActivity() {

    val viewModel: MainViewModel by viewModel { parametersOf(this) }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel.getCurrentSong().observe(this) {
            updateView(it)
        }
        if (savedInstanceState == null) {
            replaceFragment(
                R.id.nav_host_fragment,
                LibraryFragment(),
                PlayerConstants.LIBRARY
            )
            update(Song())
        }
    }

    private fun updateView(song: Song) {
        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }
        if (song.path == "") return
        binding.apply {
            miniPlayerCover.clipToOutline = true
            val uri = ContentUris.withAppendedId(PlayerConstants.ARTWORK_URI, song.albumId)
            Glide.with(applicationContext)
                .load(uri)
                .override(150)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(miniPlayerCover.drawable)
                .error(R.drawable.ic_empty_cover)
                .into(miniPlayerCover)
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

    override fun onBackPressed() {
        var isDismiss = true
        supportFragmentManager.fragments.forEach {
            isDismiss = when (it) {
                is AlbumDetailFragment -> it.onBackPressed()
                is ArtistDetailFragment -> it.onBackPressed()
                is PlaylistDetailFragment -> it.onBackPressed()
                is FolderDetailFragment -> it.onBackPressed()
                else -> true
            }
        }
        if (isDismiss) super.onBackPressed()
    }

    fun hideMiniPlayer() {
        if (bottom_controls != null) {
            bottom_controls.isEnabled = false
            please(100) {
                animate(bottom_controls) {
                    belowOf(main_container)
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
                }
            }.start()
        }
    }

    fun play(view: View) {

    }

    fun next(view: View) {
        toast("Next", Toast.LENGTH_SHORT)
    }

    fun previous(view: View) {
        toast("Previous", Toast.LENGTH_SHORT)
    }

    fun update(song: Song) {
        viewModel.update(song)
    }

    fun update(newList: List<Song>) {
        viewModel.update(newList)
    }
}
