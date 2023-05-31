package ro.pub.acs.playersneeded.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import ro.pub.acs.playersneeded.R
import ro.pub.acs.playersneeded.databinding.FragmentHomeScreenBinding

class HomeScreenFragment : Fragment() {

    private val viewModel: HomeScreenViewModel by viewModels()
    lateinit var binding: FragmentHomeScreenBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home_screen, container,
            false)

        binding.lifecycleOwner = viewLifecycleOwner

        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeScreenViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.logInButton.setOnClickListener { moveToLoginScreen() }
        binding.signUpButton.setOnClickListener{ moveSignUpScreen() }
    }

    fun moveSignUpScreen() {
        val action = HomeScreenFragmentDirections.actionHomeScreenFragmentToSignUpFragment()
        NavHostFragment.findNavController(this).navigate(action)
    }

    fun moveToLoginScreen() {
        val action = HomeScreenFragmentDirections.actionHomeScreenFragmentToLogInFragment()
        NavHostFragment.findNavController(this).navigate(action)
    }
}