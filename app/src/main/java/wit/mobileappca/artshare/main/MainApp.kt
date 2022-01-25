package org.wit.artshare.main

import android.app.Application
import org.jetbrains.anko.AnkoLogger
import org.wit.artshare.models.ArtJSONStore
import org.wit.artshare.models.ArtStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application(), AnkoLogger {

    lateinit var arts: ArtStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        arts = ArtJSONStore(applicationContext)
        i("ArtShare started")
    }
}