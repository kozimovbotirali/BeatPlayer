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

package com.crrl.beatplayer.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.ActivitySelectSongBinding
import com.crrl.beatplayer.extensions.replaceFragment
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.fragments.LibraryFragment
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.PlayerConstants
import com.google.gson.Gson
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class SelectSongActivity : AppCompatActivity() {

    private val viewModel: SongViewModel by viewModel { parametersOf(this) }
    private lateinit var binding: ActivitySelectSongBinding
    private val songsSelected = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?){
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_song)

        binding.let {
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }
    
    private fun updateView(List<Song>){
        
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, Intent().apply { putExtra("songs", Gson().toJson(songsSelected)) })
        finish()
        super.onBackPressed()
    }
}
