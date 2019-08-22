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