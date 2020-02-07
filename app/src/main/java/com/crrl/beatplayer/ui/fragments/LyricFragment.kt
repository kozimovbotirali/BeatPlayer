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
import androidx.fragment.app.Fragment
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.safeActivity
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.viewmodels.SongDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LyricFragment : Fragment() {

    private val viewModel: SongDetailViewModel by viewModel { parametersOf(safeActivity as MainActivity) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.bindingLB = inflater.inflateWithBinding(R.layout.fragment_lyric, container)
        return viewModel.bindingLB!!.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        viewModel.bindingLB!!.let {
            it.song = viewModel.getCurrentData().value
            it.viewModel = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }
    }
}
