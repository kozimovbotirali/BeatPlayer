package com.crrl.beatplayer.extensions

import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.ui.modelview.PlaylistAdapter

fun PlaylistAdapter?.dataChanged(newList: List<Playlist>): Boolean {
    this ?: return false
    when {
        newList.size > playlists.size -> {
            val pos = findElementChanged(newList)
            if (pos != -1) {
                playlists = newList
                notifyItemInserted(pos)
                return true
            }
        }
        newList.size < playlists.size -> {
            val pos = findElementChanged(newList)
            if (pos != -1) {
                playlists = newList
                notifyItemRemoved(pos)
                return true
            }
        }
        else -> {
            newList.forEachIndexed { index, playlist ->
                if (playlists.indexOf(playlist) == -1) {
                    playlists = newList
                    notifyItemChanged(index)
                    return true
                }
            }
            newList.forEachIndexed { index, t ->
                if (t != playlists[index]) {
                    playlists = newList
                    notifyItemMoved(playlists.indexOf(t), index)
                    return true
                }
            }
        }
    }
    return false
}

private fun PlaylistAdapter?.findElementChanged(newList: List<Playlist>): Int {
    this ?: return -1
    if (newList.size > playlists.size) {
        newList.forEachIndexed { index, playlist ->
            if (playlists.indexOf(playlist) == -1) return index
        }
    }
    if (newList.size < playlists.size) {
        playlists.forEachIndexed { index, playlist ->
            if (newList.indexOf(playlist) == -1) return index
        }
    }
    return -1
}