package com.crrl.beatplayer.alertdialog.dialogs

import android.view.View
import com.crrl.beatplayer.alertdialog.listeners.AlertItemActionListener
import com.crrl.beatplayer.alertdialog.stylers.AlertItemTheme

class AlertItemAction {
    var title: String
    var action: ((AlertItemAction) -> Unit)?
    var actionListener: AlertItemActionListener?
    var theme: AlertItemTheme? = AlertItemTheme.DEFAULT
    var selected: Boolean
    var input: String? = null
    var root: View? = null

    constructor(
        title: String,
        selected: Boolean,
        theme: AlertItemTheme? = AlertItemTheme.DEFAULT,
        action: (AlertItemAction) -> Unit
    ) {
        this.title = title
        this.selected = selected
        this.action = action
        this.actionListener = null
        this.theme = theme
    }

    constructor(
        title: String,
        selected: Boolean,
        theme: AlertItemTheme,
        actionListener: AlertItemActionListener
    ) {
        this.title = title
        this.selected = selected
        this.actionListener = actionListener
        this.action = null
        this.theme = theme
    }
}