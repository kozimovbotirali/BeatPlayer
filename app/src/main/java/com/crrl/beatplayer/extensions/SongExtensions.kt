package com.crrl.beatplayer.extensions

import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.utils.GeneralUtils

fun Int.format(): String {
    return GeneralUtils.formatMilliseconds(this.toLong())
}

fun Int.fix(): Int {
    var value = this
    while (value >= 1000) {
        value -= 1000
    }
    return value
}

fun Song?.update(song: Song) {
    this?.id = song.id
    this?.albumId = song.albumId
    this?.artistId = song.artistId
    this?.playListId = song.playListId
    this?.title = song.title
    this?.artist = song.artist
    this?.album = song.album
    this?.duration = song.duration
    this?.trackNumber = song.trackNumber
    this?.path = song.path
}