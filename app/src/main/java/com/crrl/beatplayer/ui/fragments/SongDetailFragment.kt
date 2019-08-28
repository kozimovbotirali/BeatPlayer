package com.crrl.beatplayer.ui.fragments


import android.content.ContentUris
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentSongDetailBinding
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.safeActivity
import com.crrl.beatplayer.extensions.toast
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.fragments.base.BaseSongDetailFragment
import com.crrl.beatplayer.ui.viewmodels.SongDetailViewModel
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.PlayerConstants
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import rm.com.audiowave.OnSamplingListener

class SongDetailFragment : BaseSongDetailFragment() {

    private lateinit var binding: FragmentSongDetailBinding
    private val viewModel: SongDetailViewModel by viewModel { parametersOf(safeActivity as MainActivity) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_song_detail, container)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        viewModel.getCurrentData().observe(this) {
            updateViewComponents(it)
        }
        binding.let {
            it.song = viewModel
            it.lifecycleOwner = this
        }
    }

    private fun updateViewComponents(song: Song) {
        if (song.path == "") return
        binding.apply {
            nowPlayingCover.clipToOutline = true
            val uri = ContentUris.withAppendedId(PlayerConstants.ARTWORK_URI, song.albumId)
            Glide.with(context!!)
                .load(uri)
                .transition(withCrossFade())
                .placeholder(R.drawable.ic_empty_cover)
                .error(R.drawable.ic_empty_cover)
                .into(nowPlayingCover)
            if (isDetached) return
            Thread {
                val data = GeneralUtils.audio2Raw(song.path)
                try {
                    safeActivity.runOnUiThread {
                        if (data == null) {
                            safeActivity.toast("File Not Found", Toast.LENGTH_SHORT)
                            return@runOnUiThread
                        }
                        seekBar.setRawData(data, object : OnSamplingListener {
                            override fun onComplete() {

                            }
                        })
                    }
                } catch (e: IllegalStateException) {
                    Log.println(Log.ERROR, "IllegalStateException", e.message!!)
                }
            }.start()
        }
    }

}
