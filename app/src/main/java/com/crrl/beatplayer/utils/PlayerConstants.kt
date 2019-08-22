package com.crrl.beatplayer.utils

import android.net.Uri
import android.provider.MediaStore
import com.crrl.beatplayer.models.Song
import java.util.*

object PlayerConstants {

    const val PLAY_LIST_DETAIL = "play_list_detail"
    const val NOW_PLAYING = "now_playing"
    const val ARTIST_DETAIL = "artist_detail"
    const val ARTIST_KEY = "artist_key"
    const val ALBUM_KEY = "album_key"
    const val SEARCH = "search_fragment"
    const val LIBRARY = "library_fragment"
    const val SONG_DETAIL = "song_detail_fragment"
    const val ALBUM_DETAIL = "album_detail_fragment"
    const val ALL_SONGS = "all_songs_media_item"
    const val ALL_ALBUMS = "all_albums_media_item"
    const val ALL_ARTISTS = "all_artists_media_item"
    const val ALL_FOLDERS = "all_folders_media_item"
    const val ALL_PLAY_LISTS = "all_play_lists_media_item"
    const val ALL_FAVORITES = "all_favorites_media_item"
    const val SHUFFLE_MODE: Int = 0
    const val REPEAT_MODE: Int = 0
    val ARTWORK_URI: Uri = Uri.parse("content://media/external/audio/albumart")
    val SONG_URI: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    const val ALBUM_ID = "album_id"
    const val LIGHT_THEME = "light_theme"
    const val DARK_THEME = "dark_theme"
    const val SONG_KEY = "song_key"
    const val NO_DATA = "no_data"
    var CURRENT_PLAYLIST_TITLE = ""
    var CURRENT_PLAYLIST: ArrayList<Song>? = null
}
