package ro.pub.acs.playersneeded.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import ro.pub.acs.playersneeded.api.PlayersNeededApi

class LogInViewModel : ViewModel() {

    private var _logInResult = MutableLiveData<Boolean>()
    val logInResult: LiveData<Boolean>
        get() = _logInResult

    private var _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token

    fun requestLogIn(username: String, password: String) {
        Log.d("LogInFragment", "username $username password $password")
        val jsonObject = JSONObject()

        jsonObject.put("email_username", username)
        jsonObject.put("password", password)

        val jsonString = jsonObject.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        /* Send the request */
        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService.login(requestBody)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    val jsonObjectResponse = JSONObject(prettyJson)
                    val tokenResponse = jsonObjectResponse.getString("token")

                    Log.i("LogInViewModel", "LogIn successful $tokenResponse")

                    _token.value = tokenResponse
                    _logInResult.value = true
                } else {
                    Log.i("LogInViewModel", "LogIn not successful")

                    _token.value = ""
                    _logInResult.value = false
                }
            }
        }
    }

    fun reinitialize() {
        _logInResult = MutableLiveData()
        _token = MutableLiveData()
    }
}