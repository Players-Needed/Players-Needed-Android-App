package ro.pub.acs.playersneeded.player

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import ro.pub.acs.playersneeded.R
import ro.pub.acs.playersneeded.joinroom.JoinRoomFragmentDirections

/**
* This class is used to create entries that are displayed
* inside the PlayerRecyclerView in the Players Menu of the
* Room Screen
*/
class PlayerAdapter(
    private val playerList: Array<Player>,
    private val token: String,
    private val fragment: Fragment,
    private val usernamePlayer: String
) :
    RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>() {
    class PlayerViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val playerName: TextView = view.findViewById<TextView>(R.id.playerTextViewName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            PlayerAdapter.PlayerViewHolder {
        val playerView = LayoutInflater.from(parent.context).inflate(
            R.layout.player_view, parent,
            false)
        return PlayerAdapter.PlayerViewHolder(playerView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PlayerAdapter.PlayerViewHolder, position: Int) {
        val currentItem = playerList[position]

        holder.playerName.text = currentItem.username

        holder.itemView.setOnClickListener {
            val action =
                JoinRoomFragmentDirections
                    .actionJoinRoomFragmentToRoomFragment(currentItem.id, token, usernamePlayer)
            NavHostFragment.findNavController(fragment).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return playerList.size
    }
}