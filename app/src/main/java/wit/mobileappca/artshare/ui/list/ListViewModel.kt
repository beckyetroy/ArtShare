package wit.mobileappca.artshare.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wit.mobileappca.artshare.main.MainApp
import wit.mobileappca.artshare.models.ArtModel

class ListViewModel : ViewModel() {

    private val artsList = MutableLiveData<List<ArtModel>>()

    val observableArtsList: LiveData<List<ArtModel>>
        get() = artsList

    init {
        load()
    }

    fun load() {
        // no longer functional, to be changed
        // artsList.value = app.arts.findAll()
    }
}