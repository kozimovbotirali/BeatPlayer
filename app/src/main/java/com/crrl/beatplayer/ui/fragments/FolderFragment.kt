package com.crrl.beatplayer.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.databinding.FolderFragmentBinding
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.models.Folder
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.modelview.FolderAdapter
import com.crrl.beatplayer.ui.viewmodels.FolderViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FolderFragment : BaseFragment<Folder>() {

    companion object {
        fun newInstance() = FolderFragment()
    }

    private val viewModel: FolderViewModel by viewModel { parametersOf(context) }
    private lateinit var folderAdapter: FolderAdapter
    private lateinit var binding: FolderFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.folder_fragment, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        folderAdapter = FolderAdapter(context).apply {
            itemClickListener = this@FolderFragment
        }

        binding.apply {
            folderList.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = folderAdapter
            }
        }

        viewModel.getFolders().observe(this) {
            folderAdapter.updateDataSet(it)
        }

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }
    }
}
