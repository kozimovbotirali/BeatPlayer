package com.crrl.beatplayer.models

import android.database.Cursor
import com.google.gson.Gson

data class Artist(
    var id: Long = 0,
    var name: String = "",
    var songCount: Int = 0,
    var albumCount: Int = 0
) : MediaItem() {
    companion object {
        fun createFromCursor(cursor: Cursor): Artist {
            return Artist(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getInt(3)
            )
        }
    }

    override fun toString(): String {
        return Gson().toJson(this)
    }
}