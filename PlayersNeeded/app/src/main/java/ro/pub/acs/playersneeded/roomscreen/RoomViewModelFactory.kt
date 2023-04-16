package ro.pub.acs.playersneeded.roomscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Class that takes care of the creation of the room
 * view model instance
 */
class RoomViewModelFactory(private val token: String, private val id: Int,
                           private val usernamePlayer: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)) {
            return RoomViewModel(token, id, usernamePlayer) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}