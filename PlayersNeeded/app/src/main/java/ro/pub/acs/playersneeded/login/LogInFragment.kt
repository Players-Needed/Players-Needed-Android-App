package ro.pub.acs.playersneeded.login

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
import ro.pub.acs.playersneeded.MainActivity
import ro.pub.acs.playersneeded.R
import ro.pub.acs.playersneeded.databinding.FragmentLogInBinding


class LogInFragment : Fragment() {

    private val viewModel: LogInViewModel by viewModels()
    private lateinit var binding: FragmentLogInBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_log_in, container,
            false)

        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logInViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.logInScreenButton.setOnClickListener{ onLogInButtonClicked(it) }
        binding.logInConstraintLayout.setOnClickListener{
            hideSoftKeyboard(it)
        }
    }

    private fun onLogInButtonClicked(view: View) {
        val username = binding.logInTextInputEditTextUsername.text.toString()
        val password = binding.logInTextInputEditTextPassword.text.toString()

        val logInResult = viewModel.requestLogIn(username, password)
        setErrorLogIn(logInResult)

        hideSoftKeyboard(view)
    }

    private fun setErrorLogIn(error: Boolean) {
        if (error) {
            val errorToast = Toast.makeText(
                context,
                R.string.errorLogIn,
                Toast.LENGTH_SHORT
            )
            errorToast.show()
        } else {
            val errorToast = Toast.makeText(
                context,
                R.string.successfulLogIn,
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