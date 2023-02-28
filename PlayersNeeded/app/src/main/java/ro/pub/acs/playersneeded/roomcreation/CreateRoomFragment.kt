package ro.pub.acs.playersneeded.roomcreation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import ro.pub.acs.playersneeded.R
import ro.pub.acs.playersneeded.databinding.FragmentCreateRoomBinding
import ro.pub.acs.playersneeded.user.UserHomeScreenFragmentArgs
import ro.pub.acs.playersneeded.user.UserHomeScreenViewModel
import ro.pub.acs.playersneeded.user.UserHomeScreenViewModelFactory


class CreateRoomFragment : Fragment() {
    private lateinit var viewModel: CreateRoomViewModel
    private lateinit var viewModelFactory: CreateRoomViewModelFactory
    private lateinit var binding: FragmentCreateRoomBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_create_room, container,
            false)

        viewModelFactory = CreateRoomViewModelFactory(
            CreateRoomFragmentArgs.fromBundle
            (requireArguments()).token!!)
        viewModel = ViewModelProvider(this, viewModelFactory)[CreateRoomViewModel::class.java]

        setHasOptionsMenu(false)
        return binding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createRoomViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.createRoomConstraintLayout.setOnClickListener{
            hideSoftKeyboard(it)
        }

        val adapter: ArrayAdapter<String>? =
            context?.let { ArrayAdapter<String>(it, android.R.layout.simple_spinner_item, R.string.sport) }

        binding.spinnerSport.adapter = adapter
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