package ro.pub.acs.playersneeded.yourrooms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ro.pub.acs.playersneeded.roomcreation.CreateRoomViewModel

class YourRoomsViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(YourRoomsViewModel::class.java)) {
            return YourRoomsViewModel(token) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}