package ro.pub.acs.playersneeded.roomscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class RoomViewModelFactory(private val token: String, private val id: Int) : ViewModelProvider
.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)) {
            return RoomViewModel(token, id) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}