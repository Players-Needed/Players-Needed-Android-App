package ro.pub.acs.playersneeded.roomscreen

import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.osmdroid.config.Configuration
import ro.pub.acs.playersneeded.R
import ro.pub.acs.playersneeded.databinding.FragmentRoomBinding
import ro.pub.acs.playersneeded.player.PlayerAdapter
import ro.pub.acs.playersneeded.roomcreation.CreateRoomFragmentDirections

/**
 * Class used to reflect the room fragment, the screen that
 * displays the details, players and chat of a particular room
 */
class RoomFragment : Fragment() {
    private lateinit var viewModel: RoomViewModel
    private lateinit var viewModelFactory: RoomViewModelFactory
    private lateinit var binding: FragmentRoomBinding
    private lateinit var playerRecyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        Configuration.getInstance().load(context,
            PreferenceManager.getDefaultSharedPreferences(context))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            ro.pub.acs.playersneeded.R.layout.fragment_room, container, false)

        viewModelFactory = RoomViewModelFactory(
            RoomFragmentArgs.fromBundle(requireArguments()).token,
            RoomFragmentArgs.fromBundle(requireArguments()).id
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[RoomViewModel::class.java]

        // perform the request that gets the room info from backend
        viewModel.getRoomDetails()

        // set up the player recycler view
        playerRecyclerView = binding.playerRecyclerView
        playerRecyclerView.layoutManager = LinearLayoutManager(context)
        playerRecyclerView.setHasFixedSize(true)

        // introduce spacing between elements of recycler view
        val dividerItemDecoration =
            DividerItemDecoration(playerRecyclerView.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            context?.let {
                ContextCompat.getDrawable(it, ro.pub.acs.playersneeded.R.drawable.divider_players)
            }!!
        )
        playerRecyclerView.addItemDecoration(dividerItemDecoration)

        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Go back arrow action
        binding.imageViewbackArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        // Hide keyboard when clicking anywhere on the layout
        binding.roomConstraintLayout.setOnClickListener {
            hideSoftKeyboard(it)
        }

        // track the result of the GET request for room info
        viewModel.getDetailsResult.observe(viewLifecycleOwner) {
            if (it) {
                setRoomDetails()
                setPlayerAdapter()
                viewModel.reinitializeDetailsResult()

                // perform the request that gets info about the player logged in
                viewModel.getSelfPlayer()
            }
        }

        // track the result of the GET request for logged in player info
        viewModel.getSelfPlayerResult.observe(viewLifecycleOwner) {
            if (it) {
                setButtonOverview()
                viewModel.reinitializeSelfPlayerResult()
            }
        }

        // track the result of the POST request for joining a players room
        viewModel.joinRoomResult.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getRoomDetails()
                viewModel.getSelfPlayer()
                viewModel.reinitializeJoinRoomResult()
            }
        }

        // track the result of the POST request for exiting a players room
        viewModel.exitRoomResult.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.getRoomDetails()
                viewModel.getSelfPlayer()
                viewModel.reinitializeExitRoomResult()
            }
        }

        // track the result of the DELETE request for deleting a players room
        viewModel.deleteRoomResult.observe(viewLifecycleOwner) {
            if (it) {
                val action =
                    RoomFragmentDirections.actionRoomFragmentToYourRoomsFragment(viewModel.token)
                NavHostFragment.findNavController(this).navigate(action)
                viewModel.getSelfPlayer()
                viewModel.reinitializeDeleteRoomResult()
            }
        }

        // the logic for the three buttons (overview, players, chat)
        binding.overviewButton.setOnClickListener {
            binding.layoutOverview.visibility = View.VISIBLE
            binding.layoutPlayers.visibility = View.GONE
            binding.layoutChat.visibility = View.GONE
        }

        binding.playersButton.setOnClickListener {
            binding.layoutOverview.visibility = View.GONE
            binding.layoutPlayers.visibility = View.VISIBLE
            binding.layoutChat.visibility = View.GONE
        }

        binding.chatButton.setOnClickListener {
            binding.layoutOverview.visibility = View.GONE
            binding.layoutPlayers.visibility = View.GONE
            binding.layoutChat.visibility = View.VISIBLE
        }

        // join room operation
        binding.joinRoomButton.setOnClickListener {
            viewModel.joinRoom()
        }

        // exit room operation
        binding.exitRoomButton.setOnClickListener {
            viewModel.exitRoom()
        }

        // delete room operation
        binding.deleteRoomButton.setOnClickListener {
            viewModel.deleteRoom()
        }
    }

    /**
     * Function that sets the content of the recycler view when
     * the room detail GET request succeeds
     */
    private fun setPlayerAdapter() {
        adapter = PlayerAdapter(viewModel.playersList, viewModel.token, this)
        playerRecyclerView.adapter = adapter
    }

    /**
     * The function sets the visibility of the buttons according
     * to the role the player has in the room (admin or not)
     */
    private fun setButtonOverview() {
        if (!viewModel.checkPlayerId(viewModel.idPlayer.value)) {
            binding.joinRoomButton.visibility = View.VISIBLE
            binding.editRoomButton.visibility = View.GONE
            binding.exitRoomButton.visibility = View.GONE
            binding.deleteRoomButton.visibility = View.GONE
        } else if (viewModel.idPlayer.value != viewModel.idPlayerAdmin.value) {
            binding.joinRoomButton.visibility = View.GONE
            binding.editRoomButton.visibility = View.GONE
            binding.exitRoomButton.visibility = View.VISIBLE
            binding.deleteRoomButton.visibility = View.VISIBLE
        } else if (viewModel.idPlayer.value == viewModel.idPlayerAdmin.value) {
            binding.joinRoomButton.visibility = View.GONE
            binding.editRoomButton.visibility = View.VISIBLE
            binding.exitRoomButton.visibility = View.VISIBLE
            binding.deleteRoomButton.visibility = View.VISIBLE
        }
    }

    /**
     * Function that sets the overview details of the room
     * and that changes the image according to the sport type
     */
    private fun setRoomDetails() {
        binding.textViewOverviewName.text = viewModel.roomName.value
        binding.textViewOverviewSport.text = viewModel.sportType.value
        binding.textViewOverviewSkill.text = viewModel.skillLevel.value
        binding.textViewOverviewDate.text = viewModel.date.value
        binding.textViewOverviewTime.text = viewModel.time.value
        binding.textViewOverviewExtraDetails.text = viewModel.extraDetails.value
        binding.textViewOverviewLocation.text = viewModel.address.value

        if (viewModel.sportType.value == "Football") {
            binding.defaultRoomImage.setImageResource(R.drawable.soccer)
        } else if (viewModel.sportType.value == "Tenis") {
            binding.defaultRoomImage.setImageResource(R.drawable.tennis)
        } else if (viewModel.sportType.value == "Jogging") {
            binding.defaultRoomImage.setImageResource(R.drawable.jogging)
        } else if (viewModel.sportType.value == "Gym") {
            binding.defaultRoomImage.setImageResource(R.drawable.gym)
        } else if (viewModel.sportType.value == "Basketball") {
            binding.defaultRoomImage.setImageResource(R.drawable.basketball)
        }
    }

    /**
     * Function tha hides the keyboard once we click anywehere else
     * on the layout of the fragment
     */
    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = context?.let {
            ContextCompat.getSystemService(
                it, InputMethodManager::class
                    .java)
        }!!
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}