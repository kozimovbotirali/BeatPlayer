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

package com.crrl.beatplayer.repository

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.crrl.beatplayer.extensions.optimizeReadOnlyList
import com.crrl.beatplayer.extensions.toList
import com.crrl.beatplayer.models.Favorite
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.utils.DBHelper

interface FavoritesRepositoryInterface {
    fun createFavorite(favorite: Favorite): Int
    fun addSongByFavorite(idFavorite: Long, ids: LongArray): Int
    fun deleteSongByFavorite(idFavorite: Long, ids: LongArray): Int
    fun deleteFavorites(ids: LongArray): Int
    fun getFavorite(id: Long): Favorite?
    fun getFavorites(): List<Favorite>
    fun getSongsForFavorite(id: Long): List<Song>
    fun songExist(id: Long): Boolean
    fun favExist(id: Long): Boolean
}

class FavoritesRepository(private val context: Context?) : DBHelper(context),
    FavoritesRepositoryInterface {

    companion object {
        const val TABLE_FAVORITES = "favorites"
        const val TABLE_SONGS = "favorite_songs"

        const val COLUMN_ID = "id"
        const val COLUMN_FAVORITE = "favorite"
        const val COLUMN_TITLE = "title"
        const val COLUMN_ARTIST = "artist"
        const val COLUMN_ARTIST_ID = "artist_id"
        const val COLUMN_YEAR = "year"
        const val COLUMN_SONG_COUNT = "song_count"
        const val COLUMN_TYPE = "type"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(getCreateFavoritesQuery())
        db?.execSQL(getCreateSongsQuery())
        db?.execSQL(getPlusTriggerQuery())
        db?.execSQL(getMinusTriggerQuery())
        db?.execSQL(getDeleteSongs())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SONGS")
        onCreate(db)
    }

    override fun createFavorite(favorite: Favorite): Int {
        return insertRow(TABLE_FAVORITES, favorite.columnNames(), favorite.values())
    }

    override fun addSongByFavorite(idFavorite: Long, ids: LongArray): Int {
        var resp = 0
        for (id in ids) {
            resp += insertRow(
                TABLE_SONGS,
                arrayOf(COLUMN_ID, COLUMN_FAVORITE),
                arrayOf("$id", "$idFavorite")
            )
        }
        return resp
    }

    override fun deleteSongByFavorite(idFavorite: Long, ids: LongArray): Int {
        var resp = 0
        for (id in ids) {
            resp += deleteRow(TABLE_SONGS, "$COLUMN_ID = ?", arrayOf("$id"))
        }
        return resp
    }

    override fun deleteFavorites(ids: LongArray): Int {
        var resp = 0
        for (id in ids) {
            resp += deleteRow(TABLE_FAVORITES, "$COLUMN_ID = ?", arrayOf("$id"))
        }
        return resp
    }

    override fun getFavorite(id: Long): Favorite {
        val cursor = getRow(TABLE_FAVORITES, "*", "$COLUMN_ID = ?", arrayOf(id.toString()))
        cursor.use {
            return if (it.moveToFirst()) {
                Favorite.fromCursor(it)
            } else {
                Favorite()
            }
        }
    }

    override fun getFavorites(): List<Favorite> {
        val cursor =
            getRow(TABLE_FAVORITES, "*", "$COLUMN_SONG_COUNT > ?", arrayOf("0"), "$COLUMN_ID DESC")
        return cursor.toList(true) {
            Favorite.fromCursor(cursor)
        }.toList().optimizeReadOnlyList()
    }

    override fun getSongsForFavorite(id: Long): List<Song> {
        val cursor = getRow(TABLE_SONGS, "*", "$COLUMN_FAVORITE = ?", arrayOf("$id"))
        val ids = cursor.toList(true) {
            getLong(0)
        }.toLongArray()
        return FoldersRepository(context).getSongsForIds(ids)
    }

    override fun songExist(id: Long): Boolean {
        val cursor = getRow(TABLE_SONGS, "*", "$COLUMN_ID = ?", arrayOf("$id"))
        cursor.use {
            return it.moveToFirst()
        }
    }

    override fun favExist(id: Long): Boolean {
        val cursor = getRow(TABLE_FAVORITES, "*", "$COLUMN_ID = ?", arrayOf("$id"))
        cursor.use {
            return it.moveToFirst()
        }
    }

    private fun getCreateFavoritesQuery(): String {
        return "CREATE TABLE $TABLE_FAVORITES (" +
                "$COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_TITLE TEXT, " +
                "$COLUMN_ARTIST TEXT, " +
                "$COLUMN_ARTIST_ID INTEGER, " +
                "$COLUMN_YEAR INTEGER, " +
                "$COLUMN_SONG_COUNT INTEGER, " +
                "$COLUMN_TYPE TEXT" +
                ")"
    }

    private fun getCreateSongsQuery(): String {
        return "CREATE TABLE $TABLE_SONGS (" +
                "$COLUMN_ID INTEGER, " +
                "$COLUMN_FAVORITE INTEGER, " +
                "FOREIGN KEY($COLUMN_FAVORITE) REFERENCES FAVORITES($COLUMN_ID), " +
                "PRIMARY KEY($COLUMN_ID, $COLUMN_FAVORITE)" +
                ")"
    }

    private fun getPlusTriggerQuery(): String {
        return "CREATE TRIGGER PLUS_SONG_COUNT\n" +
                "AFTER INSERT ON $TABLE_SONGS\n" +
                "BEGIN\n" +
                "    UPDATE $TABLE_FAVORITES SET $COLUMN_SONG_COUNT = $COLUMN_SONG_COUNT + 1 WHERE $COLUMN_ID = NEW.$COLUMN_FAVORITE;\n" +
                "END"
    }

    private fun getMinusTriggerQuery(): String {
        return "CREATE TRIGGER MINUS_SONG_COUNT\n" +
                "AFTER DELETE ON $TABLE_SONGS\n" +
                "BEGIN\n" +
                "    UPDATE $TABLE_FAVORITES SET $COLUMN_SONG_COUNT = $COLUMN_SONG_COUNT - 1 WHERE $COLUMN_ID = OLD.$COLUMN_FAVORITE;\n" +
                "END"
    }

    private fun getDeleteSongs(): String {
        return "CREATE TRIGGER DELETE_SONGS\n" +
                "AFTER DELETE ON $TABLE_FAVORITES\n" +
                "BEGIN\n" +
                "    DELETE FROM $TABLE_SONGS WHERE $COLUMN_FAVORITE = OLD.$COLUMN_ID;\n" +
                "END"
    }
}