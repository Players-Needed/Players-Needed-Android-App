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

    /*
     * Auth requests
     */

    @POST("/api/auth/login/")
    suspend fun login(@Body requestBody: RequestBody) : Response<ResponseBody>

    @POST("/api/auth/register/")
    suspend fun register(@Body requestBody: RequestBody) : Response<ResponseBody>

    @POST("/api/auth/logout")
    suspend fun logout(@HeaderMap headers: Map<String, String>) : Response<ResponseBody>

    /*
     * Room requests
     */

    @POST("/api/rooms/")
    suspend fun createRoom(@HeaderMap headers: Map<String, String>,
                           @Body requestBody: RequestBody) : Response<ResponseBody>

    @GET("/api/rooms")
    suspend fun getRooms(@QueryMap params: Map<String, String>, @HeaderMap headers: Map<String,
            String>) : Response<ResponseBody>

    @GET("/api/rooms/{roomId}")
    suspend fun getRoom(@Path("roomId") roomId: String, @HeaderMap headers: Map<String,
            String>) : Response<ResponseBody>

    @DELETE("/api/rooms/{roomId}")
    suspend fun deleteRoom(@Path("roomId") roomId: String, @HeaderMap headers: Map<String,
            String>) : Response<ResponseBody>

    @POST("/api/rooms/{roomId}/join")
    suspend fun joinRoom(@Path("roomId") roomId: String, @HeaderMap headers: Map<String,
            String>) : Response<ResponseBody>

    @POST("/api/rooms/{roomId}/exit")
    suspend fun exitRoom(@Path("roomId") roomId: String, @HeaderMap headers: Map<String,
            String>) : Response<ResponseBody>

    /*
     * Player requests
     */

    @GET("/api/players/self")
    suspend fun getSelfPlayer(@HeaderMap headers: Map<String, String>) : Response<ResponseBody>

    @GET("/api/players/player/{username}")
    suspend fun getPlayer(@Path("username") username: String,
                          @HeaderMap headers: Map<String, String>) : Response<ResponseBody>

    @PUT("/api/players/player/{username}")
    suspend fun editPlayerDetails(@Path("username") username: String,
                          @HeaderMap headers: Map<String, String>,
                          @Body requestBody: RequestBody) : Response<ResponseBody>

    @DELETE("/api/players/player/{username}")
    suspend fun deletePlayer(@Path("username") username: String,
                          @HeaderMap headers: Map<String, String>) : Response<ResponseBody>

    @POST("/api/players/self/change_password")
    suspend fun changePasswordPlayer(@HeaderMap headers: Map<String, String>,
                                     @Body requestBody: RequestBody) : Response<ResponseBody>

}

object PlayersNeededApi {
    val retrofitService : PlayersNeededApiService by lazy {
        retrofit.create(PlayersNeededApiService::class.java) }
}
