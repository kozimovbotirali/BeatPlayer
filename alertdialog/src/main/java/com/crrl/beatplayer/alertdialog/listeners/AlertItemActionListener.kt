package com.crrl.beatplayer.alertdialog.listeners

import com.crrl.beatplayer.alertdialog.dialogs.AlertItemAction

interface AlertItemActionListener {
    fun onAlertItemClick(action: AlertItemAction)
}