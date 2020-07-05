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

package com.crrl.beatplayer.alertdialog.views

import android.os.Bundle
import android.view.View
import android.widget.Button
import com.crrl.beatplayer.alertdialog.R
import com.crrl.beatplayer.alertdialog.actions.AlertItemAction
import com.crrl.beatplayer.alertdialog.enums.AlertItemTheme
import com.crrl.beatplayer.alertdialog.extensions.addOnWindowFocusChangeListener
import com.crrl.beatplayer.alertdialog.stylers.AlertItemStyle
import com.crrl.beatplayer.alertdialog.stylers.base.ItemStyle
import com.crrl.beatplayer.alertdialog.utils.ViewUtils.drawRoundRectShape
import com.crrl.beatplayer.alertdialog.views.base.DialogFragmentBase
import kotlinx.android.synthetic.main.parent_dialog_layout.view.*

class DialogAlert : DialogFragmentBase() {

    companion object {
        fun newInstance(
            title: String,
            message: String,
            actions: List<AlertItemAction>,
            style: ItemStyle
        ): DialogFragmentBase {
            return DialogAlert().apply {
                setArguments(title, message, actions, style as AlertItemStyle)
            }
        }
    }

    private lateinit var style: AlertItemStyle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        addOnWindowFocusChangeListener {
            if (!it) dismiss()
        }
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

            val background = drawRoundRectShape(
                container.layoutParams.width,
                container.layoutParams.height,
                style.backgroundColor,
                style.cornerRadius
            )

            container.background = background
            view.sepMid.setBackgroundColor(style.textColor)

            view.cancel.apply {
                val item =
                    AlertItemAction(getString(R.string.cancel), false, AlertItemTheme.DEFAULT) {}
                text = item.title

                updateItem(this, item)

                setOnClickListener {
                    dismiss()
                    item.root = view
                    item.action.invoke(item)
                }
            }

            view.ok.apply {
                val item = itemList[0]
                text = item.title

                updateItem(this, item)
                setOnClickListener {
                    dismiss()
                    item.action.invoke(item)
                }
            }
            container.background = background
        }
    }

    fun setArguments(
        title: String,
        message: String,
        itemList: List<AlertItemAction>,
        style: AlertItemStyle
    ) {
        this.title = title
        this.message = message
        this.itemList = itemList
        this.style = style
    }

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