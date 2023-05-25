package ro.pub.acs.playersneeded.api

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.*

private const val BASE_URL = "http://10.0.2.2:1237"

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .build()

interface MobileMetricApiService {
    @POST("/api/parser")
    suspend fun sendMetric(@Body requestBody: RequestBody) : Response<ResponseBody>
}

object MobileMetricApi {
    val retrofitService : MobileMetricApiService by lazy {
        retrofit.create(MobileMetricApiService::class.java) }
}