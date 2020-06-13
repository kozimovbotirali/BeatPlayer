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

import android.content.ContentUris.withAppendedId
import android.content.Context
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.RectF
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.crrl.beatplayer.R
import com.crrl.beatplayer.models.Song
import com.crrl.beatplayer.utils.BeatConstants.ARTWORK_URI
import com.crrl.beatplayer.utils.BeatConstants.SONG_URI
import java.io.FileInputStream
import java.io.FileNotFoundException


object GeneralUtils {

    const val PORTRAIT = ORIENTATION_PORTRAIT

    val screenWidth: Int
        get() = Resources.getSystem().displayMetrics.widthPixels

    val screenHeight: Int
        get() = Resources.getSystem().displayMetrics.heightPixels

    fun getOrientation(context: Context): Int {
        return context.resources.configuration.orientation
    }

    fun formatMilliseconds(duration: Long): String {
        val seconds = (duration / 1000).toInt() % 60
        val minutes = (duration / (1000 * 60) % 60).toInt()
        val hours = (duration / (1000 * 60 * 60) % 24).toInt()
        "${timeAddZeros(hours, false)}:${timeAddZeros(minutes)}:${timeAddZeros(seconds)}".apply {
            return if (this[0] == ':') replaceFirst(":", "") else this
        }
    }

    fun getTotalTime(songList: List<Song>): Long {
        var minutes = 0L
        var hours = 0L
        var seconds = 0L
        for (song in songList) {
            seconds += (song.duration / 1000 % 60).toLong()
            minutes += (song.duration / (1000 * 60) % 60).toLong()
            hours += (song.duration / (1000 * 60 * 60) % 24).toLong()
        }
        return hours * (1000 * 60 * 60) + minutes * (1000 * 60) + seconds * 1000
    }

    fun audio2Raw(context: Context, uri: Uri): ByteArray? {
        val parcelFileDescriptor = try {
            context.contentResolver.openFileDescriptor(uri, BeatConstants.READ_ONLY_MODE, null)
                ?: return null
        } catch (ex: FileNotFoundException) {
            return null
        }
        val fis = FileInputStream(parcelFileDescriptor.fileDescriptor)
        val data = try {
            fis.readBytes()
        } catch (ex: Exception) {
            audio2Raw(context, uri)
        }
        fis.close()
        return data
    }

    fun toggleShowKeyBoard(context: Context?, editText: EditText, show: Boolean) {
        if (show) {
            editText.apply {
                requestFocus()
                val imm =
                    context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        } else {
            editText.apply {
                clearFocus()
                val imm =
                    context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.hideSoftInputFromWindow(editText.windowToken, 0)
            }
        }
    }

    fun dip2px(context: Context, dpValue: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun addZeros(number: Int?): String {
        if (number!! < 10) return "00${number}"
        if (number < 100) return "0${number}"
        return number.toString()
    }

    private fun timeAddZeros(number: Int?, showIfIsZero: Boolean = true): String {
        return when (number) {
            0 -> if (showIfIsZero) "0${number}" else ""
            1, 2, 3, 4, 5, 6, 7, 8, 9 -> "0${number}"
            else -> number.toString()
        }
    }

    fun getBlackWhiteColor(color: Int): Int {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return if (darkness >= 0.5) {
            Color.WHITE
        } else Color.BLACK
    }

    fun getStoragePaths(context: Context): List<String> {
        return ContextCompat.getExternalFilesDirs(context, null).map {
            it.path.replace("/Android/data/${context.packageName}/files", "")
        }
    }

    /**
     * This method draws a round rect shape.
     * @param width: int
     * @param height: int
     * @param color: int
     * @return ShapeDrawable
     */
    fun drawRoundRectShape(
        width: Int,
        height: Int,
        color: Int,
        radius: Float = 30f
    ): ShapeDrawable {
        val r = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        val oval = ShapeDrawable(RoundRectShape(r, RectF(), r))
        oval.intrinsicHeight = height
        oval.intrinsicWidth = width
        oval.paint.color = color
        return oval
    }

    fun getAlbumArtBitmap(context: Context, albumId: Long?): Bitmap? {
        if (albumId == null) return null
        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, getAlbumArtUri(albumId))
        } catch (e: FileNotFoundException) {
            BitmapFactory.decodeResource(context.resources, R.drawable.ic_app_logo)
        }
    }

    fun isOreo() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    fun getAlbumArtUri(albumId: Long): Uri = withAppendedId(ARTWORK_URI, albumId)
    fun getSongUri(songId: Long): Uri = withAppendedId(SONG_URI, songId)
}
