package wit.mobileappca.artshare.api

import retrofit2.Call
import retrofit2.http.*
import wit.mobileappca.artshare.models.ArtModel

interface ArtService {
    @GET("/art")
    fun findall(): Call<List<ArtModel>>

    @GET("/art/{email}")
    fun findall(@Path("email") email: String?)
            : Call<List<ArtModel>>

    @GET("/art/{email}/{id}")
    fun get(@Path("email") email: String?,
            @Path("id") id: String): Call<ArtModel>

    @DELETE("/art/{email}/{id}")
    fun delete(@Path("email") email: String?,
               @Path("id") id: Long
    ): Call<ArtWrapper>

    @POST("/art/{email}")
    fun post(@Path("email") email: String?,
             @Body donation: ArtModel)
            : Call<ArtWrapper>

    @PUT("/art/{email}/{id}")
    fun put(@Path("email") email: String?,
            @Path("id") id: Long,
            @Body donation: ArtModel
    ): Call<ArtWrapper>
}
