package ro.pub.acs.playersneeded.joinroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.*
import org.json.JSONArray
import ro.pub.acs.playersneeded.api.PlayersNeededApi
import ro.pub.acs.playersneeded.room.Room

class JoinRoomViewModel(tokenArgument: String, usernameArg: String) : ViewModel() {
    var roomList : Array<Room> = arrayOf()

    private var _token = tokenArgument
    val token: String
        get() = _token

    private var _usernamePlayer = usernameArg
    val usernamePlayer: String
        get() = _usernamePlayer

    private val _eventDataSuccess = MutableLiveData<Boolean>()
    val eventDataSuccess: LiveData<Boolean>
        get() = _eventDataSuccess

    private var _roomNameFilter = MutableLiveData<String?>()
    val roomNameFilter: MutableLiveData<String?>
        get() = _roomNameFilter

    private var _sportTypeFilter = MutableLiveData<String?>()
    val sportTypeFilter: MutableLiveData<String?>
        get() = _sportTypeFilter

    private var _skillLevelFilter = MutableLiveData<String?>()
    val skillLevelFilter: MutableLiveData<String?>
        get() = _skillLevelFilter

    private var _dateFromFilter = MutableLiveData<String?>()
    val dateFromFilter: MutableLiveData<String?>
        get() = _dateFromFilter

    private var _timeFromFilter = MutableLiveData<String?>()
    val timeFromFilter: MutableLiveData<String?>
        get() = _timeFromFilter

    private var _dateUntilFilter = MutableLiveData<String?>()
    val dateUntilFilter: MutableLiveData<String?>
        get() = _dateUntilFilter

    private var _timeUntilFilter = MutableLiveData<String?>()
    val timeUntilFilter: MutableLiveData<String?>
        get() = _timeUntilFilter

    fun populateRoomList() {
        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token $_token"

        Log.i("JoinRoomViewModel", _token)

        val arguments: MutableMap<String, String> = checkFilters().toMutableMap()
        arguments["self"] = "false"
        
        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService
                .getRooms(arguments, headerMap)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    addRooms(prettyJson)
                    _eventDataSuccess.value = true

                    Log.i("JoinRoomViewModel", prettyJson)
                } else {

                    Log.i(
                        "JoinRoomViewModel", response.code().toString() + " "
                                + response.message()
                    )
                }
            }
        }
    }

    fun checkFilters(): Map<String, String> {
        val arguments = mutableMapOf<String, String>()

        if (_sportTypeFilter.value != null)
            arguments["sport_type"] = _sportTypeFilter.value.toString()
        if (_dateFromFilter.value != null)
            arguments["date_from"] = _dateFromFilter.value.toString()
        if (_dateUntilFilter.value != null)
            arguments["date_until"] = _dateUntilFilter.value.toString()
        if (_timeFromFilter.value != null)
            arguments["time_from"] = _timeFromFilter.value.toString()
        if (_timeUntilFilter.value != null)
            arguments["time_until"] = _timeUntilFilter.value.toString()
        if (_roomNameFilter.value != null)
            arguments["name"] = _roomNameFilter.value.toString()
        if (_skillLevelFilter.value != null)
            arguments["skill_level"] = _skillLevelFilter.value.toString()

        return arguments
    }

    fun addRooms(prettyJson: String?) {
        val jsonArray = JSONArray(prettyJson)

        Log.i("JoinRoomViewModel", jsonArray.toString())

        roomList = arrayOf()

        for (i in 0 until jsonArray.length()) {
            val jsonRoom = jsonArray.getJSONObject(i)
            val roomName = jsonRoom.getString("name")
            val roomSport = jsonRoom.getString("sport_type")
            val roomDate = jsonRoom.getString("date")
            val roomTime = jsonRoom.getString("time")
            val roomAddress = jsonRoom.getString("location_address")
            val id = jsonRoom.getString("id")

            roomList += Room(roomName, roomSport, roomDate, roomTime, "",
                roomAddress, "", "", 1, id.toInt())
        }
    }

    fun refreshFilters() {
        _sportTypeFilter.value = null
        _skillLevelFilter.value = null
        _roomNameFilter.value = null
        _dateFromFilter.value = null
        _dateUntilFilter.value = null
        _timeFromFilter.value = null
        _timeUntilFilter.value = null
    }

    fun roomDataGot() {
        _eventDataSuccess.value = false
    }

    fun setSportType(sport: String) {
        _sportTypeFilter.value = sport
    }

    fun setSkillLevel(skill: String) {
        _skillLevelFilter.value = skill
    }

    fun setDateFrom(date: String) {
        _dateFromFilter.value = date
    }

    fun setTimeFrom(time: String) {
        _timeFromFilter.value = time
    }

    fun setDateUntil(date: String) {
        _dateUntilFilter.value = date
    }

    fun setTimeUntil(time: String) {
        _timeUntilFilter.value = time
    }

    fun setRoomName(name: String) {
        _roomNameFilter.value = name
    }
}