package ro.pub.acs.playersneeded.roomcreation

import androidx.lifecycle.ViewModel

class CreateRoomViewModel(tokenArgument: String) : ViewModel() {
    private var _token = tokenArgument
    val token: String
        get() = _token
}