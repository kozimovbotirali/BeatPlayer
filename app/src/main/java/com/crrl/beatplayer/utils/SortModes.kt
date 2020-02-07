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

package com.crrl.beatplayer.utils

import android.provider.MediaStore
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.models.Song

object SortModes {
    class SongModes {
        companion object {
            const val SONG_DEFAULT = MediaStore.Audio.Media.DEFAULT_SORT_ORDER
            const val SONG_A_Z = MediaStore.Audio.Media.TITLE
            const val SONG_Z_A = "$SONG_A_Z DESC"
            const val SONG_DURATION = MediaStore.Audio.Media.DURATION + " DESC"
            const val SONG_YEAR = MediaStore.Audio.Media.YEAR
            const val SONG_LAST_ADDED = MediaStore.Audio.Media.DATE_MODIFIED + " DESC"
            const val SONG_ALBUM = MediaStore.Audio.Media.ALBUM
            const val SONG_TRACK =
                MediaStore.Audio.Media.TRACK + ", " + MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        }
    }

    class AlbumModes {
        companion object {
            const val ALBUM_DEFAULT = MediaStore.Audio.Albums.DEFAULT_SORT_ORDER
            const val ALBUM_A_Z = MediaStore.Audio.Albums.ALBUM
            const val ALBUM_Z_A = "$ALBUM_A_Z DESC"
            const val ALBUM_YEAR = "${MediaStore.Audio.Albums.FIRST_YEAR} DESC"
        }
    }

    class ArtistModes {
        companion object {
            const val ARTIST_DEFAULT = MediaStore.Audio.Artists.DEFAULT_SORT_ORDER
            const val ARTIST_A_Z = MediaStore.Audio.Artists.ARTIST
            const val ARTIST_Z_A = "$ARTIST_A_Z DESC"
        }
    }

    fun sortSongList(songList: MutableList<Song>, sortMode: String) {
        when (sortMode) {
            SongModes.SONG_A_Z -> Thread {
                songList.sortWith(Comparator { a, b ->
                    a.title.toUpperCase().compareTo(b.title.toUpperCase())
                })
            }.start()
            SongModes.SONG_Z_A -> Thread {
                songList.sortWith(Comparator { a, b ->
                    b.title.toUpperCase().compareTo(a.title.toUpperCase())
                })
            }.start()
        }
    }

    fun sortAlbumList(albumList: MutableList<Album>, sortMode: String) {
        when (sortMode) {
            AlbumModes.ALBUM_A_Z -> albumList.sortWith(Comparator { a, b ->
                a.title.toUpperCase().compareTo(b.title.toUpperCase())
            })
            AlbumModes.ALBUM_Z_A -> albumList.sortWith(Comparator { a, b ->
                b.title.toUpperCase().compareTo(a.title.toUpperCase())
            })
        }
    }

    fun sortAlbumSongList(songList: MutableList<Song>) {
        songList.sortWith(Comparator { a, b -> a.trackNumber.compareTo(b.trackNumber) })
    }

    fun sortArtistList(artistList: MutableList<Artist>, sortMode: String) {
        when (sortMode) {
            ArtistModes.ARTIST_A_Z -> artistList.sortBy { it.name.toLowerCase() }
            ArtistModes.ARTIST_Z_A -> artistList.sortByDescending { it.name.toLowerCase() }
        }
    }
}
