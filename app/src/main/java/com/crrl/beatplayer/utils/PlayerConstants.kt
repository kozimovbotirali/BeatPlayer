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

package com.crrl.beatplayer.utils

import android.net.Uri
import android.provider.MediaStore

object PlayerConstants {

    const val PLAY_LIST_DETAIL = "play_list_detail"
    const val NOW_PLAYING = "now_playing"
    const val ARTIST_DETAIL = "artist_detail"
    const val ARTIST_KEY = "artist_key"
    const val ALBUM_KEY = "album_key"
    const val FOLDER_KEY = "folder_key"
    const val LIBRARY = "library_fragment"
    const val SONG_DETAIL = "song_detail_fragment"
    const val ALBUM_DETAIL = "album_detail_fragment"
    val ARTWORK_URI: Uri = Uri.parse("content://media/external/audio/albumart")
    val SONG_URI: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    const val LIGHT_THEME = "light_theme"
    const val DARK_THEME = "dark_theme"
    const val SONG_KEY = "song_key"
    const val NO_DATA = "no_data"
}

enum class Repeat {
    ONE,
    ALL,
    LIST,
    OFF
}

enum class Shuffle {
    ON,
    OFF
}
