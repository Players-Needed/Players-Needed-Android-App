package ro.pub.acs.playersneeded.roomscreen

import android.opengl.Visibility
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
import androidx.navigation.fragment.findNavController
import org.osmdroid.config.Configuration
import ro.pub.acs.playersneeded.R
import ro.pub.acs.playersneeded.databinding.FragmentRoomBinding
import ro.pub.acs.playersneeded.joinroom.JoinRoomFragmentArgs
import ro.pub.acs.playersneeded.joinroom.JoinRoomViewModel
import ro.pub.acs.playersneeded.joinroom.JoinRoomViewModelFactory

class RoomFragment : Fragment() {
    private lateinit var viewModel: RoomViewModel
    private lateinit var viewModelFactory: RoomViewModelFactory
    private lateinit var binding: FragmentRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences
            (context))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, ro.pub.acs.playersneeded.R.layout.fragment_room, container,
            false
        )

        viewModelFactory = RoomViewModelFactory(
            RoomFragmentArgs.fromBundle(requireArguments()).token,
            RoomFragmentArgs.fromBundle(requireArguments()).id
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[RoomViewModel::class.java]

        viewModel.getRoomDetails()
        viewModel.getSelfPlayer()

        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewbackArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.roomConstraintLayout.setOnClickListener {
            hideSoftKeyboard(it)
        }

        viewModel.getDetailsResult.observe(viewLifecycleOwner) {
            setRoomDetails()
        }

        viewModel.getSelfPlayerResult.observe(viewLifecycleOwner) {
            setButtonOverview()
        }
    }

    /*
    * The function sets the visibility of the buttons according
    * to the role the player has in the room (admin or not)
    * */
    private fun setButtonOverview() {
        if (viewModel.idPlayer.value == viewModel.idPlayerAdmin.value) {
            binding.joinRoomButton.visibility = View.GONE
            binding.editRoomButton.visibility = View.VISIBLE
            binding.exitRoomButton.visibility = View.VISIBLE
            binding.deleteRoomButton.visibility = View.VISIBLE
        } else {
            binding.joinRoomButton.visibility = View.VISIBLE
            binding.editRoomButton.visibility = View.GONE
            binding.exitRoomButton.visibility = View.GONE
            binding.deleteRoomButton.visibility = View.GONE
        }
    }

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

    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = context?.let {
            ContextCompat.getSystemService(
                it, InputMethodManager::class
                    .java)
        }!!
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}