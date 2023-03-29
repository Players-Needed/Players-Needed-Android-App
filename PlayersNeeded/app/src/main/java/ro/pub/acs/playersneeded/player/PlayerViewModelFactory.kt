package ro.pub.acs.playersneeded.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Class that takes care of the creation of the player
 * view model instance
 */
class PlayerViewModelFactory(private val token: String, private val username: String) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerViewModel::class.java)) {
            return PlayerViewModel(token, username) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}