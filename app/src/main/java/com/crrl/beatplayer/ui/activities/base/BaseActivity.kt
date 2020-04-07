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

package com.crrl.beatplayer.ui.activities.base

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.addFragment
import com.crrl.beatplayer.extensions.getColorByTheme
import com.crrl.beatplayer.ui.activities.SettingsActivity
import com.crrl.beatplayer.ui.fragments.SearchFragment
import com.crrl.beatplayer.utils.PlayerConstants
import com.crrl.beatplayer.utils.SettingsUtility
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem


open class BaseActivity : RequestPermissionActivity() {

    private var currentTheme: String? = null

    private var powerMenu: PowerMenu? = null

    private val onMenuItemClickListener = OnMenuItemClickListener<PowerMenuItem> { position, _ ->
        when (position) {
            0 -> {
            }
            1 -> {
                val options = ActivityOptions.makeSceneTransitionAnimation(this)
                val intent = Intent(this@BaseActivity, SettingsActivity::class.java)
                startActivity(intent, options.toBundle())
            }
        }
        powerMenu!!.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onBackPressed() {
        if (powerMenu != null) {
            if (powerMenu!!.isShowing) {
                powerMenu!!.dismiss()
            } else {
                super.onBackPressed()
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        if (SettingsUtility.getInstance(this).currentTheme != currentTheme) {
            recreateActivity()
        }
    }

    private fun init() {
        currentTheme = SettingsUtility.getInstance(this).currentTheme
        setAppTheme(currentTheme!!)

        powerMenu = initPopUpMenu().setOnMenuItemClickListener(onMenuItemClickListener).build()
    }

    protected open fun recreateActivity(){
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }


    fun back(view: View) {
        onBackPressed()
    }

    fun menu(view: View) {
        powerMenu?.showAsAnchorRightTop(view)
    }

    fun search(view: View) {
        addFragment(
            R.id.nav_host_fragment,
            SearchFragment(),
            PlayerConstants.SONG_DETAIL,
            true
        )
    }

    private fun initPopUpMenu(): PowerMenu.Builder {
        // Build Popup Menu

        return PowerMenu.Builder(this)
            .addItem(PowerMenuItem(getString(R.string.equalizer), false))
            .addItem(PowerMenuItem(getString(R.string.settings), false))
            .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
            .setMenuRadius(this.resources.getDimension(R.dimen.popupMenuRadius))
            .setMenuShadow(10f)
            .setShowBackground(false)
            .setTextColor(getColorByTheme(R.attr.titleTextColor, "titleTextColor"))
            .setTextGravity(Gravity.CENTER)
            .setTextSize(16)
            .setTextTypeface(Typeface.createFromAsset(assets, "fonts/product_sans_regular.ttf"))
            .setSelectedTextColor(getColorByTheme(R.attr.colorAccent, "colorAccent"))
            .setMenuColor(getColorByTheme(R.attr.colorPrimarySecondary2, "colorPrimarySecondary2"))
            .setSelectedMenuColor(
                getColorByTheme(
                    R.attr.colorPrimarySecondary,
                    "colorPrimarySecondary"
                )
            )
    }

    private fun setAppTheme(current_theme: String) {
        when (current_theme) {
            PlayerConstants.DARK_THEME -> setTheme(R.style.AppTheme_Dark)
            PlayerConstants.LIGHT_THEME -> setTheme(R.style.AppTheme_Light)
            else -> setTheme(R.style.AppTheme_Auto)
        }
    }

    open fun triggerRebirth(context: Context) {
        val packageManager = context.packageManager
        val intent = packageManager.getLaunchIntentForPackage(context.packageName)
        val componentName = intent!!.component
        val mainIntent = Intent.makeRestartActivityTask(componentName)
        context.startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }
}

