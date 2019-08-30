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

package com.crrl.beatplayer.models

data class SearchData(
    var songList: MutableList<Song> = mutableListOf(),
    var albumList: MutableList<Album> = mutableListOf(),
    var artistList: MutableList<Artist> = mutableListOf()
) {

    fun isNotEmpty(): Boolean {
        return songList.isNotEmpty() || albumList.isNotEmpty()
    }

    fun isNotSongListEmpty(): Boolean {
        return songList.isNotEmpty()
    }

    fun isNotAlbumListEmpty(): Boolean {
        return albumList.isNotEmpty()
    }

    fun isNotArtistListEmpty(): Boolean {
        return artistList.isNotEmpty()
    }

    fun flush(): SearchData {
        songList.clear()
        albumList.clear()
        artistList.clear()
        return this
    }
}