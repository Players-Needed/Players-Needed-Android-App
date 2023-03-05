package ro.pub.acs.playersneeded.roomcreation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreateRoomViewModel(tokenArgument: String) : ViewModel() {
    private var _token = tokenArgument
    val token: String
        get() = _token

    private var _lat = MutableLiveData<Double>()
    val lat: MutableLiveData<Double>
        get() = _lat

    private var _lon = MutableLiveData<Double>()
    val lon: MutableLiveData<Double>
        get() = _lon
}