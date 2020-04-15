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

package com.crrl.beatplayer.ui.activities

import android.os.Bundle
import android.view.View
import com.crrl.beatplayer.R
import com.crrl.alertdialog.AlertDialog
import com.crrl.alertdialog.dialogs.AlertItemAction
import com.crrl.alertdialog.stylers.AlertItemStyle
import com.crrl.alertdialog.stylers.AlertItemTheme
import com.crrl.alertdialog.stylers.AlertType
import com.crrl.beatplayer.extensions.getColorByTheme
import com.crrl.beatplayer.ui.activities.base.BaseActivity
import com.crrl.beatplayer.utils.PlayerConstants
import com.crrl.beatplayer.utils.SettingsUtility

class SettingsActivity : BaseActivity() {

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        init()
    }

    private fun init() {
        dialog = buildThemeDialog()
    }

    private fun buildThemeDialog(): AlertDialog {
        val style = AlertItemStyle()
        style.apply {
            textColor = getColorByTheme(R.attr.titleTextColor, "titleTextColor")
            selectedTextColor = getColorByTheme(R.attr.colorAccent, "colorAccent")
            backgroundColor =
                getColorByTheme(R.attr.colorPrimarySecondary2, "colorPrimarySecondary2")
        }
        return AlertDialog(
            getString(R.string.theme_title),
            getString(R.string.theme_description),
            style,
            AlertType.BOTTOM_SHEET
        ).apply {
            addItem(AlertItemAction(
                getString(R.string.default_theme),
                SettingsUtility.getInstance(applicationContext).currentTheme == PlayerConstants.AUTO_THEME,
                AlertItemTheme.DEFAULT
            ) {
                it.selected = true
                SettingsUtility.getInstance(applicationContext).currentTheme =
                    PlayerConstants.AUTO_THEME
                recreateActivity()
            })
            addItem(AlertItemAction(
                getString(R.string.light_theme),
                SettingsUtility.getInstance(applicationContext).currentTheme == PlayerConstants.LIGHT_THEME,
                AlertItemTheme.DEFAULT
            ) {
                it.selected = true
                SettingsUtility.getInstance(applicationContext).currentTheme =
                    PlayerConstants.LIGHT_THEME
                recreateActivity()
            })
            addItem(AlertItemAction(
                getString(R.string.dark_theme),
                SettingsUtility.getInstance(applicationContext).currentTheme == PlayerConstants.DARK_THEME,
                AlertItemTheme.DEFAULT
            ) {
                it.selected = true
                SettingsUtility.getInstance(applicationContext).currentTheme =
                    PlayerConstants.DARK_THEME
                recreateActivity()
            })
        }
    }

    fun showThemes(view: View) {
        dialog.show(this)
    }
}
