package ro.pub.acs.playersneeded.room

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import ro.pub.acs.playersneeded.R
import ro.pub.acs.playersneeded.joinroom.JoinRoomFragment
import ro.pub.acs.playersneeded.joinroom.JoinRoomFragmentDirections
import ro.pub.acs.playersneeded.roomcreation.CreateRoomFragment
import ro.pub.acs.playersneeded.roomcreation.CreateRoomFragmentDirections
import ro.pub.acs.playersneeded.yourrooms.YourRoomsFragment
import ro.pub.acs.playersneeded.yourrooms.YourRoomsFragmentDirections

class RoomAdapter(
    private val roomList: Array<Room>,
    private val token: String,
    private val fragment: Fragment,
    private val usernamePlayer: String
) :
    RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {
    class RoomViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val roomName: TextView = view.findViewById<TextView>(R.id.roomTextViewRoomName)
        val datetime: TextView = view.findViewById<TextView>(R.id.roomTextViewDateTime)
        val address: TextView = view.findViewById<TextView>(R.id.roomTextViewAddress)
        val image: ImageView = view.findViewById<ImageView>(R.id.sportsImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomAdapter.RoomViewHolder {
        val roomView = LayoutInflater.from(parent.context).inflate(
            R.layout.room_view, parent,
            false)
        return RoomAdapter.RoomViewHolder(roomView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RoomAdapter.RoomViewHolder, position: Int) {
        val currentItem = roomList[position]

        holder.roomName.text = currentItem.name
        holder.datetime.text = currentItem.date + " " + currentItem.time
        holder.address.text = currentItem.address

        if (currentItem.sport == "Football") {
            holder.image.setImageResource(R.drawable.soccer)
        } else if (currentItem.sport == "Tenis") {
            holder.image.setImageResource(R.drawable.tennis)
        } else if (currentItem.sport == "Jogging") {
            holder.image.setImageResource(R.drawable.jogging)
        } else if (currentItem.sport == "Gym") {
            holder.image.setImageResource(R.drawable.gym)
        } else if (currentItem.sport == "Basketball") {
            holder.image.setImageResource(R.drawable.basketball)
        }

        holder.itemView.setOnClickListener {
            if (fragment is JoinRoomFragment) {
                val action =
                    JoinRoomFragmentDirections
                        .actionJoinRoomFragmentToRoomFragment(currentItem.id, token, usernamePlayer)
                NavHostFragment.findNavController(fragment).navigate(action)
            } else if (fragment is YourRoomsFragment) {
                val action =
                    YourRoomsFragmentDirections
                        .actionYourRoomsFragmentToRoomFragment(currentItem.id, token, usernamePlayer)
                NavHostFragment.findNavController(fragment).navigate(action)
            } else if (fragment is CreateRoomFragment) {
                val action =
                    CreateRoomFragmentDirections
                        .actionCreateRoomFragmentToRoomFragment(currentItem.id, token, usernamePlayer)
                NavHostFragment.findNavController(fragment).navigate(action)
            }
        }
    }

    override fun getItemCount(): Int {
        return roomList.size
    }
}