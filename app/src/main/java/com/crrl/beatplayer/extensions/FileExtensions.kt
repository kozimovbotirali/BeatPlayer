package com.crrl.beatplayer.extensions

import android.os.Environment
import java.io.File

private const val INTERNAL_STORAGE = "/Internal Storage"
private const val EXTERNAL_STORAGE = "/SD Card"

fun File?.fixedPath(): String? {
    val fixedPath =
        StringBuilder(if (Environment.isExternalStorageEmulated(this!!)) INTERNAL_STORAGE else EXTERNAL_STORAGE)
    val parts = path.split("/")
    for ((i, part) in parts.withIndex()) {
        if (i > if (fixedPath.contains(EXTERNAL_STORAGE)) 2 else 3) {
            fixedPath.append("/$part")
        }
    }
    return fixedPath.toString()
}

fun File?.fixedName(): String? {
    val fixedPath =
        if (Environment.isExternalStorageEmulated(this!!)) INTERNAL_STORAGE else EXTERNAL_STORAGE
    val parts = path.split("/")
    return if ((parts.size == 3 && fixedPath == EXTERNAL_STORAGE) || (parts.size == 4 && fixedPath == INTERNAL_STORAGE)) fixedPath.substring(
        fixedPath.indexOf("/") + 1
    ) else parts[parts.size - 1]
}