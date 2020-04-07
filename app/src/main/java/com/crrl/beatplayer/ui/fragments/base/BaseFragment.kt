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

package com.crrl.beatplayer.ui.fragments.base

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast.LENGTH_LONG
import android.widget.Toast.LENGTH_SHORT
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.crrl.beatplayer.R
import com.crrl.beatplayer.alertdialog.AlertDialog
import com.crrl.beatplayer.alertdialog.dialogs.AlertItemAction
import com.crrl.beatplayer.alertdialog.stylers.AlertItemStyle
import com.crrl.beatplayer.alertdialog.stylers.AlertItemTheme
import com.crrl.beatplayer.alertdialog.stylers.AlertType
import com.crrl.beatplayer.alertdialog.stylers.InputStyle
import com.crrl.beatplayer.extensions.getColorByTheme
import com.crrl.beatplayer.extensions.safeActivity
import com.crrl.beatplayer.extensions.toast
import com.crrl.beatplayer.interfaces.ItemClickListener
import com.crrl.beatplayer.models.MediaItem
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.repository.PlaylistRepository
import com.crrl.beatplayer.repository.SongsRepository
import com.crrl.beatplayer.ui.activities.MainActivity
import com.crrl.beatplayer.ui.fragments.PlaylistDetailFragment
import com.crrl.beatplayer.utils.GeneralUtils.addZeros
import com.crrl.beatplayer.utils.PlayerConstants
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem


open class BaseFragment<T : MediaItem> : Fragment(), ItemClickListener<T> {

    private lateinit var currentItemList: List<T>
    protected lateinit var dialog: AlertDialog
    private lateinit var alertPlaylists: AlertDialog
    private var currentItem: T? = null

    protected var powerMenu: PowerMenu? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        init()
    }

    private fun init() {
        Thread {
            powerMenu = initPopUpMenu().setOnMenuItemClickListener(onMenuItemClickListener).build()
        }.start()
    }

    protected fun buildPlaylistMenu(playlists: List<Playlist>, song: Song) {
        val style = AlertItemStyle().apply {
            textColor = activity?.getColorByTheme(R.attr.titleTextColor, "titleTextColor")!!
            selectedTextColor = activity?.getColorByTheme(R.attr.colorAccent, "colorAccent")!!
            backgroundColor =
                activity?.getColorByTheme(R.attr.colorPrimarySecondary, "colorPrimarySecondary")!!
        }
        val alert = AlertDialog(
            getString(R.string.playlists),
            getString(R.string.choose_playlist),
            style,
            AlertType.BOTTOM_SHEET
        ).apply {
            playlists.forEach { playlist ->
                addItem(AlertItemAction(playlist.name, false) {
                    addToList(playlist.id, song)
                })
            }
            addItem(
                AlertItemAction(getString(R.string.new_playlist), false, AlertItemTheme.ACCEPT) {
                    createPlayList(song)
                })
        }
        alertPlaylists = alert
    }

    private fun createDialog(song: Song): AlertDialog {
        val style = InputStyle(
            safeActivity.getColorByTheme(R.attr.colorPrimarySecondary, "colorPrimarySecondary"),
            safeActivity.getColorByTheme(R.attr.colorPrimarySecondary2, "colorPrimarySecondary2"),
            safeActivity.getColorByTheme(R.attr.titleTextColor, "titleTextColor"),
            safeActivity.getColorByTheme(R.attr.bodyTextColor, "bodyTextColor"),
            safeActivity.getColorByTheme(R.attr.colorAccent, "colorAccent"),
            "${safeActivity.getString(R.string.playlist)} ${addZeros(
                PlaylistRepository.getInstance(
                    context
                ).getPlayListsCount() + 1
            )}"
        )
        return AlertDialog(
            getString(R.string.new_playlist),
            getString(R.string.create_playlist),
            style,
            AlertType.INPUT,
            getString(R.string.input_hint)
        ).apply {
            addItem(AlertItemAction("Cancel", false, AlertItemTheme.CANCEL) {
            })
            addItem(AlertItemAction("OK", false, AlertItemTheme.ACCEPT) {
                val id = PlaylistRepository.getInstance(context)
                    .createPlaylist(it.input, longArrayOf(song.id))
                if (id != -1L) {
                    safeActivity.toast(getString(R.string.playlist_added_success), LENGTH_SHORT)
                } else {
                    safeActivity.toast(
                        "${getString(R.string.playlist_added_error)} ${it.input}",
                        LENGTH_LONG
                    )
                }
            })
        }
    }

    private fun initPopUpMenu(): PowerMenu.Builder {
        return PowerMenu.Builder(context).apply {
            addItem(PowerMenuItem(getString(R.string.play), false))
            if (tag != PlayerConstants.PLAY_LIST_DETAIL)
                addItem(PowerMenuItem(getString(R.string.add), false))

            addItem(PowerMenuItem(getString(R.string.share), false))
            setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
            setMenuRadius(resources.getDimension(R.dimen.popupMenuRadius))
            setOnBackgroundClickListener { powerMenu!!.dismiss() }
            setMenuShadow(5f)
            setShowBackground(false)
            setTextColor(safeActivity.getColorByTheme(R.attr.titleTextColor, "titleTextColor"))
            setTextGravity(Gravity.CENTER)
            setTextSize(16)
            setTextTypeface(
                Typeface.createFromAsset(
                    context!!.assets,
                    "fonts/product_sans_regular.ttf"
                )
            )
            setSelectedTextColor(safeActivity.getColorByTheme(R.attr.colorAccent, "colorAccent"))
            setMenuColor(
                safeActivity.getColorByTheme(R.attr.colorPrimarySecondary, "colorPrimarySecondary")
            )
            setSelectedMenuColor(
                safeActivity.getColorByTheme(R.attr.colorPrimarySecondary, "colorPrimarySecondary")
            )
            if (tag == PlayerConstants.PLAY_LIST_DETAIL)
                addItem(PowerMenuItem(getString(R.string.remove), false))
            if (tag != PlayerConstants.PLAY_LIST_DETAIL) {
                addItem(PowerMenuItem(getString(R.string.delete), false))
            }
        }

    }

    private fun createConfDialog(song: Song): AlertDialog {
        val style = AlertItemStyle().apply {
            textColor = activity?.getColorByTheme(R.attr.titleTextColor, "titleTextColor")!!
            selectedTextColor = activity?.getColorByTheme(R.attr.colorAccent, "colorAccent")!!
            backgroundColor =
                activity?.getColorByTheme(R.attr.colorPrimarySecondary2, "colorPrimarySecondary2")!!
        }

        return AlertDialog(
            getString(R.string.delete_conf_title),
            "${getString(R.string.delete_conf)} \"${song.title}\"",
            style,
            AlertType.BOTTOM_SHEET
        ).apply {
            addItem(AlertItemAction(getString(R.string.delete), false, AlertItemTheme.ACCEPT) {
                val resp =
                    SongsRepository(context).deleteTracks(longArrayOf(song.id))
                if (resp > 0)
                    activity.toast(getString(R.string.deleted_ok), LENGTH_SHORT)
                else
                    activity.toast(getString(R.string.deleted_err), LENGTH_SHORT)
            })
        }
    }

    private fun createPlayList(song: Song) {
        createDialog(song).show(activity as AppCompatActivity)
    }

    private val onMenuItemClickListener = OnMenuItemClickListener<PowerMenuItem> { position, _ ->
        when (position) {
            0 -> {
                (safeActivity as MainActivity).viewModel.update(currentItem as Song)
                (safeActivity as MainActivity).viewModel.update(currentItemList.filterIsInstance<Song>())
            }
            1 -> {
                alertPlaylists.show(safeActivity as AppCompatActivity)
            }
            2 -> {
                if (this is PlaylistDetailFragment) {
                    removeFromList(binding.playlist!!.id, currentItem)
                } else {
                    //Share
                }
            }
            3 -> {
                createConfDialog(currentItem as Song).show(safeActivity as AppCompatActivity)
            }
        }
        powerMenu!!.dismiss()
    }

    open fun onBackPressed(): Boolean {
        return if (powerMenu != null) {
            if (powerMenu!!.isShowing) {
                powerMenu!!.dismiss()
                false
            } else {
                true
            }
        } else {
            true
        }
    }

    open fun addToList(playListId: Long, song: Song) {}
    open fun removeFromList(playListId: Long, item: T?) {}
    override fun onItemClick(view: View, position: Int, item: T) {}
    override fun onShuffleClick(view: View) {}
    override fun onSortClick(view: View) {}
    override fun onPlayAllClick(view: View) {}

    override fun onPopupMenuClick(view: View, position: Int, item: T, itemList: List<T>) {
        currentItem = item
        currentItemList = itemList
    }
}

