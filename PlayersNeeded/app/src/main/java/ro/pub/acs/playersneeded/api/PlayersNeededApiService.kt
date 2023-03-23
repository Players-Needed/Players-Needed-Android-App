package ro.pub.acs.playersneeded.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

private const val BASE_URL = "http://10.0.2.2:8000"
//private const val BASE_URL = "http://127.0.0.1:8000"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .build()

interface PlayersNeededApiService {
    @POST("/api/auth/login/")
    suspend fun login(@Body requestBody: RequestBody) : Response<ResponseBody>

    @POST("/api/auth/register/")
    suspend fun register(@Body requestBody: RequestBody) : Response<ResponseBody>

    @POST("/api/rooms/")
    suspend fun createRoom(@HeaderMap headers: Map<String, String>, @Body requestBody: RequestBody) :
            Response<ResponseBody>

    @GET("/api/rooms")
    suspend fun getRooms(@QueryMap params: Map<String, String>, @HeaderMap headers: Map<String,
            String>) :
            Response<ResponseBody>
}

object PlayersNeededApi {
    val retrofitService : PlayersNeededApiService by lazy {
        retrofit.create(PlayersNeededApiService::class.java) }
}
