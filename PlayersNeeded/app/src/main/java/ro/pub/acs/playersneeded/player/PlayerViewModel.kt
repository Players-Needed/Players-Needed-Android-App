package ro.pub.acs.playersneeded.player

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

/**
 * Class that implements the logic behind the player fragment
 * screen (API requests and live data operations)
 */
class PlayerViewModel(tokenArgument: String, usernameArgument: String) : ViewModel() {
    private var _token = tokenArgument
    val token: String
        get() = _token

    // the id of the player whose details are displayed
    private var _usernamePlayer = usernameArgument
    val usernamePlayer: String
        get() = _usernamePlayer

    private var _firstNamePlayer = MutableLiveData<String>()
    val firstNamePlayer: MutableLiveData<String>
        get() = _firstNamePlayer

    private var _lastNamePlayer = MutableLiveData<String>()
    val lastNamePlayer: MutableLiveData<String>
        get() = _lastNamePlayer

    private var _levelPlayer = MutableLiveData<Int>()
    val levelPlayer: MutableLiveData<Int>
        get() = _levelPlayer

    private var _experiencePlayer = MutableLiveData<Int>()
    val experiencePlayer: MutableLiveData<Int>
        get() = _experiencePlayer

    // the id of the player that is logged in
    private var _usernameSelfPlayer = MutableLiveData<String>()
    val usernameSelfPlayer: MutableLiveData<String>
        get() = _usernameSelfPlayer

    private var _getSelfPlayerResult = MutableLiveData<Boolean>()
    val getSelfPlayerResult: LiveData<Boolean>
        get() = _getSelfPlayerResult

    private var _getPlayerResult = MutableLiveData<Boolean>()
    val getPlayerResult: LiveData<Boolean>
        get() = _getPlayerResult

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
                    _usernamePlayer = jsonObj.getString("username").toString()

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

    /**
     * Function that gets a player according to his username
     */
    fun getPlayer() {
        val jsonObject = JSONObject()

        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token $_token"

        val jsonString = jsonObject.toString()

        Log.i("PlayerViewModel", _token)

        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService.getPlayer(_usernamePlayer, headerMap)

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
                    /* TODO - to be continued when Theo's PR is merged
                    _firstNamePlayer.value = jsonObj.getString("first_name").toString()
                    _lastNamePlayer.value = jsonObj.getString("last_name").toString()
                    */
                    _usernameSelfPlayer.value = jsonObj.getString("username").toString()
                    _levelPlayer.value = jsonObj.getString("level").toInt()
                    _experiencePlayer.value = jsonObj.getString("experience").toInt()

                    _getPlayerResult.value = true
                }
                else {
                    Log.i("PlayerViewModel", response.code().toString() + " "
                            + response.message() + "\n" + jsonString)

                    _getPlayerResult.value = false
                }
            }
        }
    }

    // reinitialize functions
    fun reinitializeSelfPlayerResult() {
        _getSelfPlayerResult.value = false
    }

    fun reinitializePlayerResult() {
        _getPlayerResult.value = false
    }
}