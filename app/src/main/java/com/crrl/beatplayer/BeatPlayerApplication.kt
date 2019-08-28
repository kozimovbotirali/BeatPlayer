package com.crrl.beatplayer

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BeatPlayerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            // Android context
            androidContext(this@BeatPlayerApplication)
            modules(mainModule)
        }
    }
}