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
import java.util.*

interface ArtistsRepositoryInterface {
    fun getAllArtist(): List<Artist>
    fun getSongsForArtist(artistId: Long): List<Song>
    fun getAlbumsForArtist(artistId: Long): List<Album>
}

class ArtistsRepository() : ArtistsRepositoryInterface {

    private lateinit var contentResolver: ContentResolver
    private lateinit var settingsUtility: SettingsUtility

    companion object {
        private var instance: ArtistsRepository? = null

        fun getInstance(context: Context?): ArtistsRepository? {
            if (instance == null)
                instance = ArtistsRepository(context)
            return instance
        }
    }

    constructor(context: Context? = null) : this() {
        contentResolver = context!!.contentResolver
        settingsUtility = SettingsUtility.getInstance(context)
    }

    override fun getAllArtist(): List<Artist> {
        val albumList =
            makeArtistCursor(null, null).toList(true, Artist.Companion::createFromCursor)
        getArtists(albumList)
        return albumList
    }

    private fun getArtists(albumList: MutableList<Artist>){
        val artistList = mutableListOf<Artist>()
        albumList.sortBy { it.name.toLowerCase(Locale.ROOT) }
        for ((i, album) in albumList.withIndex()) {
            if (i == 0) {
                artistList.add(album)
                artistList[artistList.size - 1].albumCount++
            } else {
                if (album.name != albumList[i - 1].name) {
                    artistList.add(album)
                    artistList[artistList.size - 1].albumCount++
                } else {
                    artistList[artistList.size - 1].albumCount++
                }
            }
        }
        SortModes.sortArtistList(artistList, settingsUtility.artistSortOrder)
        albumList.clear()
        albumList.addAll(artistList)
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
            getArtists(results)
            results
        } else {
            val res = results.subList(0, limit)
            getArtists(res)
            res
        }
    }

    override fun getSongsForArtist(artistId: Long): List<Song> {
        return makeArtistSongCursor(artistId)
            .toList(true) { Song.createFromCursor(this) }
    }

    override fun getAlbumsForArtist(artistId: Long): List<Album> {
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

    private fun makeArtistCursor(
        selection: String?,
        paramArrayOfString: Array<String>?
    ): Cursor? {
        return contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            arrayOf("artist_id", "_id", "artist", "numsongs"),
            selection,
            paramArrayOfString,
            "artist_id"
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