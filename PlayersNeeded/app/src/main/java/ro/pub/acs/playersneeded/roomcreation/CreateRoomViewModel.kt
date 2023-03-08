package ro.pub.acs.playersneeded.roomcreation

import android.util.Log
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

class CreateRoomViewModel(tokenArgument: String) : ViewModel() {
    private var _token = tokenArgument
    val token: String
        get() = _token

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

    fun setSportType(sportTypeArg: String) {
        _sportType.value = sportTypeArg
    }

    fun setDate(dateArg: String) {
        _date.value = dateArg
    }

    fun setTime(timeArg: String) {
        _time.value = timeArg
    }

    fun setLocationLat(latArg: Double) {
        _lat.value = latArg
    }

    fun setLocationLon(lonArg: Double) {
        _lon.value = lonArg
    }

    fun setExtraDetails(extraDetailsArg: String) {
        _extraDetails.value = extraDetailsArg
    }

    fun setName(roomNameArg: String) {
        _roomName.value = roomNameArg
    }

    fun setSkillLevel(skillLevelArg: String) {
        _skillLevel.value = skillLevelArg
    }

    fun setNoPlayers(noPlayersArg: String) {
        _noPlayers.value = noPlayersArg
    }

    fun sendRoomDetails() {
        val jsonObject = JSONObject()
        jsonObject.put("sport_type", _sportType.value)
        jsonObject.put("date", _date.value)
        jsonObject.put("time", _time.value)
        jsonObject.put("location_lat", _lat.value)
        jsonObject.put("location_lon", _lon.value)
        jsonObject.put("extra_details", _extraDetails.value)
        jsonObject.put("name", _roomName.value)
        jsonObject.put("skill_level", _skillLevel.value)
        jsonObject.put("max_no_players", _noPlayers.value)

        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token $_token"

        val jsonString = jsonObject.toString()
        val requestBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

        Log.i("CreateRoomViewModel", _token)

        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService.createRoom(headerMap, requestBody)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.i("CreateRoomViewModel", prettyJson)
                }
                else {
                    Log.i("CreateRoomViewModel", response.code().toString() + " "
                            + response.message() + "\n" + jsonString)
                }
            }
        }
    }
}