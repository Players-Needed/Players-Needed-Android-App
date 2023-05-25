package ro.pub.acs.playersneeded.news

class News(
    private var _title: String,
    private var _url: String,
    private var _urlToImage: String,
) {
    val title: String
        get() = _title

    val url: String
        get() = _url

    val urlToImage: String
        get() = _urlToImage
}