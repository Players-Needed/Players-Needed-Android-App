package ro.pub.acs.playersneeded.player

import android.app.Dialog
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
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

        // get details about the player whose name we have already
        viewModel.getPlayer()

        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // create dialog instance - mainly for setting details
        val dialog = activity?.let { Dialog(it) }

        // Go back arrow action
        binding.imageViewbackArrow.setOnClickListener {
            findNavController().popBackStack()
        }

        // Hide keyboard when clicking anywhere on the layout
        binding.playerScreenConstraintLayout.setOnClickListener {
            hideSoftKeyboard(it)
        }

        // handle edit details button action
        binding.editPlayerDetails.setOnClickListener {
            editPlayerDetails(dialog)
        }

        // handle logout button action
        binding.logOutPlayer.setOnClickListener {
            viewModel.logOutPlayer()
        }

        // handle delete account button action
        binding.deleteAccount.setOnClickListener {
            viewModel.deleteAccountPlayer()
        }

        // handle change password button action
        binding.changePassword.setOnClickListener {
            showDialogChangePassword(dialog)
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

        // track the result of the PUT request for player info
        viewModel.editPlayerDetailsResult.observe(viewLifecycleOwner) {
            if (it) {
                setTextViewPlayerDetails()
                viewModel.reinitializeEditPlayerDetailsResult()
            }
        }

        // track the result of the POST request for logging out the player
        viewModel.logOutPlayerResult.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.reinitializeLogOutPlayerResult()
                val action =
                    PlayerFragmentDirections.actionPlayerFragmentToHomeScreenFragment()
                NavHostFragment.findNavController(this).navigate(action)
            }
        }

        // track the result of the DELETE request for deleting a players account
        viewModel.deleteAccountPlayerResult.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.reinitializeDeleteAccountPlayerResult()
                val action =
                    PlayerFragmentDirections.actionPlayerFragmentToHomeScreenFragment()
                NavHostFragment.findNavController(this).navigate(action)
            }
        }

        // track the result of the POST request for changing the password of a players account
        viewModel.changePasswordPlayerResult.observe(viewLifecycleOwner) {
            if (it) {
                viewModel.reinitializeChangePasswordPlayerResult()
            }
        }
    }

    /**
     * Function used to display a dialog window in order to set the new
     * user password
     */
    private fun showDialogChangePassword(dialog: Dialog?) {
        dialog?.setContentView(ro.pub.acs.playersneeded.R.layout.fragment_change_password);
        if (dialog != null) {
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        };
        dialog?.setCancelable(true)
        dialog?.show()

        val saveChangesButton = dialog?.findViewById(ro.pub.acs.playersneeded.R.id
            .applyPasswordChangesButton) as Button

        val newPassword = dialog.findViewById(ro.pub.acs.playersneeded.R.id
            .newPassword) as TextView
        val confirmNewPassword = dialog.findViewById(ro.pub.acs.playersneeded.R.id
            .confirmNewPassword) as TextView

        saveChangesButton.setOnClickListener {
            dialog.hide()

            viewModel.changePasswordPlayer(newPassword.text, confirmNewPassword.text)
        }
    }

    /**
     * Function used to display a dialog window in order to set the new
     * user details
     */
    private fun editPlayerDetails(dialog: Dialog?) {
        dialog?.setContentView(ro.pub.acs.playersneeded.R.layout.fragment_edit_player_details);
        if (dialog != null) {
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        };
        dialog?.setCancelable(true)
        dialog?.show()

        val saveChangesButton = dialog?.findViewById(ro.pub.acs.playersneeded.R.id
            .applyChangesButton) as Button

        val newUsername = dialog.findViewById(ro.pub.acs.playersneeded.R.id
            .editUsername) as TextView
        val newFirstName = dialog.findViewById(ro.pub.acs.playersneeded.R.id
            .editFirstName) as TextView
        val newLastName = dialog.findViewById(ro.pub.acs.playersneeded.R.id
            .editLastName) as TextView
        val newEmail = dialog.findViewById(ro.pub.acs.playersneeded.R.id
            .editEmail) as TextView

        saveChangesButton.setOnClickListener {
            dialog.hide()

            viewModel.editPlayerDetails(newFirstName.text, newLastName.text, newUsername.text,
                newEmail.text)
        }
    }

    /**
     * Function used to update the labels with the details
     * of the user that is viewed
     */
    private fun setTextViewPlayerDetails() {
        binding.playerFirstName.text = viewModel.firstNamePlayer.value
        binding.playerLastName.text = viewModel.lastNamePlayer.value
        binding.playerUsername.text = viewModel.usernamePlayer
        binding.playerEmail.text = viewModel.emailPlayer.value
        binding.playerLevel.text = viewModel.levelPlayer.value.toString()
        binding.playerExperience.text = viewModel.experiencePlayer.value.toString()
    }

    /**
     * Function used to set the visibility of the buttons
     * from the current page
     */
    private fun setButtons() {
        Log.i("Player Fragment", viewModel.usernamePlayer + " " + viewModel.usernameSelfPlayer.value)

        if (viewModel.usernamePlayer == viewModel.usernameSelfPlayer.value) {
            binding.editPlayerDetails.visibility = View.VISIBLE
            binding.logOutPlayer.visibility = View.VISIBLE
            binding.deleteAccount.visibility = View.VISIBLE
            binding.changePassword.visibility = View.VISIBLE
        } else {
            binding.editPlayerDetails.visibility = View.GONE
            binding.logOutPlayer.visibility = View.GONE
            binding.deleteAccount.visibility = View.GONE
            binding.changePassword.visibility = View.GONE
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