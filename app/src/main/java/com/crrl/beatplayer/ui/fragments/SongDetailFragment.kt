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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

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
            viewModel.getTime().observe(this) {

            }
        }

        binding.let {
            it.song = viewModel
            it.lifecycleOwner = this
        }
    }

    private fun updateViewComponents(song: Song) {
        if (song.path == "") return
        binding.apply {
            playContainer.setOnClickListener { safeActivity.toast("Play", Toast.LENGTH_SHORT) }
            nextBtn.setOnClickListener {
                (safeActivity as MainActivity).viewModel.next(song)
                viewModel.updateTime(0)
            }
            previousBtn.setOnClickListener {
                (safeActivity as MainActivity).viewModel.previous(song)
                viewModel.updateTime(0)
            }
            seekBar.apply {
                onStopTracking = {
                    viewModel.updateTime((it * song.duration / 100).toInt())
                    Log.println(
                        Log.DEBUG,
                        "wave",
                        "Progress set: ${(it * song.duration / 100).toInt()}"
                    )
                }

                onStartTracking = {
                    viewModel.updateTime((it * song.duration / 100).toInt())
                    Log.println(
                        Log.DEBUG,
                        "wave",
                        "Progress set: ${(it * song.duration / 100).toInt()}"
                    )
                }

                onProgressChanged = { progress, byUser ->
                    viewModel.updateTime((progress * song.duration / 100).toInt())
                    Log.println(
                        Log.DEBUG,
                        "wave",
                        "Progress set: ${(progress * song.duration / 100)}, and it's $byUser that user did this"
                    )
                }
            }
        }
    }
}
