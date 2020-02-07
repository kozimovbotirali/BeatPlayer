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

import android.graphics.RectF
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.crrl.beatplayer.alertdialog.R
import com.crrl.beatplayer.alertdialog.dialogs.AlertItemAction
import com.crrl.beatplayer.alertdialog.listeners.ItemListener
import com.crrl.beatplayer.alertdialog.stylers.AlertItemStyle
import com.crrl.beatplayer.alertdialog.stylers.AlertItemTheme
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetAlert(
    private val title: String,
    private val message: String,
    private val actions: ArrayList<AlertItemAction>,
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

    private fun initView(view: View?) {
        //Finding views
        val titleView = view!!.findViewById<TextView>(R.id.title)
        val subTitleView = view.findViewById<TextView>(R.id.sub_title)
        val cancel = view.findViewById<Button>(R.id.cancel)
        val container = view.findViewById<LinearLayout>(R.id.container)

        titleView.apply {
            if (title.isEmpty()) {
                visibility = View.GONE
            } else {
                text = title
            }
            setTextColor(style.textColor)
        }

        subTitleView.apply {
            if (message.isEmpty()) {
                visibility = View.GONE
            } else {
                text = message
            }
            setTextColor(style.textColor)
        }

        // Configuring View Parent
        val back = drawRoundRectShape(
            container.layoutParams.width,
            container.layoutParams.height,
            style.backgroundColor
        )

        container.background = back

        cancel.apply {
            setOnClickListener { dismiss() }
            this.background = back
            setTextColor(ContextCompat.getColor(view.context, R.color.red))
        }
        // Inflate action views
        inflateActionsView(view.findViewById(R.id.item_container), actions)
    }

    private fun inflateActionsView(actionsLayout: LinearLayout, items: ArrayList<AlertItemAction>) {
        for (item in items) {

            // Finding Views
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_item, null)
            val action = view.findViewById<Button>(R.id.action)
            val indicator = view.findViewById<View>(R.id.indicator)

            action.text = item.title

            // Click listener for action.
            action.setOnClickListener {
                dismiss()

                //Store selectedState
                val oldState = item.selected

                //Add root view
                item.root = view

                //Execute listeners' methods for each case (Kotlin on Java)
                item.action?.invoke(item)
                item.actionListener?.onAlertItemClick(item)

                // Check if selected state changed
                if (oldState != item.selected) {
                    //Clean Selection State
                    cleanSelection(items, item)
                    //Update Item Style
                    updateItem(view, item)
                }
            }
            //Set style first time
            updateItem(view, item)

            //Set separator view background
            indicator.setBackgroundColor(style.textColor)

            // Add child to its parent
            actionsLayout.addView(view)
        }
    }

    /**
     * This method clears the selection states for each item in the array.
     * @param items: java.util.ArrayList<AlertItemAction> All the items that will be modified
     * @param currentItem: AlertItemAction to save current item state
     */
    private fun cleanSelection(
        items: java.util.ArrayList<AlertItemAction>,
        currentItem: AlertItemAction
    ) {
        for (item in items) {
            if (item != currentItem) item.selected = false
        }
    }

    /**
     * This method sets the views style
     * @param view: View
     * @param alertItemAction: AlertItemAction
     */
    override fun updateItem(view: View, alertItemAction: AlertItemAction) {
        val action = view.findViewById<Button>(R.id.action)

        // Action text color according to AlertActionStyle
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
                    action.setTextColor(ContextCompat.getColor(context!!, R.color.red))
                }
                AlertItemTheme.ACCEPT -> {
                    action.setTextColor(ContextCompat.getColor(context!!, R.color.green))
                }
            }
        }
    }

    /**
     * This method draws a round rect shape.
     * @param width: int
     * @param height: int
     * @param color: int
     * @return ShapeDrawable
     */
    private fun drawRoundRectShape(width: Int, height: Int, color: Int): ShapeDrawable {
        val r = floatArrayOf(20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f, 20.0f)
        val oval = ShapeDrawable(RoundRectShape(r, RectF(), r))
        oval.intrinsicHeight = height
        oval.intrinsicWidth = width
        oval.paint.color = color
        return oval
    }
}