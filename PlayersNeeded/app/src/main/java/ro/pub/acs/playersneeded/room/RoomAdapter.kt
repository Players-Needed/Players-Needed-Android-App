package ro.pub.acs.playersneeded.room

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ro.pub.acs.playersneeded.R

class RoomAdapter(private val roomList: Array<Room>) : RecyclerView.Adapter<RoomAdapter
    .RoomViewHolder>() {
    class RoomViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomAdapter.RoomViewHolder {
        val newsView = LayoutInflater.from(parent.context).inflate(
            R.layout.room_view, parent,
            false)
        return RoomAdapter.RoomViewHolder(newsView)
    }

    override fun onBindViewHolder(holder: RoomAdapter.RoomViewHolder, position: Int) {
        val currentItem = roomList[position]
    }

    override fun getItemCount(): Int {
        return roomList.size
    }
}