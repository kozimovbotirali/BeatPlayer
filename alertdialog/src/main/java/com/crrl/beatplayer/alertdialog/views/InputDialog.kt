/*
 * Copyright 2019 Carlos René Ramos López. All rights reserved.
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
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.crrl.beatplayer.alertdialog.R
import com.crrl.beatplayer.alertdialog.dialogs.AlertItemAction
import com.crrl.beatplayer.alertdialog.listeners.ItemListener
import com.crrl.beatplayer.alertdialog.stylers.AlertItemTheme
import com.crrl.beatplayer.alertdialog.stylers.InputStyle
import com.crrl.beatplayer.alertdialog.utils.Utils.drawRoundRectShape
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.input_dialog_item.view.*
import kotlinx.android.synthetic.main.input_dialog_parent.view.*
import java.util.*

class InputDialog(
    private val title: String,
    private val message: String,
    private val bottomActions: ArrayList<AlertItemAction>,
    private val style: InputStyle,
    private val inputText: String
) : DialogFragment(), ItemListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.BottomSheetAlertTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate base view
        val view = inflater.inflate(R.layout.input_dialog_parent, container, false)

        // Set up view
        initView(view)

        return view
    }

    private fun initView(view: View) {
        view.title.apply {
            if (this@InputDialog.title.isEmpty()) {
                visibility = View.GONE
            } else {
                text = this@InputDialog.title
            }
            setTextColor(style.textColor)
        }

        view.sub_title.apply {
            if (message.isEmpty()) {
                visibility = View.GONE
            } else {
                text = message
            }
            setTextColor(style.textColor)
        }

        inflateActionsView(view.item_container)

        // Configuring View Parent
        val background = drawRoundRectShape(
            view.container.layoutParams.width,
            view.container.layoutParams.height,
            style.backgroundColor
        )

        view.container.background = background
        view.sepTop.setBackgroundColor(style.textColor)
        view.sepMid.setBackgroundColor(style.textColor)

        view.cancel.apply {
            val item = bottomActions[0]
            text = item.title

            updateItem(this, item)

            setOnClickListener {

                item.input = view.text.text.toString()

                dismiss()

                //Add root view
                item.root = view

                //Execute listeners' methods for each case (Kotlin or Java)
                item.action?.invoke(item)
                item.actionListener?.onAlertItemClick(item)
            }
        }

        view.ok.apply {
            val item = bottomActions[1]
            text = item.title

            updateItem(this, item)

            setOnClickListener {

                item.input = view.text.text.toString()
                dismiss()

                //Execute listeners' methods for each case (Kotlin or Java)
                item.action?.invoke(item)
                item.actionListener?.onAlertItemClick(item)
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun inflateActionsView(actionsLayout: LinearLayout) {
        val view = LayoutInflater.from(context).inflate(
            R.layout.input_dialog_item,
            null
        ).apply {
            text.apply {
                hint = inputText
                setTextColor(style.textColor)
                setHintTextColor(style.hintTextColor)
                background =
                    drawRoundRectShape(layoutParams.width, layoutParams.height, style.inputColor)
                requestFocus()
            }
        }
        actionsLayout.addView(view)
        dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    /**
     * This method sets the views style
     * @param view: View
     * @param alertItemAction: AlertItemAction
     */
    override fun updateItem(view: View, alertItemAction: AlertItemAction) {
        val action = view as Button

        // Action text color according to AlertActionStyle
        if (context != null) {
            when (alertItemAction.theme) {
                AlertItemTheme.DEFAULT -> {
                    action.setTextColor(style.textColor)
                }
                AlertItemTheme.CANCEL -> {
                    action.setTextColor(ContextCompat.getColor(context!!, R.color.red))
                }
                AlertItemTheme.ACCEPT -> {
                    action.setTextColor(style.acceptColor)
                }
            }
        }
    }
}
