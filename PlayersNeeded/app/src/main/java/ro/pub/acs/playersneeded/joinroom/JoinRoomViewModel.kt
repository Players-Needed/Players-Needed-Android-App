package ro.pub.acs.playersneeded.joinroom

import androidx.lifecycle.ViewModel
import ro.pub.acs.playersneeded.room.Room

class JoinRoomViewModel(tokenArgument: String) : ViewModel() {
    lateinit var roomList : Array<Room>

    private var _token = tokenArgument
    val token: String
        get() = _token

    fun populateRoomList() {
        roomList = arrayOf()

        roomList += Room("room", "sport", "date", "time", "location",
            "address", "extraDetails", "skillLevel", 2)
        roomList += Room("room", "sport", "date", "time", "location",
            "address", "extraDetails", "skillLevel", 2)
        roomList += Room("room", "sport", "date", "time", "location",
            "address", "extraDetails", "skillLevel", 2)
        roomList += Room("room", "sport", "date", "time", "location",
            "address", "extraDetails", "skillLevel", 2)
        roomList += Room("room", "sport", "date", "time", "location",
            "address", "extraDetails", "skillLevel", 2)
        roomList += Room("room", "sport", "date", "time", "location",
            "address", "extraDetails", "skillLevel", 2)
        roomList += Room("room", "sport", "date", "time", "location",
            "address", "extraDetails", "skillLevel", 2)
    }
}