package com.crrl.beatplayer.extensions

import com.crrl.beatplayer.models.Album
import com.crrl.beatplayer.models.Artist
import com.crrl.beatplayer.models.Playlist
import com.crrl.beatplayer.models.Song
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