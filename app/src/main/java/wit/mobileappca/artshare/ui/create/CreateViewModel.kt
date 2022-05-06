package wit.mobileappca.artshare.ui.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import wit.mobileappca.artshare.main.MainApp
import wit.mobileappca.artshare.models.ArtJSONStore
import wit.mobileappca.artshare.models.ArtManager
import wit.mobileappca.artshare.models.ArtModel
import java.util.Calendar.getInstance

class CreateViewModel : ViewModel() {
    private val status = MutableLiveData<Boolean>()

    val observableStatus: LiveData<Boolean>
        get() = status

    fun addArt(art: ArtModel) {
        status.value = try {
            ArtManager.create(art)
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