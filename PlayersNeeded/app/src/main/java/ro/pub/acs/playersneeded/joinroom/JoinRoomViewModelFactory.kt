package ro.pub.acs.playersneeded.joinroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ro.pub.acs.playersneeded.yourrooms.YourRoomsViewModel

class JoinRoomViewModelFactory(private val token: String,
                               private val usernamePlayer: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JoinRoomViewModel::class.java)) {
            return JoinRoomViewModel(token, usernamePlayer) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}