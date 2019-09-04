/*
 * Copyright 2019 Carlos René Ramos López. All rights reserved.
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

package com.crrl.beatplayer.extensions

import android.util.Log
import com.crrl.beatplayer.models.Folder
import com.crrl.beatplayer.models.MediaItem
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.ui.adapters.FolderAdapter
import com.crrl.beatplayer.ui.adapters.PlaylistAdapter
import com.crrl.beatplayer.ui.adapters.SongAdapter

fun PlaylistAdapter?.dataChanged(newList: List<Playlist>): Boolean {
    this ?: return false
    when {
        newList.size > playlists.size -> {
            if (newList.size - playlists.size > 1) {
                playlists = newList.toMutableList()
                notifyDataSetChanged()
                return true
            } else {
                val pos = findElementChanged(playlists, newList)
                if (pos != -1) {
                    playlists.add(pos, newList[pos])
                    notifyItemInserted(pos)
                    Log.println(Log.DEBUG, "Dev", "$pos")
                    return true
                }
            }
        }
        newList.size < playlists.size -> {
            if (playlists.size - newList.size > 1) {
                playlists = newList.toMutableList()
                notifyDataSetChanged()
                return true
            } else {
                val pos = findElementChanged(playlists, newList)
                if (pos != -1) {
                    notifyItemRemoved(pos)
                    playlists.removeAt(pos)
                    Log.println(Log.DEBUG, "Dev", "$pos")
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
                    notifyDataSetChanged()
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
            if (newList.size - songList.size > 1) {
                songList = newList.toMutableList()
                notifyDataSetChanged()
                return true
            } else {
                val pos = findElementChanged(songList, newList)
                if (pos != -1) {
                    songList.add(pos, newList[pos])
                    notifyItemInserted(pos + 1)
                    Log.println(Log.DEBUG, "Dev", "$pos")
                    return true
                }
            }
        }
        newList.size < songList.size -> {
            if (songList.size - newList.size > 1) {
                songList = newList.toMutableList()
                notifyDataSetChanged()
                return true
            } else {
                val pos = findElementChanged(songList, newList)
                if (pos != -1) {
                    notifyItemRemoved(pos + 1)
                    songList.removeAt(pos)
                    Log.println(Log.DEBUG, "Dev", "$pos")
                    return true
                }
            }
        }
        else -> {
            newList.forEachIndexed { index, playlist ->
                if (songList.indexOf(playlist) == -1) {
                    songList = newList.toMutableList()
                    notifyItemChanged(index)
                    return true
                }
            }

            newList.forEachIndexed { index, t ->
                if (t != songList[index]) {
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
            if (newList.size - folderList.size > 1) {
                folderList = newList.toMutableList()
                notifyDataSetChanged()
            } else {
                val pos = findElementChanged(folderList, newList)
                if (pos != -1) {
                    if (folderList.size != 0) {
                        folderList.add(pos, newList[pos])
                        notifyItemInserted(pos)
                    } else {
                        folderList = newList.toMutableList()
                        notifyDataSetChanged()
                    }
                    return true
                }
            }
        }
        newList.size < folderList.size -> {
            if (folderList.size - newList.size > 1) {
                folderList = newList.toMutableList()
                notifyDataSetChanged()
            } else {
                val pos = findElementChanged(folderList, newList)
                if (pos != -1) {
                    notifyItemRemoved(pos)
                    folderList.removeAt(pos)
                    return true
                }
            }
        }
        else -> {
            newList.forEachIndexed { i, folder ->
                if (folder.id != folderList[i].id) {
                    folderList = newList.toMutableList()
                    notifyDataSetChanged()
                    return true
                }
            }
        }
    }
    return false
}

private fun findElementChanged(oldList: List<MediaItem>, newList: List<MediaItem>): Int {
    if (newList.size > oldList.size) {
        newList.forEachIndexed { index, item ->
            if (getIndex(
                    oldList,
                    item,
                    index
                ) == -1
            ) return index
        }
    } else if (newList.size < oldList.size) {
        oldList.forEachIndexed { index, item ->
            if (getIndex(
                    newList,
                    item,
                    index
                ) == -1
            ) return index
        }
    }
    return -1
}

private fun getIndex(list: List<MediaItem>, item: MediaItem, index: Int): Int {
    for ((i, l) in list.withIndex()) {
        if (l.compare(item) && i == index) return i
    }
    return -1
}