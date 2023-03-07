package ro.pub.acs.playersneeded.roomcreation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateRoomViewModel(tokenArgument: String) : ViewModel() {
    private var _token = tokenArgument
    val token: String
        get() = _token

    private var _roomName = MutableLiveData<String>()
    val roomName: MutableLiveData<String>
        get() = _roomName

    private var _noPlayers = MutableLiveData<Int>()
    val noPlayers: MutableLiveData<Int>
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

    fun sendRoomDetails() {

    }
}