package ro.pub.acs.playersneeded.player

import okhttp3.internal.userAgent

class Player(
    private var _id: Int,
    private var _email: String,
    private var _username: String,
    private var _experience: Int,
    private var _level: Int
) {
    val id: Int
        get() = _id

    val email: String
        get() = _email

    val username: String
        get() = _username

    val experience: Int
        get() = _experience

    val level: Int
        get() = _level
}