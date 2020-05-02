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
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentSongDetailBinding
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.setCustomColor
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.fragments.base.BaseSongDetailFragment
import com.crrl.beatplayer.utils.SettingsUtility
import rm.com.audiowave.OnSamplingListener
import timber.log.Timber

class SongDetailFragment : BaseSongDetailFragment() {

    private lateinit var binding: FragmentSongDetailBinding
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
        updateViewComponents()
        mainViewModel.getCurrentSong().observe(viewLifecycleOwner) {
            initNeeded(it, emptyList())
        }

        binding.addPlaylist.setOnClickListener { shareItem() }
        setupRawData()
        binding.let {
            it.viewModel = mainViewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    private fun setupRawData() {
        try {
            mainViewModel.getRawData().observe(viewLifecycleOwner) {
                binding.seekBar.setRawData(it, object : OnSamplingListener {
                    override fun onComplete() = Unit
                })
            }
        } catch (e: IllegalStateException) {
            Timber.e(e)
        }
    }

    private fun updateViewComponents() {
        binding.apply {
            playContainer.setOnClickListener {}
            nextBtn.setOnClickListener {
                val song = mainViewModel.getCurrentSong().value ?: return@setOnClickListener
                mainViewModel.next(song.id)
            }
            previousBtn.setOnClickListener {
                val song = mainViewModel.getCurrentSong().value ?: return@setOnClickListener
                mainViewModel.previous(song.id)
            }
            seekBar.apply {
                onStopTracking = {
                    val song = mainViewModel.getCurrentSong().value ?: Song()
                    mainViewModel.update((it * song.duration / 100).toInt())
                }

                onStartTracking = {
                    val song = mainViewModel.getCurrentSong().value ?: Song()
                    mainViewModel.update((it * song.duration / 100).toInt())
                }

                onProgressChanged = { progress, _ ->
                    val song = mainViewModel.getCurrentSong().value ?: Song()
                    mainViewModel.update((progress * song.duration / 100).toInt())
                }
            }
        }

        mainViewModel.getTime().observe(viewLifecycleOwner) {
            val song = mainViewModel.getCurrentSong().value ?: return@observe
            binding.seekBar.apply {
                if (it == -1) {
                    progress = 0F
                    setRawData(ByteArray(Int.SIZE_BYTES))
                }
                // Percent to Milliseconds and Normalize step to 1000
                val t = (progress * song.duration / 100).toInt() / 1000 * 1000
                if (t != it) {
                    val time = (it * 100) / song.duration.toFloat()
                    val timeF = if (time < 0F) 0F else if (time > 100F) 100F else time
                    progress = timeF
                }
            }
        }
    }
}
