package ro.pub.acs.playersneeded.user

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
import org.json.JSONObject
import ro.pub.acs.playersneeded.api.PlayersNeededApi
import ro.pub.acs.playersneeded.news.News

class UserHomeScreenViewModel(tokenArgument: String) : ViewModel() {
    var newsList : Array<News> = arrayOf(News(), News(), News())

    private var _token = tokenArgument
    val token: String
        get() = _token

    private var _idPlayer = MutableLiveData<Int>()
    val idPlayer: MutableLiveData<Int>
        get() = _idPlayer

    private var _usernamePlayer = MutableLiveData<String>()
    val usernamePlayer: MutableLiveData<String>
        get() = _usernamePlayer

    private var _getSelfPlayerResult = MutableLiveData<Boolean>()
    val getSelfPlayerResult: LiveData<Boolean>
        get() = _getSelfPlayerResult

    /**
     * Function that gets the player that is logged in
     * It is used to determine whether the current player
     * is administrator or participant
     */
    fun getSelfPlayer() {
        val jsonObject = JSONObject()

        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token $_token"

        val jsonString = jsonObject.toString()

        Log.i("PlayerViewModel", _token)

        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService.getSelfPlayer(headerMap)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.i("PlayerViewModel", prettyJson)

                    val jsonObj = JSONObject(prettyJson)
                    _idPlayer.value = jsonObj.getString("id").toInt()
                    _usernamePlayer.value = jsonObj.getString("username").toString()

                    _getSelfPlayerResult.value = true
                }
                else {
                    Log.i("PlayerViewModel", response.code().toString() + " "
                            + response.message() + "\n" + jsonString)

                    _getSelfPlayerResult.value = false
                }
            }
        }
    }

    fun reinitializeSelfPlayerResult() {
        _getSelfPlayerResult.value = false
    }
}