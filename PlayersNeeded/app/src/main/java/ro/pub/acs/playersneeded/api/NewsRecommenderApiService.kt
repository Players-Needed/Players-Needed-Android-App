package ro.pub.acs.playersneeded.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

private const val BASE_URL = "http://0.0.0.0:8000"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .build()

interface NewsRecommenderApiService {
    @POST("/api/recommender/news")
    suspend fun login(@Body requestBody: RequestBody) : Response<ResponseBody>

    @GET("/api/recommender/news")
    suspend fun register(@Body requestBody: RequestBody) : Response<ResponseBody>

    @DELETE("/api/recommender/news")
    suspend fun logout(@HeaderMap headers: Map<String, String>) : Response<ResponseBody>


}

object NewsRecommenderApi {
    val retrofitService : NewsRecommenderApiService by lazy {
        retrofit.create(NewsRecommenderApiService::class.java) }
}