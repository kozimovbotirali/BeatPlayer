package com.crrl.beatplayer.extensions

import android.util.Log
import com.crrl.beatplayer.models.Folder
import com.crrl.beatplayer.models.MediaItem
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.modelview.FolderAdapter
import com.crrl.beatplayer.ui.modelview.PlaylistAdapter
import com.crrl.beatplayer.ui.modelview.SongAdapter

fun PlaylistAdapter?.dataChanged(newList: List<Playlist>): Boolean {
    this ?: return false
    when {
        newList.size > playlists.size -> {
            findElementChanged(playlists, newList).forEach { pos ->
                if (pos != -1) {
                    if ((newList.size - playlists.size) == 1) {
                        playlists.add(pos, newList[pos])
                    } else {
                        playlists = newList.toMutableList()
                    }
                    notifyItemInserted(pos)
                    return true
                }
            }
        }
        newList.size < playlists.size -> {
            findElementChanged(playlists, newList).forEach { pos ->
                if (pos != -1) {
                    playlists.removeAt(pos)
                    notifyItemRemoved(pos)
                    return true
                }
            }
        }
        else -> {
            newList.forEachIndexed { index, playlist ->
                if (playlists.indexOf(playlist) == -1) {
                    playlists = newList.toMutableList()
                    notifyItemChanged(index)
                    return true
                }
            }

            newList.forEachIndexed { index, t ->
                if (t != playlists[index]) {
                    playlists = newList.toMutableList()
                    notifyItemMoved(playlists.indexOf(t), index)
                    return true
                }
            }
        }
    }
    return false
}

fun SongAdapter?.dataChanged(newList: List<Song>): Boolean {
    this ?: return false
    when {
        newList.size > songList.size -> {
            val list = findElementChanged(songList, newList)
            songList = newList.toMutableList()
            list.forEach { pos ->
                if (pos != -1) {
                    songList = newList.toMutableList()
                    notifyItemInserted(pos)
                    return true
                }
            }
        }
        newList.size < songList.size -> {
            findElementChanged(songList, newList).forEach { pos ->
                if (pos != -1) {
                    notifyItemRemoved(pos)
                    songList.removeAt(pos)
                    return true
                }
            }
        }
        else -> {
            newList.forEach {
                if (songList.indexOf(it) == -1) {
                    songList = newList.toMutableList()
                    notifyDataSetChanged()
                    return true
                }
            }

            newList.forEachIndexed { index, t ->
                if (t.id != songList[index].id) {
                    songList = newList.toMutableList()
                    notifyDataSetChanged()
                    return true
                }
            }
        }
    }
    return false
}

fun FolderAdapter?.dataChanged(newList: List<Folder>): Boolean {
    this ?: return false
    when {
        newList.size > folderList.size -> {
            findElementChanged(folderList, newList).forEach { pos ->
                if (pos != -1) {
                    if ((newList.size - folderList.size) == 1) {
                        folderList.add(pos, newList[pos])
                    } else {
                        folderList = newList.toMutableList()
                    }
                    notifyItemInserted(pos)
                    Log.println(Log.DEBUG, "Dev", "Changed 1")
                    return true
                }
            }
        }
        newList.size < folderList.size -> {
            findElementChanged(folderList, newList).forEach { pos ->
                if (pos != -1) {
                    notifyItemRemoved(pos)
                    folderList.removeAt(pos)
                    Log.println(Log.DEBUG, "Dev", "Changed 2")
                    return true
                }
            }

        }
        else -> {
            newList.forEachIndexed { i, folder ->
                if (folder.id != folderList[i].id) {
                    folderList = newList.toMutableList()
                    notifyDataSetChanged()
                    Log.println(Log.DEBUG, "Dev", "Changed 3")
                    return true
                }
            }
        }
    }
    return false
}

private fun findElementChanged(oldList: List<MediaItem>, newList: List<MediaItem>): List<Int> {
    val list = mutableListOf<Int>()
    if (newList.size > oldList.size) {
        newList.forEachIndexed { index, item ->
            if (if (item is Folder) getIndex(
                    oldList,
                    item
                ) == -1 else oldList.indexOf(item) == -1
            ) list.add(index)
        }
    }
    if (newList.size < oldList.size) {
        oldList.forEachIndexed { index, item ->
            if (if (item is Folder) getIndex(
                    newList,
                    item
                ) == -1 else newList.indexOf(item) == -1
            ) list.add(index)
        }
    }
    return list
}

private fun getIndex(list: List<MediaItem>, item: MediaItem): Int {
    for ((i, l) in list.withIndex()) {
        if (l._id == item._id) return i
    }
    return -1
}