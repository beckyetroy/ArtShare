package wit.mobileappca.artshare.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import timber.log.Timber
import wit.mobileappca.artshare.main.MainApp
import wit.mobileappca.artshare.models.ArtManager
import wit.mobileappca.artshare.models.ArtModel
import java.lang.Exception

class ListViewModel : ViewModel() {

    private val artsList = MutableLiveData<List<ArtModel>>()

    val observableArtsList: LiveData<List<ArtModel>>
        get() = artsList

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()

    init {
        load()
    }

    fun load() {
        try {
            ArtManager.findAll(liveFirebaseUser.value?.email!!, artsList)
            Timber.i("Report Load Success : ${artsList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("Report Load Error : $e.message")
        }
    }

    fun delete(email: String, art: ArtModel) {
        try {
            ArtManager.delete(email,art)
            Timber.i("Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Delete Error : $e.message")
        }
    }
}