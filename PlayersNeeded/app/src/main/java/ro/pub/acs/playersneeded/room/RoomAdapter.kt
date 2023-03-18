package ro.pub.acs.playersneeded.room

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ro.pub.acs.playersneeded.R

class RoomAdapter(private val roomList: Array<Room>) : RecyclerView.Adapter<RoomAdapter
    .RoomViewHolder>() {
    class RoomViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val roomName: TextView = view.findViewById<TextView>(R.id.roomTextViewRoomName)
        val datetime: TextView = view.findViewById<TextView>(R.id.roomTextViewDateTime)
        val address: TextView = view.findViewById<TextView>(R.id.roomTextViewAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomAdapter.RoomViewHolder {
        val newsView = LayoutInflater.from(parent.context).inflate(
            R.layout.room_view, parent,
            false)
        return RoomAdapter.RoomViewHolder(newsView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RoomAdapter.RoomViewHolder, position: Int) {
        val currentItem = roomList[position]

        holder.roomName.text = currentItem.name
        holder.datetime.text = currentItem.date + " " + currentItem.time
        holder.address.text = currentItem.address
    }

    override fun getItemCount(): Int {
        return roomList.size
    }
}