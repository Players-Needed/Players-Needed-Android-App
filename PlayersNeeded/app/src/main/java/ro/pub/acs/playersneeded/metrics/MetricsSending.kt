package ro.pub.acs.playersneeded.metrics

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import ro.pub.acs.playersneeded.api.MobileMetricApi
import ro.pub.acs.playersneeded.api.MobileMetricApiService
import ro.pub.acs.playersneeded.api.PlayersNeededApi
import java.text.SimpleDateFormat
import java.util.*

interface MetricsSending: MetricsApi {
    fun getCurrentTimeFormatted(): String {
        val currentTime = Date()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getDefault()

        return dateFormat.format(currentTime)
    }

    fun sendMetric(type: String) {
        val timestamp = getCurrentTimeFormatted()

        val data = MetricsData()

        data.type = type
        data.timestamp = timestamp

        when (type) {
            "available_memory" -> {
                data.value1 = getMaximumAvailableMemory()
            }
            "total_memory" -> {
                data.value1 = getTotalMemory()
            }
            "free_memory" -> {
                data.value1 = getFreeMemory()
            }
            "rx_bytes" -> {
                data.value1 = getRxBytes()
            }
            "tx_bytes" -> {
                data.value1 = getTxBytes()
            }
        }

        data.value2 = 0

        val jsonObject = JSONObject()

        jsonObject.put("source", "mobile_metrics")
        jsonObject.put("metric_type", data.type)
        jsonObject.put("timestamp", data.timestamp)
        jsonObject.put("value1", data.value1)
        jsonObject.put("value2", data.value2)
        val jsonString = jsonObject.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            val response = MobileMetricApi.retrofitService.sendMetric(requestBody)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()

                    Log.i("MetricSending", "Metric Sending Successful")
                }
                else {
                    Log.i("MetricSending", response.code().toString() + " "
                            + response.message() + "\n" + jsonString)
                }
            }
        }
    }
}
