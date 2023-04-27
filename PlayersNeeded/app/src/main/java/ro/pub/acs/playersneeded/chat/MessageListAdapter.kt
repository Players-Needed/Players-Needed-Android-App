package ro.pub.acs.playersneeded.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ro.pub.acs.playersneeded.R

/**
 * This class is used to create entries that are displayed
 * inside the MessageRecyclerView in the Room Chat of the
 * Room Screen
 */
class MessageListAdapter(
    var messageList: Array<Message>,
    private val usernamePlayer: String
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val viewTypeMessageSent = 1
    private val viewTypeMessageReceived = 2

    class PlayerMessageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val player: TextView = view.findViewById<TextView>(R.id.text_gchat_user_other)
        val message: TextView = view.findViewById<TextView>(R.id.text_gchat_message_other)
        val time: TextView = view.findViewById<TextView>(R.id.text_gchat_timestamp_other)
        val date: TextView = view.findViewById<TextView>(R.id.text_gchat_date_other)
    }

    class SelfPlayerMessageViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val message: TextView = view.findViewById<TextView>(R.id.text_gchat_message_me)
        val time: TextView = view.findViewById<TextView>(R.id.text_gchat_timestamp_me)
        val date: TextView = view.findViewById<TextView>(R.id.text_gchat_date_me)
    }

    override fun getItemViewType(position: Int): Int {
        val message: Message = messageList[position]

        if (message.player == usernamePlayer) {
            return viewTypeMessageSent
        }
        return viewTypeMessageReceived
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == viewTypeMessageSent) {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.self_player_message_view, parent,
                false)
            MessageListAdapter.SelfPlayerMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.player_message_view, parent,
                false)
            MessageListAdapter.PlayerMessageViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = messageList[position]

        if (holder.itemViewType == viewTypeMessageSent) {
            val messageHolder = holder as SelfPlayerMessageViewHolder

            messageHolder.date.text = currentItem.date
            messageHolder.time.text = currentItem.time
            messageHolder.message.text = currentItem.message

            if (currentItem.today) {
                messageHolder.date.visibility = View.GONE
            }
        } else {
            val messageHolder = holder as PlayerMessageViewHolder

            messageHolder.date.text = currentItem.date
            messageHolder.time.text = currentItem.time
            messageHolder.player.text = currentItem.player
            messageHolder.message.text = currentItem.message

            if (currentItem.today) {
                messageHolder.date.visibility = View.GONE
            }
        }
    }
}