package ro.pub.acs.playersneeded.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserHomeScreenViewModelFactory(private val token: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserHomeScreenViewModel::class.java)) {
            return UserHomeScreenViewModel(token) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}