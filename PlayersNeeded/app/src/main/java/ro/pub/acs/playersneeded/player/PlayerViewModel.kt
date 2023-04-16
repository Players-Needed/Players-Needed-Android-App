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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
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

    private var _emailPlayer = MutableLiveData<String>()
    val emailPlayer: MutableLiveData<String>
        get() = _emailPlayer

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

    private var _editPlayerDetailsResult = MutableLiveData<Boolean>()
    val editPlayerDetailsResult: LiveData<Boolean>
        get() = _editPlayerDetailsResult

    private var _logOutPlayerResult = MutableLiveData<Boolean>()
    val logOutPlayerResult: LiveData<Boolean>
        get() = _logOutPlayerResult

    private var _deleteAccountPlayerResult = MutableLiveData<Boolean>()
    val deleteAccountPlayerResult: LiveData<Boolean>
        get() = _deleteAccountPlayerResult

    private var _changePasswordPlayerResult = MutableLiveData<Boolean>()
    val changePasswordPlayerResult: LiveData<Boolean>
        get() = _changePasswordPlayerResult

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
                    _usernameSelfPlayer.value = jsonObj.getString("username").toString()

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
                    _firstNamePlayer.value = jsonObj.getString("first_name").toString()
                    _lastNamePlayer.value = jsonObj.getString("last_name").toString()
                    _emailPlayer.value = jsonObj.getString("email").toString()
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

    /**
     * Function that performs the API call (PUT request) in order to
     * change personal details about the user logged in:
     * first name, last name, user name or email
     */
    fun editPlayerDetails(
        firstName: CharSequence,
        lastName: CharSequence,
        username: CharSequence,
        email: CharSequence
    ) {
        val jsonObject = JSONObject()

        if (firstName.isNotEmpty())
            jsonObject.put("first_name", firstName)
        else
            jsonObject.put("first_name", _firstNamePlayer.value)

        if (lastName.isNotEmpty())
            jsonObject.put("last_name", lastName)
        else
            jsonObject.put("last_name", _lastNamePlayer.value)

        if (username.isNotEmpty())
            jsonObject.put("username", username)
        else
            jsonObject.put("username", _usernamePlayer)

        if (email.isNotEmpty())
            jsonObject.put("email", email)
        else
            jsonObject.put("email", _emailPlayer.value)

        val jsonString = jsonObject.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token $_token"

        /* Send the request */
        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService.editPlayerDetails(_usernamePlayer,
                headerMap, requestBody)

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

                    Log.i("PlayerViewModel", "Edit Details successful")

                    _usernamePlayer = jsonObjectResponse.getString("username").toString()
                    _firstNamePlayer.value = jsonObjectResponse.getString("first_name").toString()
                    _lastNamePlayer.value = jsonObjectResponse.getString("last_name").toString()
                    _emailPlayer.value = jsonObjectResponse.getString("email").toString()

                    _editPlayerDetailsResult.value = true
                } else {
                    Log.i("PlayerViewModel", "Edit Details unsuccessful")

                    _editPlayerDetailsResult.value = false
                }
            }
        }
    }

    /**
     * Function that performs the API call (POST request) for
     * logging out the player that is currently logged in
     */
    fun logOutPlayer() {
        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token $_token"

        /* Send the request */
        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService.logout(headerMap)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()

                    Log.i("PlayerViewModel", "LogOut successful")

                    _logOutPlayerResult.value = true
                } else {
                    Log.i("PlayerViewModel", "LogOut not successful")

                    _logOutPlayerResult.value = false
                }
            }
        }
    }

    /**
     * Function that performs the API call (DELETE request) for
     * deleting the account of the player that is currently logged in
     */
    fun deleteAccountPlayer() {
        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token $_token"

        /* Send the request */
        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService.deletePlayer(_usernamePlayer, headerMap)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    Log.i("PlayerViewModel", "Delete successful")

                    _deleteAccountPlayerResult.value = true
                } else {
                    Log.i("PlayerViewModel", "Delete not successful")

                    _deleteAccountPlayerResult.value = false
                }
            }
        }
    }

    /**
     * Function that performs the API call (POST request) for
     * changing the password of the player that is currently logged in
     */
    fun changePasswordPlayer(newPassword: CharSequence, confirmedPassword: CharSequence) {
        if (newPassword != confirmedPassword) {
            _changePasswordPlayerResult.value = false
        }

        val jsonObject = JSONObject()

        if (newPassword.isNotEmpty())
            jsonObject.put("password", newPassword)

        val jsonString = jsonObject.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token $_token"

        /* Send the request */
        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService
                .changePasswordPlayer(headerMap, requestBody)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {

                    Log.i("PlayerViewModel", "Change Password successful")

                    _changePasswordPlayerResult.value = true
                } else {
                    Log.i("PlayerViewModel", "Change Password not successful")

                    _changePasswordPlayerResult.value = false
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

    fun reinitializeEditPlayerDetailsResult() {
        _editPlayerDetailsResult.value = false
    }

    fun reinitializeLogOutPlayerResult() {
        _logOutPlayerResult.value = false
    }

    fun reinitializeDeleteAccountPlayerResult() {
        _deleteAccountPlayerResult.value = false
    }

    fun reinitializeChangePasswordPlayerResult() {
        _changePasswordPlayerResult.value = false
    }
}