package ro.pub.acs.playersneeded.player

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
import ro.pub.acs.playersneeded.databinding.FragmentPlayerBinding

/**
 * Class used to define the player fragment, the screen where
 * a user can view details about himself or about other players,
 * edit his personal details, or logout
 */
class PlayerFragment : Fragment() {
    private lateinit var viewModel: PlayerViewModel
    private lateinit var viewModelFactory: PlayerViewModelFactory
    private lateinit var binding: FragmentPlayerBinding

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
            ro.pub.acs.playersneeded.R.layout.fragment_player, container, false)

        viewModelFactory = PlayerViewModelFactory(
            PlayerFragmentArgs.fromBundle(requireArguments()).token,
            PlayerFragmentArgs.fromBundle(requireArguments()).username
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[PlayerViewModel::class.java]

        // get the player that is logged in
        viewModel.getSelfPlayer()

        // get details about the player whose id we have already
        viewModel.getPlayer()

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
        binding.playerScreenConstraintLayout.setOnClickListener {
            hideSoftKeyboard(it)
        }

        // track the result of the GET request for logged in player info
        viewModel.getSelfPlayerResult.observe(viewLifecycleOwner) {
            if (it) {
                setButtons()
                viewModel.reinitializeSelfPlayerResult()
            }
        }

        // track the result of the GET request for player info
        viewModel.getPlayerResult.observe(viewLifecycleOwner) {
            if (it) {
                setTextViewPlayerDetails()
                viewModel.reinitializePlayerResult()
            }
        }
    }

    /**
     * Function used to update the labels with the details
     * of the user that is viewed
     */
    private fun setTextViewPlayerDetails() {
        /* TODO - decomment these lines and delete the following two
        binding.playerFirstName.text = viewModel.firstNamePlayer.value
        binding.playerLastName.text = viewModel.lastNamePlayer.value
        */
        binding.playerFirstName.text = "Dorian"
        binding.playerLastName.text = "Verna"
        binding.playerUsername.text = viewModel.usernamePlayer
        binding.playerLevel.text = viewModel.levelPlayer.value.toString()
        binding.playerExperience.text = viewModel.experiencePlayer.value.toString()
    }

    /**
     * Function used to set the visibility of the buttons
     * from the current page
     */
    private fun setButtons() {
        if (viewModel.usernamePlayer == viewModel.usernameSelfPlayer.value) {
            binding.editPlayerDetails.visibility = View.VISIBLE
            binding.logOutPlayer.visibility = View.VISIBLE
        } else {
            binding.editPlayerDetails.visibility = View.GONE
            binding.logOutPlayer.visibility = View.GONE
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