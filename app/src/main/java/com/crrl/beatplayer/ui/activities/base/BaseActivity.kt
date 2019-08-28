package com.crrl.beatplayer.ui.activities.base

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.crrl.beatplayer.R
import com.crrl.beatplayer.extensions.addFragment
import com.crrl.beatplayer.extensions.getColorByTheme
import com.crrl.beatplayer.ui.activities.SettingsActivity
import com.crrl.beatplayer.ui.fragments.SearchFragment
import com.crrl.beatplayer.utils.PlayerConstants
import com.skydoves.powermenu.MenuAnimation
import com.skydoves.powermenu.OnMenuItemClickListener
import com.skydoves.powermenu.PowerMenu
import com.skydoves.powermenu.PowerMenuItem


open class BaseActivity : RequestPermissionActivity() {

    private var powerMenu: PowerMenu? = null

    private val onMenuItemClickListener = OnMenuItemClickListener<PowerMenuItem> { position, item ->
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

    private fun init() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        powerMenu = initPopUpMenu().setOnMenuItemClickListener(onMenuItemClickListener).build()
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
            .setTextTypeface(Typeface.createFromAsset(assets, "fonts/rubik.ttf"))
            .setSelectedTextColor(getColorByTheme(R.attr.colorAccent, "colorAccent"))
            .setMenuColor(getColorByTheme(R.attr.colorPrimarySecondary, "colorPrimarySecondary"))
            .setSelectedMenuColor(
                getColorByTheme(
                    R.attr.colorPrimarySecondary,
                    "colorPrimarySecondary"
                )
            )
    }
}

