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

package com.crrl.beatplayer.extensions

import com.crrl.beatplayer.models.*
import com.google.gson.Gson

fun String?.toSong(): Song {
    return Gson().fromJson(this, Song::class.java)
}

fun String?.toAlbum(): Album {
    return Gson().fromJson(this, Album::class.java)
}

fun String?.toArtist(): Artist {
    return Gson().fromJson(this, Artist::class.java)
}

fun String?.toPlaylist(): Playlist {
    return Gson().fromJson(this, Playlist::class.java)
}

fun String?.toFolder(): Folder {
    return Gson().fromJson(this, Folder::class.java)
}