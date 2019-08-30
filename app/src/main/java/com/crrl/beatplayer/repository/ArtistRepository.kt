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

package com.crrl.beatplayer.repository

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.crrl.beatplayer.extensions.toList
import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.utils.SettingsUtility
import com.crrl.beatplayer.utils.SortModes

class ArtistRepository() {

    private lateinit var contentResolver: ContentResolver
    private lateinit var settingsUtility: SettingsUtility

    val currentArtistList: List<Artist>
        get() = getArtist()

    companion object {
        private var instance: ArtistRepository? = null

        fun getInstance(context: Context?): ArtistRepository? {
            if (instance == null)
                instance = ArtistRepository(context)
            return instance
        }
    }

    constructor(context: Context? = null) : this() {
        contentResolver = context!!.contentResolver
        settingsUtility = SettingsUtility.getInstance(context)
    }

    private fun getArtist(): List<Artist> {
        val sl = makeArtistCursor(null, null)
            .toList(true) { Artist.createFromCursor(this) }
        SortModes.sortArtistList(sl, settingsUtility.artistSortOrder)
        return sl
    }


    fun search(paramString: String, limit: Int): List<Artist> {
        val results = makeArtistCursor("artist LIKE ?", arrayOf("$paramString%"))
            .toList(true) { Artist.createFromCursor(this) }
        if (results.size < limit) {
            val moreArtists = makeArtistCursor("artist LIKE ?", arrayOf("%_$paramString%"))
                .toList(true) { Artist.createFromCursor(this) }
            results += moreArtists
        }
        return if (results.size < limit) {
            results
        } else {
            results.subList(0, limit)
        }
    }

    fun getSongsForArtist(artistId: Long): List<Song> {
        return makeArtistSongCursor(artistId)
            .toList(true) { Song.createFromCursor(this) }
    }

    fun getAlbumsForArtist(artistId: Long): List<Album> {
        return makeAlbumForArtistCursor(artistId)
            .toList(true) { Album.createFromCursor(this, artistId) }
    }


    private fun makeAlbumForArtistCursor(artistID: Long): Cursor? {
        if (artistID == -1L) {
            return null
        }
        return contentResolver.query(
            MediaStore.Audio.Artists.Albums.getContentUri("external", artistID),
            arrayOf("_id", "album", "artist", "numsongs", "minyear"),
            null,
            null,
            SortModes.AlbumModes.ALBUM_A_Z
        )
    }

    private fun makeArtistCursor(selection: String?, paramArrayOfString: Array<String>?): Cursor? {
        return contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            arrayOf("_id", "artist", "number_of_tracks", "number_of_albums"),
            selection,
            paramArrayOfString,
            ""
        )
    }

    private fun makeArtistSongCursor(artistId: Long): Cursor? {
        val artistSongSortOrder = SortModes.SongModes.SONG_A_Z
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "is_music=1 AND title != '' AND artist_id=$artistId"
        return contentResolver.query(
            uri,
            arrayOf(
                "_id",
                "title",
                "artist",
                "album",
                "duration",
                "track",
                "artist_id",
                "album_id",
                "_data"
            ),
            selection,
            null,
            artistSongSortOrder
        )
    }
}