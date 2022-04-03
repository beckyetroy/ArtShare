package wit.mobileappca.artshare.main

import android.app.Application
import org.jetbrains.anko.AnkoLogger
import wit.mobileappca.artshare.models.ArtJSONStore
import wit.mobileappca.artshare.models.ArtStore
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