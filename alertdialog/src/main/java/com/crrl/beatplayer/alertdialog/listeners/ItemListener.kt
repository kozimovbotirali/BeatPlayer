package com.crrl.beatplayer.alertdialog.listeners

import android.view.View
import com.crrl.beatplayer.alertdialog.dialogs.AlertItemAction

interface ItemListener {
    fun updateItem(view: View, alertItemAction: AlertItemAction)
}