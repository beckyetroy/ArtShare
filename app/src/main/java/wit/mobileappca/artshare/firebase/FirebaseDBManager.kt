package wit.mobileappca.artshare.firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import timber.log.Timber
import wit.mobileappca.artshare.models.ArtModel
import wit.mobileappca.artshare.models.ArtStore

object FirebaseDBManager : ArtStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAll(artsList: MutableLiveData<List<ArtModel>>) {
        TODO("Not yet implemented")
    }

    override fun findAll(userid: String, artsList: MutableLiveData<List<ArtModel>>) {
        database.child("user-arts").child(userid)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Art error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<ArtModel>()
                    val children = snapshot.children
                    children.forEach {
                        val art = it.getValue(ArtModel::class.java)
                        localList.add(art!!)
                    }
                    database.child("user-arts").child(userid)
                        .removeEventListener(this)

                    artsList.value = localList
                }
            })
    }

    override fun findById(userid: String, artid: String, art: MutableLiveData<ArtModel>) {
        database.child("user-arts").child(userid.toString())
            .child(artid).get().addOnSuccessListener {
                art.value = it.getValue(ArtModel::class.java)
                Timber.i("firebase Got value ${it.value}")
            }.addOnFailureListener{
                Timber.e("firebase Error getting data $it")
            }
    }

    override fun create(firebaseUser: MutableLiveData<FirebaseUser>, art: ArtModel) {
        Timber.i("Firebase DB Reference : $database")

        val uid = firebaseUser.value!!.uid
        val key = database.child("arts").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        art.id = key
        val artValues = art.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/arts/$key"] = artValues
        childAdd["/user-arts/$uid/$key"] = artValues

        database.updateChildren(childAdd)
    }

    override fun search(searchTerm: String): List<ArtModel> {
        TODO("Not yet implemented")
    }

    override fun delete(userid: String, artid: String) {
        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/arts/$artid"] = null
        childDelete["/user-arts/$userid/$artid"] = null

        database.updateChildren(childDelete)
    }

    override fun update(userid: String, artid: String, art: ArtModel) {
        val artValues = art.toMap()

        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["arts/$artid"] = artValues
        childUpdate["user-arts/$userid/$artid"] = artValues

        database.updateChildren(childUpdate)
    }
}