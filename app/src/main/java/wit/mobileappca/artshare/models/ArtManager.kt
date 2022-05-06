package wit.mobileappca.artshare.models

import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import wit.mobileappca.artshare.api.ArtClient
import wit.mobileappca.artshare.api.ArtWrapper

object ArtManager : ArtStore {
    override fun findAll(): List<ArtModel> {
        TODO("Not yet implemented")
    }

    override fun findAll(artsList: MutableLiveData<List<ArtModel>>) {

        val call = ArtClient.getApi().findall()

        call.enqueue(object : Callback<List<ArtModel>> {
            override fun onResponse(call: Call<List<ArtModel>>,
                                    response: Response<List<ArtModel>>
            ) {
                artsList.value = response.body() as ArrayList<ArtModel>
                Timber.i("Retrofit findAll() = ${response.body()}")
            }

            override fun onFailure(call: Call<List<ArtModel>>, t: Throwable) {
                Timber.i("Retrofit findAll() Error : $t.message")
            }
        })
    }

    override fun findAll(email: String, artsList: MutableLiveData<List<ArtModel>>) {

        val call = ArtClient.getApi().findall(email)

        call.enqueue(object : Callback<List<ArtModel>> {
            override fun onResponse(call: Call<List<ArtModel>>,
                                    response: Response<List<ArtModel>>
            ) {
                artsList.value = response.body() as ArrayList<ArtModel>
                Timber.i("Retrofit findAll() = ${response.body()}")
            }

            override fun onFailure(call: Call<List<ArtModel>>, t: Throwable) {
                Timber.i("Retrofit findAll() Error : $t.message")
            }
        })
    }

    override fun findById(email: String, id: String, art: MutableLiveData<ArtModel>)   {

        val call = ArtClient.getApi().get(email,id)

        call.enqueue(object : Callback<ArtModel> {
            override fun onResponse(call: Call<ArtModel>, response: Response<ArtModel>) {
                art.value = response.body() as ArtModel
                Timber.i("Retrofit findById() = ${response.body()}")
            }

            override fun onFailure(call: Call<ArtModel>, t: Throwable) {
                Timber.i("Retrofit findById() Error : $t.message")
            }
        })
    }

    override fun create( art: ArtModel) {

        val call = ArtClient.getApi().post(art.email,art)
        Timber.i("Retrofit $call")

        call.enqueue(object : Callback<ArtWrapper> {
            override fun onResponse(call: Call<ArtWrapper>,
                                    response: Response<ArtWrapper>
            ) {
                val artWrapper = response.body()
                if (artWrapper != null) {
                    Timber.i("Retrofit ${artWrapper.message}")
                    Timber.i("Retrofit ${artWrapper.data.toString()}")
                }
            }

            override fun onFailure(call: Call<ArtWrapper>, t: Throwable) {
                Timber.i("Retrofit Error : $t.message")
                Timber.i("Retrofit create Error : $t.message")
            }
        })
    }

    override fun search(searchTerm: String): List<ArtModel> {
        TODO("Not yet implemented")
    }

    override fun delete(email: String, art: ArtModel) {

        val call = ArtClient.getApi().delete(email,art.id)

        call.enqueue(object : Callback<ArtWrapper> {
            override fun onResponse(call: Call<ArtWrapper>,
                                    response: Response<ArtWrapper>
            ) {
                val artWrapper = response.body()
                if (artWrapper != null) {
                    Timber.i("Retrofit Delete ${artWrapper.message}")
                    Timber.i("Retrofit Delete ${artWrapper.data.toString()}")
                }
            }

            override fun onFailure(call: Call<ArtWrapper>, t: Throwable) {
                Timber.i("Retrofit Delete Error : $t.message")
            }
        })
    }

    override fun update(email: String, art: ArtModel) {

        val call = ArtClient.getApi().put(email,art.id,art)

        call.enqueue(object : Callback<ArtWrapper> {
            override fun onResponse(call: Call<ArtWrapper>,
                                    response: Response<ArtWrapper>
            ) {
                val artWrapper = response.body()
                if (artWrapper != null) {
                    Timber.i("Retrofit Update ${artWrapper.message}")
                    Timber.i("Retrofit Update ${artWrapper.data.toString()}")
                }
            }

            override fun onFailure(call: Call<ArtWrapper>, t: Throwable) {
                Timber.i("Retrofit Update Error : $t.message")
            }
        })
    }
}