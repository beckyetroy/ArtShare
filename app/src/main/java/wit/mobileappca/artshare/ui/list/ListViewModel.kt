package wit.mobileappca.artshare.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import timber.log.Timber
import wit.mobileappca.artshare.firebase.FirebaseDBManager
import wit.mobileappca.artshare.models.ArtModel
import java.lang.Exception

class ListViewModel : ViewModel() {

    private val artsList = MutableLiveData<List<ArtModel>>()

    val observableArtsList: LiveData<List<ArtModel>>
        get() = artsList

    var liveFirebaseUser = MutableLiveData<FirebaseUser>()
    var readOnly = MutableLiveData(false)

    init {
        load()
    }

    fun load() {
        try {
            readOnly.value = false
            FirebaseDBManager.findAll(liveFirebaseUser.value?.uid!!, artsList)
            Timber.i("List Load Success : ${artsList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("List Load Error : $e.message")
        }
    }

    fun loadAll() {
        try {
            readOnly.value = true
            FirebaseDBManager.findAll(artsList)
            Timber.i("LoadAll Success : ${artsList.value.toString()}")
        }
        catch (e: Exception) {
            Timber.i("LoadAll Error : $e.message")
        }
    }

    fun delete(userid: String, id: String) {
        try {
            FirebaseDBManager.delete(userid,id)
            Timber.i("Delete Success")
        }
        catch (e: Exception) {
            Timber.i("Delete Error : $e.message")
        }
    }
}