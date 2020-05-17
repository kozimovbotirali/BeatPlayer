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

package com.crrl.beatplayer.ui.widgets.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.crrl.beatplayer.R
import com.crrl.beatplayer.interfaces.ItemListener
import com.crrl.beatplayer.ui.widgets.actions.AlertItemAction
import com.crrl.beatplayer.ui.widgets.stylers.AlertItemStyle
import com.crrl.beatplayer.ui.widgets.stylers.AlertItemTheme
import com.crrl.beatplayer.utils.GeneralUtils.drawRoundRectShape
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.parent_dialog_layout.view.*

class DialogAlert(
    private val title: String,
    private val message: String,
    private val itemList: ArrayList<AlertItemAction>,
    private val style: AlertItemStyle
) : BottomSheetDialogFragment(), ItemListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetAlertTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate base view
        val view = inflater.inflate(R.layout.parent_dialog_layout, container, false)

        // Set up view
        initView(view)

        return view
    }

    private fun initView(view: View) {
        with(view) {
            title.apply {
                if (this@DialogAlert.title.isEmpty()) {
                    visibility = View.GONE
                } else {
                    text = this@DialogAlert.title
                }
                setTextColor(style.textColor)
            }

            sub_title.apply {
                if (message.isEmpty()) {
                    visibility = View.GONE
                } else {
                    text = message
                }
                setTextColor(style.textColor)
            }

            // Configuring View Parent
            val background = drawRoundRectShape(
                container.layoutParams.width,
                container.layoutParams.height,
                style.backgroundColor,
                style.cornerRadius
            )

            container.background = background

            view.cancel.apply {
                val item =
                    AlertItemAction(getString(R.string.cancel), false, AlertItemTheme.DEFAULT) {}
                text = item.title

                updateItem(this, item)

                setOnClickListener {
                    dismiss()
                    item.root = view
                    item.action?.invoke(item)
                }
            }

            view.ok.apply {
                val item = itemList[0]
                text = item.title

                updateItem(this, item)
                setOnClickListener {
                    dismiss()
                    item.action?.invoke(item)
                }
            }
            container.background = background
        }
    }


    /**
     * This method sets the views style
     * @param view: View
     * @param alertItemAction: AlertItemAction
     */
    override fun updateItem(view: View, alertItemAction: AlertItemAction) {
        val action = view as Button
        if (context != null) {
            when (alertItemAction.theme) {
                AlertItemTheme.DEFAULT -> {
                    if (alertItemAction.selected) {
                        action.setTextColor(style.selectedTextColor)
                    } else {
                        action.setTextColor(style.textColor)
                    }
                }
                AlertItemTheme.CANCEL -> {
                    action.setTextColor(style.backgroundColor)
                }
                AlertItemTheme.ACCEPT -> {
                    action.setTextColor(style.selectedTextColor)
                }
            }
        }
    }
}