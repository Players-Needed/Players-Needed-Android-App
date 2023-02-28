package ro.pub.acs.playersneeded.roomcreation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CreateRoomViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateRoomViewModel::class.java)) {
            return CreateRoomViewModel(token) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}