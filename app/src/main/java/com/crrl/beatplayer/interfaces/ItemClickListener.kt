package com.crrl.beatplayer.interfaces

import android.view.View

interface ItemClickListener<T> {
    fun onItemClick(view: View, position: Int, item: T)
    fun onPopupMenuClick(view: View, position: Int, item: T)
    fun onShuffleClick(view: View)
    fun onSortClick(view: View)
    fun onPlayAllClick(view: View)
}
