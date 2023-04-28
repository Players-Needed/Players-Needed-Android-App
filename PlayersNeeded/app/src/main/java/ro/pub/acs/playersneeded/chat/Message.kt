package ro.pub.acs.playersneeded.chat

/**
 * This class defines the components of an instance
 * of message that gets displayed in the chat of a
 * particular room
 */
class Message(
    private var _message: String,
    private var _date: String,
    private var _time: String,
    private var _player: String,
    var today: Boolean
) {
    val message: String
        get() = _message

    val date: String
        get() = _date

    val time: String
        get() = _time

    val player: String
        get() = _player
}