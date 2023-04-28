package ro.pub.acs.playersneeded.roomscreen

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
import ro.pub.acs.playersneeded.chat.Message
import ro.pub.acs.playersneeded.chat.MessageListAdapter
import ro.pub.acs.playersneeded.player.Player

/**
 * This class implements the logic behind the room fragment
 * screen (API requests and live data operations)
 */
class RoomViewModel(tokenArgument: String, idArgument: Int, usernameArg: String) : ViewModel() {
    lateinit var playersList : Array<Player>
    var messageList : Array<Message> = arrayOf()

    private var _token = tokenArgument
    val token: String
        get() = _token

    private var _usernamePlayer = usernameArg
    val usernamePlayer: String
        get() = _usernamePlayer

    private var _idRoom = idArgument
    val idRoom: Int
        get() = _idRoom

    private var _idPlayer = MutableLiveData<Int>()
    val idPlayer: MutableLiveData<Int>
        get() = _idPlayer

    private var _idPlayerAdmin = MutableLiveData<Int>()
    val idPlayerAdmin: MutableLiveData<Int>
        get() = _idPlayerAdmin

    private var _roomName = MutableLiveData<String>()
    val roomName: MutableLiveData<String>
        get() = _roomName

    private var _sportType = MutableLiveData<String>()
    val sportType: MutableLiveData<String>
        get() = _sportType

    private var _skillLevel = MutableLiveData<String>()
    val skillLevel: MutableLiveData<String>
        get() = _skillLevel

    private var _noPlayers = MutableLiveData<String>()
    val noPlayers: MutableLiveData<String>
        get() = _noPlayers

    private var _date = MutableLiveData<String>()
    val date: MutableLiveData<String>
        get() = _date

    private var _time = MutableLiveData<String>()
    val time: MutableLiveData<String>
        get() = _time

    private var _extraDetails = MutableLiveData<String>()
    val extraDetails: MutableLiveData<String>
        get() = _extraDetails

    private var _address = MutableLiveData<String>()
    val address: MutableLiveData<String>
        get() = _address

    private var _lat = MutableLiveData<Double>()
    val lat: MutableLiveData<Double>
        get() = _lat

    private var _lon = MutableLiveData<Double>()
    val lon: MutableLiveData<Double>
        get() = _lon

    private var _getDetailsResult = MutableLiveData<Boolean>()
    val getDetailsResult: LiveData<Boolean>
        get() = _getDetailsResult

    private var _getSelfPlayerResult = MutableLiveData<Boolean>()
    val getSelfPlayerResult: LiveData<Boolean>
        get() = _getSelfPlayerResult

    private var _joinRoomResult = MutableLiveData<Boolean>()
    val joinRoomResult: LiveData<Boolean>
        get() = _joinRoomResult

    private var _exitRoomResult = MutableLiveData<Boolean>()
    val exitRoomResult: LiveData<Boolean>
        get() = _exitRoomResult

    private var _deleteRoomResult = MutableLiveData<Boolean>()
    val deleteRoomResult: LiveData<Boolean>
        get() = _deleteRoomResult

    /**
     * Function that performs a GET request in order to pull the
     * existing details about the room
     */
    fun getRoomDetails() {
        val jsonObject = JSONObject()

        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token $_token"

        val jsonString = jsonObject.toString()

        Log.i("RoomViewModel", _token)

        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService.getRoom(_idRoom.toString(), headerMap)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.i("RoomViewModel", prettyJson)

                    setDetails(prettyJson)
                    populatePlayerList(prettyJson)

                    _getDetailsResult.value = true
                }
                else {
                    Log.i("RoomViewModel", response.code().toString() + " "
                            + response.message() + "\n" + jsonString)

                    _getDetailsResult.value = false
                }
            }
        }
    }

    /**
     * Function that extracts the list of players from the room
     * and populates the player list variable
     */
    private fun populatePlayerList(prettyJson: String?) {
        val jsonObj = prettyJson?.let { JSONObject(it) }

        var id: Int
        var email: String
        var username: String
        var experience: Int
        var level: Int
        var jsonPlayer: JSONObject

        playersList = arrayOf()

        if (jsonObj != null) {
            val jsonArray = jsonObj.getJSONArray("players")

            for (i in 0 until jsonArray.length()) {
                jsonPlayer = jsonArray.getJSONObject(i)

                id = jsonPlayer.getString("id").toInt()
                email = jsonPlayer.getString("email")
                username = jsonPlayer.getString("username")
                experience = jsonPlayer.getString("experience").toInt()
                level = jsonPlayer.getString("level").toInt()

                playersList += Player(id, email, username, experience, level)
            }
        }
    }

    /**
     * Function that sets room details according to the server response
     */
    private fun setDetails(prettyJson: String?) {
        val jsonObj = prettyJson?.let { JSONObject(it) }

        if (jsonObj != null) {
            _roomName.value = jsonObj.getString("name")
            _sportType.value = jsonObj.getString("sport_type")
            _skillLevel.value = jsonObj.getString("skill_level")
            _noPlayers.value = jsonObj.getString("max_no_players")
            _date.value = jsonObj.getString("date")
            _time.value = jsonObj.getString("time")
            _extraDetails.value = jsonObj.getString("extra_details")
            _address.value = jsonObj.getString("location_address")
            _lat.value = jsonObj.getString("location_lat").toDouble()
            _lon.value = jsonObj.getString("location_lon").toDouble()

            /* get the id of the administrator as well */
            val jsonObjAdmin = jsonObj.getJSONObject("room_administrator")
            _idPlayerAdmin.value = jsonObjAdmin.getString("id").toInt()
        }
    }

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

        Log.i("RoomViewModel", _token)

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

                    Log.i("RoomViewModel", prettyJson)

                    val jsonObj = JSONObject(prettyJson)
                    _idPlayer.value = jsonObj.getString("id").toInt()

                    _getSelfPlayerResult.value = true
                }
                else {
                    Log.i("RoomViewModel", response.code().toString() + " "
                            + response.message() + "\n" + jsonString)

                    _getSelfPlayerResult.value = false
                }
            }
        }
    }

    /**
     * Function that performs the join room operation
     */
    fun joinRoom() {
        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token $_token"

        Log.i("RoomViewModel", _token)

        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService.joinRoom(_idRoom.toString(), headerMap)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.i("RoomViewModel", prettyJson)

                    _joinRoomResult.value = true
                }
                else {
                    Log.i("RoomViewModel", response.code().toString() + " "
                            + response.message())

                    _joinRoomResult.value = false
                }
            }
        }
    }

    /**
     * Function that performs the exit room operation
     */
    fun exitRoom() {
        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token $_token"

        Log.i("RoomViewModel", _token)

        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService.exitRoom(_idRoom.toString(), headerMap)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.i("RoomViewModel", prettyJson)

                    _exitRoomResult.value = true
                }
                else {
                    Log.i("RoomViewModel", response.code().toString() + " "
                            + response.message())

                    _exitRoomResult.value = false
                }
            }
        }
    }

    /**
     * Function that performs the request for deleting a certain room
     */
    fun deleteRoom() {
        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token $_token"

        Log.i("RoomViewModel", _token)

        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService.deleteRoom(_idRoom.toString(),
                headerMap)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    Log.i("RoomViewModel", "Delete Request Successful")

                    _deleteRoomResult.value = true
                }
                else {
                    Log.i("RoomViewModel", response.code().toString() + " "
                            + response.message())

                    _deleteRoomResult.value = false
                }
            }
        }
    }

    /**
     * Function that checks if a certain player is in the list of players of that team
     */
    fun checkPlayerId(value: Int?): Boolean {
        if (value != null) {
            for (i in playersList.indices) {
                if (playersList[i].id == value)
                    return true
            }
        }
        return false
    }

    /**
     * Function that adds the messages that were already
     * in the chat in the message list
     */
    fun addMessages(messageJson: String) {
        Log.i("RoomViewModel", messageJson)

        val jsonObj = JSONObject(messageJson)
        val type = jsonObj.getString("type")

//        if (type == "chat_message_echo" && jsonObj.getString("name") != usernamePlayer) {
//            addMessage(messageJson)
//        } else
        if (type == "welcome_message") {
            val jsonArray = jsonObj.getJSONArray("messages")
            var jsonMessage: JSONObject

            for (i in 0 until jsonArray.length()) {
                jsonMessage = jsonArray.getJSONObject(i)

                val timestamp = jsonMessage.getString("timestamp")
                val date = timestamp.subSequence(0, 10)
                val time = timestamp.subSequence(11, 16)

                val messageObj = Message(
                    jsonMessage.getString("message"),
                    date as String, time as String,
                    jsonMessage.getString("name"), false
                )

                if (messageList.size - 1 > 0 && date == messageList[messageList.size - 1].date) {
                    messageObj.today = true
                }

                messageList += messageObj
            }
        }
    }

    /**
     * Function that adds a message in the message list
     */
    fun addMessage(messageJson: String) {
        Log.i("RoomViewModel", messageJson)

        val jsonObj = JSONObject(messageJson)

        val timestamp = jsonObj.getString("timestamp")
        val date = timestamp.subSequence(0, 10)
        val time = timestamp.subSequence(11, 16)

        val messageObj = Message(
            jsonObj.getString("message"),
            date as String, time as String,
            jsonObj.getString("name"), false
        )

        if (messageList.size - 1 > 0 && date == messageList[messageList.size - 1].date) {
            messageObj.today = true
        }

        messageList += messageObj
    }

    /* reinitialization functions */
    fun reinitializeDetailsResult() {
        _getDetailsResult.value = false
    }

    fun reinitializeSelfPlayerResult() {
        _getSelfPlayerResult.value = false
    }

    fun reinitializeJoinRoomResult() {
        _joinRoomResult.value = false
    }

    fun reinitializeExitRoomResult() {
        _joinRoomResult.value = false
    }

    fun reinitializeDeleteRoomResult() {
        _deleteRoomResult.value = false
    }
}