package wit.mobileappca.artshare.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber
import wit.mobileappca.artshare.firebase.FirebaseDBManager
import wit.mobileappca.artshare.models.ArtModel
import java.lang.Exception

class ArtDetailViewModel : ViewModel() {
    private val art = MutableLiveData<ArtModel>()

    var observableArt: LiveData<ArtModel>
        get() = art
        set(value) {
            art.value = value.value
        }

    fun getArt(userid: String, id: String) {
        try {
            FirebaseDBManager.findById(userid, id, art)
            Timber.i(
                "Detail getArt() Success : ${
                    art.value.toString()
                }"
            )
        } catch (e: Exception) {
            Timber.i("Detail getArt() Error : $e.message")
        }
    }

    fun updateArt(userid: String, id: String, art: ArtModel) {
        try {
            FirebaseDBManager.update(userid, id, art)
            Timber.i("Detail update() Success : $art")
        } catch (e: Exception) {
            Timber.i("Detail update() Error : $e.message")
        }
    }
}