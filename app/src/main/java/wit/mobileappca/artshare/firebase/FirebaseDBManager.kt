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
        database.child("arts")
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
                    database.child("arts")
                        .removeEventListener(this)

                    artsList.value = localList
                }
            })
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
        art.uid = key
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

    fun updateProfilePicRef(userid: String,imageUri: String) {

        val userArts = database.child("user-arts").child(userid)
        val allArts = database.child("arts")

        userArts.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        //Update Users imageUri
                        it.ref.child("profilepic").setValue(imageUri)
                        //Update all arts that match 'it'
                        val art = it.getValue(ArtModel::class.java)
                        allArts.child(art!!.uid!!)
                            .child("profilepic").setValue(imageUri)
                    }
                }
            })
    }

    fun updateImageRef(userid: String, title: String,imageUri: String) {

        val userArts = database.child("user-arts").child(userid).child(title)
        val allArts = database.child("arts")

        userArts.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        //Update Users imageUri
                        it.ref.child("image").setValue(imageUri)
                        //Update all arts that match 'it'
                        val art = it.getValue(ArtModel::class.java)
                        userArts.child(art!!.title!!)
                            .child("image").setValue(imageUri)
                    }
                }
            })
    }
}