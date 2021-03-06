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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import com.crrl.beatplayer.alertdialog.R
import com.crrl.beatplayer.alertdialog.actions.AlertItemAction
import com.crrl.beatplayer.alertdialog.enums.AlertItemTheme
import com.crrl.beatplayer.alertdialog.extensions.addOnWindowFocusChangeListener
import com.crrl.beatplayer.alertdialog.extensions.setMargins
import com.crrl.beatplayer.alertdialog.stylers.InputStyle
import com.crrl.beatplayer.alertdialog.stylers.base.ItemStyle
import com.crrl.beatplayer.alertdialog.utils.ViewUtils.dip2px
import com.crrl.beatplayer.alertdialog.utils.ViewUtils.drawRoundRectShape
import com.crrl.beatplayer.alertdialog.views.base.DialogFragmentBase
import kotlinx.android.synthetic.main.input_dialog_item.view.*
import kotlinx.android.synthetic.main.parent_dialog_layout.view.*

class InputDialog : DialogFragmentBase() {

    companion object {
        fun newInstance(
            title: String,
            message: String,
            actions: List<AlertItemAction>,
            style: ItemStyle,
            inputText: String
        ): DialogFragmentBase {
            return InputDialog().apply {
                setArguments(title, message, actions, style as InputStyle, inputText)
            }
        }
    }

    private lateinit var style: InputStyle
    private lateinit var inputText: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        addOnWindowFocusChangeListener {
            if (!it) dismiss()
        }
    }

    private fun initView(view: View) {
        view.apply {
            itemScroll.apply {
                setMargins(left = dip2px(context, 12), right = dip2px(context, 12))
                setBackgroundResource(R.drawable.search_text_view_frame)
                clipToOutline = true
            }

            title.apply {
                if (this@InputDialog.title.isEmpty()) {
                    visibility = View.GONE
                } else {
                    text = this@InputDialog.title
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
        }

        inflateActionsView(view.item_container)

        val background = drawRoundRectShape(
            view.container.layoutParams.width,
            view.container.layoutParams.height,
            style.backgroundColor,
            style.cornerRadius
        )

        view.container.background = background
        view.sepMid.setBackgroundColor(style.textColor)

        view.cancel.apply {
            val item = itemList[0]
            text = item.title

            updateItem(this, item)

            setOnClickListener {
                item.input = view.text.text.toString()

                dismiss()

                item.root = view
                item.action.invoke(item)
            }
        }

        view.ok.apply {
            val item = itemList[1]
            text = item.title

            updateItem(this, item)

            setOnClickListener {
                item.input = view.text.text.toString()

                dismiss()

                item.action.invoke(item)
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun inflateActionsView(actionsLayout: LinearLayout) {
        val view = LayoutInflater.from(context).inflate(R.layout.input_dialog_item, null).apply {
            text.apply {
                hint = inputText
                setTextColor(style.textColor)
                setHintTextColor(style.hintTextColor)
                background = drawRoundRectShape(
                    layoutParams.width,
                    layoutParams.height,
                    style.inputColor
                )
                requestFocus()
                setText(style.text)
                selectAll()
            }
        }
        actionsLayout.addView(view)
        dialog?.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    fun setArguments(
        title: String,
        message: String,
        itemList: List<AlertItemAction>,
        style: InputStyle,
        inputText: String
    ) {
        this.title = title
        this.message = message
        this.itemList = itemList
        this.style = style
        this.inputText = inputText
    }

    override fun updateItem(view: View, alertItemAction: AlertItemAction) {
        val action = view as Button

        context ?: return

        when (alertItemAction.theme) {
            AlertItemTheme.DEFAULT -> {
                action.setTextColor(style.hintTextColor)
            }
            AlertItemTheme.CANCEL -> {
                action.setTextColor(context!!.getColor(R.color.red))
            }
            AlertItemTheme.ACCEPT -> {
                action.setTextColor(style.acceptColor)
            }
        }
    }
}
