package ro.pub.acs.playersneeded.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import ro.pub.acs.playersneeded.api.NewsRecommenderApi
import ro.pub.acs.playersneeded.api.PlayersNeededApi
import ro.pub.acs.playersneeded.news.News
import ro.pub.acs.playersneeded.room.Room

class UserHomeScreenViewModel(tokenArgument: String) : ViewModel() {
    lateinit var newsList : Array<News>

    private var _token = tokenArgument
    val token: String
        get() = _token

    private var _idPlayer = MutableLiveData<Int>()
    val idPlayer: MutableLiveData<Int>
        get() = _idPlayer

    private var _usernamePlayer = MutableLiveData<String>()
    val usernamePlayer: MutableLiveData<String>
        get() = _usernamePlayer

    private var _getSelfPlayerResult = MutableLiveData<Boolean>()
    val getSelfPlayerResult: LiveData<Boolean>
        get() = _getSelfPlayerResult

    private var _getNewsResult = MutableLiveData<Boolean>()
    val getNewsResult: LiveData<Boolean>
        get() = _getNewsResult

    /**
     * Function that gets the player that is logged in
     * It is used to determine whether the current player
     * is administrator or participant
     */
    fun getSelfPlayer() {
        val jsonObject = JSONObject()

        val headerMap = mutableMapOf<String, String>()
        headerMap["Authorization"] = "Token $_token"

        val jsonString = jsonObject.toString()

        Log.i("UserHomeScreenViewModel", _token)

        CoroutineScope(Dispatchers.IO).launch {
            val response = PlayersNeededApi.retrofitService.getSelfPlayer(headerMap)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    Log.i("PlayerViewModel", prettyJson)

                    val jsonObj = JSONObject(prettyJson)
                    _idPlayer.value = jsonObj.getString("id").toInt()
                    _usernamePlayer.value = jsonObj.getString("username").toString()

                    _getSelfPlayerResult.value = true
                }
                else {
                    Log.i("PlayerViewModel", response.code().toString() + " "
                            + response.message() + "\n" + jsonString)

                    _getSelfPlayerResult.value = false
                }
            }
        }
    }

    /**
     * Function that get the news that are recommended
     * to the player that is currently logged in
     */
    fun getNews() {
        Log.i("UserHomeScreenViewModel", _token)

        val arguments: MutableMap<String, String> = mutableMapOf()
        arguments["username"] = _usernamePlayer.value.toString()

        CoroutineScope(Dispatchers.IO).launch {
            val response = NewsRecommenderApi.retrofitService.getNews(arguments)

            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    val gson = GsonBuilder().setPrettyPrinting().create()
                    val prettyJson = gson.toJson(
                        JsonParser.parseString(
                            response.body()
                                ?.string()
                        )
                    )

                    addNews(prettyJson)
                    _getNewsResult.value = true

                    Log.i("UserHomeScreenViewModel", prettyJson)
                } else {
                    Log.i(
                        "UserHomeScreenViewModel", response.code().toString() + " "
                                + response.message()
                    )

                    _getNewsResult.value = false
                }
            }
        }
    }

    /**
     * Function that adds the news fetched from the recommender server
     * to the news list that gets displayed in the recycler view
     */
    private fun addNews(prettyJson: String?) {
        val jsonArray = JSONArray(prettyJson)

        newsList = arrayOf()

        for (i in 0 until jsonArray.length()) {
            val jsonNews = jsonArray.getJSONObject(i)

            Log.i("UserHomeScreenViewModel", jsonNews.toString())

            val title = jsonNews.getString("title")
            val url = jsonNews.getString("url")

            var urlToImage: String
            if (jsonNews.has("urlToImage")) {
                urlToImage = jsonNews.getString("urlToImage")
                newsList += News(title, url, urlToImage)
            } else {
                newsList += News(title, url, "image")
            }
        }
    }

    // Reinitialization functions
    fun reinitializeSelfPlayerResult() {
        _getSelfPlayerResult.value = false
    }

    fun reinitializeGetNewsResult() {
        _getNewsResult.value = false
    }
}