package wit.mobileappca.artshare.models

import androidx.lifecycle.MutableLiveData

interface ArtStore {
    fun findAll(): List<ArtModel>
    fun findAll(artsList:
                MutableLiveData<List<ArtModel>>)
    fun findAll(email:String,
                donationsList:
                MutableLiveData<List<ArtModel>>)
    fun findById(email:String, id: String,
                 art: MutableLiveData<ArtModel>)
    fun create(art: ArtModel)
    fun search(searchTerm: String): List<ArtModel>
    fun update(email: String, art: ArtModel)
    fun delete(email:String, art: ArtModel)

}