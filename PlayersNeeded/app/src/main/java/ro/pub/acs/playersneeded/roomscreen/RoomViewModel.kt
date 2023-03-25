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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import ro.pub.acs.playersneeded.api.PlayersNeededApi
import ro.pub.acs.playersneeded.player.Player

class RoomViewModel(tokenArgument: String, idArgument: Int) : ViewModel() {
    lateinit var playersList : Array<Player>

    private var _token = tokenArgument
    val token: String
        get() = _token

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

    /*
     * Function that gets the player that is logged in
     * It is used to determine whether the current player
     * is administrator or participant
     * */
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
}