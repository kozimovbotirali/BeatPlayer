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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.alertdialog.AlertDialog
import com.crrl.beatplayer.alertdialog.dialogs.AlertItemAction
import com.crrl.beatplayer.alertdialog.stylers.AlertItemStyle
import com.crrl.beatplayer.alertdialog.stylers.AlertItemTheme
import com.crrl.beatplayer.alertdialog.stylers.AlertType
import com.crrl.beatplayer.databinding.FragmentSongBinding
import com.crrl.beatplayer.extensions.getColorByTheme
import com.crrl.beatplayer.extensions.inflateWithBinding
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.safeActivity
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.adapters.SongAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.SongViewModel
import com.crrl.beatplayer.utils.SettingsUtility
import com.crrl.beatplayer.utils.SortModes
import com.dgreenhalgh.android.simpleitemdecoration.linear.EndOffsetItemDecoration
import kotlinx.android.synthetic.main.fragment_song.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SongFragment : BaseFragment<Song>() {

    private val viewModel: SongViewModel by viewModel { parametersOf(context) }
    private lateinit var songAdapter: SongAdapter
    private lateinit var binding: FragmentSongBinding

    companion object {
        fun newInstance() = SongFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = inflater.inflateWithBinding(R.layout.fragment_song, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        // Set up adapter
        songAdapter = SongAdapter(activity).apply {
            showHeader = true
            itemClickListener = this@SongFragment
        }

        // Set up RecyclerView
        view.song_list.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = songAdapter
            addItemDecoration(EndOffsetItemDecoration(resources.getDimensionPixelOffset(R.dimen.song_item_size)))
        }

        dialog = buildSortModesDialog()

        viewModel.liveData().observe(this) { list ->
            songAdapter.updateDataSet(list)
        }

        binding.let {
            it.viewModel = viewModel
            it.lifecycleOwner = this
        }
    }

    private fun buildSortModesDialog(): AlertDialog {
        val style = AlertItemStyle().apply {
            textColor = activity?.getColorByTheme(R.attr.titleTextColor, "titleTextColor")!!
            selectedTextColor = activity?.getColorByTheme(R.attr.colorAccent, "colorAccent")!!
            backgroundColor =
                activity?.getColorByTheme(R.attr.colorPrimarySecondary2, "colorPrimarySecondary2")!!
        }
        val alert = AlertDialog(
            getString(R.string.sort_title),
            getString(R.string.sort_msg),
            style,
            AlertType.BOTTOM_SHEET
        )
        alert.addItem(AlertItemAction(
            context!!.getString(R.string.sort_default),
            SettingsUtility.getInstance(context).songSortOrder == SortModes.SongModes.SONG_DEFAULT,
            AlertItemTheme.DEFAULT
        ) { action ->
            action.selected = true
            SettingsUtility.getInstance(context).songSortOrder = SortModes.SongModes.SONG_DEFAULT
            reloadAdapter()
        })
        alert.addItem(AlertItemAction(
            context!!.getString(R.string.sort_az),
            SettingsUtility.getInstance(context).songSortOrder == SortModes.SongModes.SONG_A_Z,
            AlertItemTheme.DEFAULT
        ) { action ->
            action.selected = true
            SettingsUtility.getInstance(context).songSortOrder = SortModes.SongModes.SONG_A_Z
            reloadAdapter()
        })
        alert.addItem(AlertItemAction(
            context!!.getString(R.string.sort_za),
            SettingsUtility.getInstance(context).songSortOrder == SortModes.SongModes.SONG_Z_A,
            AlertItemTheme.DEFAULT
        ) { action ->
            action.selected = true
            SettingsUtility.getInstance(context).songSortOrder = SortModes.SongModes.SONG_Z_A
            reloadAdapter()
        })
        alert.addItem(AlertItemAction(
            context!!.getString(R.string.sort_duration),
            SettingsUtility.getInstance(context).songSortOrder == SortModes.SongModes.SONG_DURATION,
            AlertItemTheme.DEFAULT
        ) { action ->
            action.selected = true
            SettingsUtility.getInstance(context).songSortOrder = SortModes.SongModes.SONG_DURATION
            reloadAdapter()
        })
        alert.addItem(AlertItemAction(
            context!!.getString(R.string.sort_year),
            SettingsUtility.getInstance(context).songSortOrder == SortModes.SongModes.SONG_YEAR,
            AlertItemTheme.DEFAULT
        ) { action ->
            action.selected = true
            SettingsUtility.getInstance(context).songSortOrder = SortModes.SongModes.SONG_YEAR
            reloadAdapter()
        })
        alert.addItem(AlertItemAction(
            context!!.getString(R.string.sort_last_added),
            SettingsUtility.getInstance(context).songSortOrder == SortModes.SongModes.SONG_LAST_ADDED,
            AlertItemTheme.DEFAULT
        ) { action ->
            action.selected = true
            SettingsUtility.getInstance(context).songSortOrder = SortModes.SongModes.SONG_LAST_ADDED
            reloadAdapter()
        })
        return alert
    }

    private fun reloadAdapter() {
        viewModel.update()
    }

    override fun addToList(playListId: Long, song: Song) {
        viewModel.addToPlaylist(playListId, arrayOf(song.id).toLongArray())
    }

    override fun onItemClick(view: View, position: Int, item: Song) {
        (safeActivity as MainActivity).viewModel.update(item)
        (safeActivity as MainActivity).viewModel.update(songAdapter.songList)
    }

    override fun onShuffleClick(view: View) {
        Toast.makeText(context, "Shuffle", Toast.LENGTH_LONG).show()
    }

    override fun onSortClick(view: View) {
        dialog.show(activity as AppCompatActivity)
    }

    override fun onPlayAllClick(view: View) {
        Toast.makeText(context, "Play All", Toast.LENGTH_LONG).show()
    }

    override fun onPopupMenuClick(view: View, position: Int, item: Song) {
        powerMenu!!.showAsAnchorRightTop(view)
        viewModel.playLists().observe(this) {
            buildPlaylistMenu(it, item)
        }
    }
}
