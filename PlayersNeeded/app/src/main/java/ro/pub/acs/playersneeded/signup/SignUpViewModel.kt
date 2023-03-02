package ro.pub.acs.playersneeded.signup

import android.text.Editable
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

class SignUpViewModel : ViewModel() {
    private var _token = MutableLiveData<String>()
    val token: LiveData<String>
        get() = _token

    private var _signUpResult = MutableLiveData<Boolean>()
    val signUpResult: LiveData<Boolean>
        get() = _signUpResult

    fun requestSignUp(email: String, username: String, password: String,
                      firstName: String, lastName: String) {
        Log.d("SignUpFragment", "username $username password $password")

        val jsonObject = JSONObject()

        jsonObject.put("email", email)
        jsonObject.put("username", username)
        jsonObject.put("password", password)
        jsonObject.put("first_name", firstName)
        jsonObject.put("last_name", lastName)

        val jsonString = jsonObject.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        /* Send the request */
        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService.register(requestBody)

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

                    Log.i("SignUpViewModel", "SignUp successful $tokenResponse")

                    _token.value = tokenResponse
                    _signUpResult.value = true
                }
                else {
                    Log.i("SignUpViewModel", "SignUp not successful")

                    _token.value = ""
                    _signUpResult.value = false
                }
            }
        }
    }

    fun reinitialize() {
        _signUpResult = MutableLiveData()
        _token = MutableLiveData()
    }
}