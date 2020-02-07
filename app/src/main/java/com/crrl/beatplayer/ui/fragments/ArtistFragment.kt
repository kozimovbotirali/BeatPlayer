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
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.alertdialog.AlertDialog
import com.crrl.beatplayer.alertdialog.dialogs.AlertItemAction
import com.crrl.beatplayer.alertdialog.stylers.AlertItemStyle
import com.crrl.beatplayer.alertdialog.stylers.AlertItemTheme
import com.crrl.beatplayer.alertdialog.stylers.AlertType
import com.crrl.beatplayer.extensions.addFragment
import com.crrl.beatplayer.extensions.getColorByTheme
import com.crrl.beatplayer.extensions.observe
import com.crrl.beatplayer.extensions.safeActivity
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.ui.adapters.ArtistAdapter
import com.crrl.beatplayer.ui.fragments.base.BaseFragment
import com.crrl.beatplayer.ui.viewmodels.ArtistViewModel
import com.crrl.beatplayer.utils.GeneralUtils
import com.crrl.beatplayer.utils.PlayerConstants
import com.crrl.beatplayer.utils.SettingsUtility
import com.crrl.beatplayer.utils.SortModes
import kotlinx.android.synthetic.main.fragment_artist.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ArtistFragment : BaseFragment<Artist>() {

    private val viewModel: ArtistViewModel by viewModel { parametersOf(safeActivity) }
    private lateinit var artistAdapter: ArtistAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_artist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
    }

    private fun init(view: View) {
        val sc = if (GeneralUtils.getRotation(safeActivity) == GeneralUtils.VERTICAL) 2 else 5

        artistAdapter = ArtistAdapter(context).apply {
            showHeader = true
            itemClickListener = this@ArtistFragment
            spanCount = sc
        }

        view.apply {
            artist_list.apply {
                layoutManager = GridLayoutManager(context!!, sc).apply {
                    spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (position == 0) sc else 1
                        }
                    }
                }
                adapter = artistAdapter
            }
        }

        dialog = buildSortModesDialog()

        viewModel.getArtists()!!.observe(this) { list ->
            artistAdapter.updateDataSet(list)
        }
    }

    private fun buildSortModesDialog(): AlertDialog {
        val style = AlertItemStyle()
        style.apply {
            textColor = activity?.getColorByTheme(R.attr.titleTextColor, "titleTextColor")!!
            selectedTextColor = activity?.getColorByTheme(R.attr.colorAccent, "colorAccent")!!
            backgroundColor =
                activity?.getColorByTheme(R.attr.colorPrimarySecondary2, "colorPrimarySecondary2")!!
        }
        return AlertDialog(
            getString(R.string.sort_title),
            getString(R.string.sort_msg),
            style,
            AlertType.BOTTOM_SHEET
        ).apply {
            addItem(AlertItemAction(
                context!!.getString(R.string.sort_default),
                SettingsUtility.getInstance(context).artistSortOrder == SortModes.ArtistModes.ARTIST_DEFAULT,
                AlertItemTheme.DEFAULT
            ) { action ->
                action.selected = true
                SettingsUtility.getInstance(context).artistSortOrder =
                    SortModes.ArtistModes.ARTIST_DEFAULT
                reloadAdapter()
            })
            addItem(AlertItemAction(
                context!!.getString(R.string.sort_az),
                SettingsUtility.getInstance(context).artistSortOrder == SortModes.ArtistModes.ARTIST_A_Z,
                AlertItemTheme.DEFAULT
            ) { action ->
                action.selected = true
                SettingsUtility.getInstance(context).artistSortOrder =
                    SortModes.ArtistModes.ARTIST_A_Z
                reloadAdapter()
            })
            addItem(AlertItemAction(
                context!!.getString(R.string.sort_za),
                SettingsUtility.getInstance(context).artistSortOrder == SortModes.ArtistModes.ARTIST_Z_A,
                AlertItemTheme.DEFAULT
            ) { action ->
                action.selected = true
                SettingsUtility.getInstance(context).artistSortOrder =
                    SortModes.ArtistModes.ARTIST_Z_A
                reloadAdapter()
            })
        }
    }

    private fun reloadAdapter() {
        viewModel.update()
    }

    override fun onItemClick(view: View, position: Int, item: Artist) {
        val extras = Bundle()
        extras.putString(PlayerConstants.ARTIST_KEY, item.toString())
        activity!!.addFragment(
            R.id.nav_host_fragment,
            ArtistDetailFragment(),
            PlayerConstants.ARTIST_DETAIL,
            true,
            extras
        )
    }

    override fun onSortClick(view: View) {
        dialog.show(activity as AppCompatActivity)
    }
}
