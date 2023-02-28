package ro.pub.acs.playersneeded.user

import androidx.lifecycle.ViewModel

class UserHomeScreenViewModel(tokenArgument: String) : ViewModel() {
    private var _token = tokenArgument
    val token: String
        get() = _token
}