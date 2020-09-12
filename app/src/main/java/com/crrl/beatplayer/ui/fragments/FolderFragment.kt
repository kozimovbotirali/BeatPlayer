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
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FragmentFolderBinding
import com.crrl.beatplayer.extensions.*
import com.crrl.beatplayer.models.Folder
import com.crrl.beatplayer.ui.adapters.FolderAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.FolderViewModel
import com.crrl.beatplayer.utils.BeatConstants.FOLDER_KEY
import kotlinx.android.synthetic.main.layout_recyclerview.*
import org.koin.android.ext.android.inject

class FolderFragment : BaseFragment<Folder>() {

    private val viewModel by inject<FolderViewModel>()
    private lateinit var folderAdapter: FolderAdapter
    private lateinit var binding: FragmentFolderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_folder, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        retainInstance = true
    }

    private fun init() {
        folderAdapter = FolderAdapter(context).apply {
            itemClickListener = this@FolderFragment
        }

        list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = folderAdapter
        }

        viewModel.getFolders()
            .filter { !folderAdapter.folderList.deepEquals(it) }
            .observe(viewLifecycleOwner) {
            folderAdapter.updateDataSet(it)
        }

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
            it.executePendingBindings()
        }
    }

    override fun onItemClick(view: View, position: Int, item: Folder) {
        activity!!.addFragment(
            R.id.nav_host_fragment,
            FolderDetailFragment(),
            FOLDER_KEY,
            true,
            bundleOf(FOLDER_KEY to item.id)
        )
    }
}
