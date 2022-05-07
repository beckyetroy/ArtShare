package wit.mobileappca.artshare.models

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser

interface ArtStore {
    fun findAll(artsList:
                MutableLiveData<List<ArtModel>>)
    fun findAll(userid:String,
                artsList:
                MutableLiveData<List<ArtModel>>)
    fun findById(userid:String, artid: String,
                 art: MutableLiveData<ArtModel>)
    fun create(firebaseUser: MutableLiveData<FirebaseUser>, art: ArtModel)
    fun search(searchTerm: String): List<ArtModel>
    fun delete(userid:String, artid: String)
    fun update(userid:String, artid: String, art: ArtModel)
}