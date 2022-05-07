package wit.mobileappca.artshare.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import wit.mobileappca.artshare.firebase.FirebaseDBManager
import wit.mobileappca.artshare.models.ArtModel
import java.util.Calendar.getInstance

class CreateViewModel : ViewModel() {
    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addArt(firebaseUser: MutableLiveData<FirebaseUser>,
               art: ArtModel) {
        status.value = try {
            FirebaseDBManager.create(firebaseUser,art)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun updateArt(art: ArtModel) {
        status.value = try {
            // no longer functional, to be changed
            //app.arts.update(art.copy())
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    fun deleteArt(art: ArtModel) {
        status.value = try {
            // no longer functional, to be changed
            //app.arts.delete(art)
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }
}