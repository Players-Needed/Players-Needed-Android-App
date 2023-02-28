package ro.pub.acs.playersneeded.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import ro.pub.acs.playersneeded.R
import ro.pub.acs.playersneeded.databinding.FragmentSignUpBinding
import ro.pub.acs.playersneeded.login.LogInFragmentDirections

class SignUpFragment : Fragment() {

    private val viewModel: SignUpViewModel by viewModels()
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sign_up, container,
            false)

        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.signUpScreenButton.setOnClickListener{ onSignUpButtonClicked(it) }
        binding.signUpConstraintLayout.setOnClickListener{
            hideSoftKeyboard(it)
        }
    }

    private fun onSignUpButtonClicked(view: View) {
        val email = binding.signUpTextInputEditTextEmail.text.toString()
        val username = binding.signUpTextInputEditTextUsername.text.toString()
        val password = binding.signUpTextInputEditTextPassword.text.toString()
        val firstName = binding.signUpTextInputEditTextFirstName.text.toString()
        val lastName = binding.signUpTextInputEditTextLastName.text.toString()

        val signUpResult = viewModel.requestSignUp(email, username, password, firstName, lastName)
        setErrorLogIn(signUpResult)

        if (signUpResult) {
            val action = SignUpFragmentDirections.actionSignUpFragmentToUserHomeScreenFragment()
            NavHostFragment.findNavController(this).navigate(action)
        }

        hideSoftKeyboard(view)
    }

    private fun setErrorLogIn(error: Boolean) {
        if (error) {
            val errorToast = Toast.makeText(
                context,
                R.string.errorSignUp,
                Toast.LENGTH_SHORT
            )
            errorToast.show()
        } else {
            val errorToast = Toast.makeText(
                context,
                R.string.successfulSignUp,
                Toast.LENGTH_SHORT
            )
            errorToast.show()
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