package ro.pub.acs.playersneeded.user

import androidx.lifecycle.ViewModel
import ro.pub.acs.playersneeded.news.News

class UserHomeScreenViewModel(tokenArgument: String) : ViewModel() {
    var newsList : Array<News> = arrayOf(News(), News(), News())

    private var _token = tokenArgument
    val token: String
        get() = _token
}