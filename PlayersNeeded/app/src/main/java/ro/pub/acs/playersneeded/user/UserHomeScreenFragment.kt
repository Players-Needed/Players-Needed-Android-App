package ro.pub.acs.playersneeded.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NavUtils
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ro.pub.acs.playersneeded.R
import ro.pub.acs.playersneeded.databinding.FragmentUserHomeScreenBinding
import ro.pub.acs.playersneeded.news.NewsAdapter
import ro.pub.acs.playersneeded.roomscreen.RoomFragmentDirections

/**
 * Class used to define the landing screen for a user that is
 * already logged in, or for a user that has just signed up or
 * logged in.
 */
class UserHomeScreenFragment : Fragment() {
    private lateinit var viewModel: UserHomeScreenViewModel
    private lateinit var viewModelFactory: UserHomeScreenViewModelFactory
    private lateinit var binding: FragmentUserHomeScreenBinding
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<NewsAdapter.NewsViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_user_home_screen, container,
            false)

        // display the available news for a certain user
        displayNews()

        // get data about the current user
        viewModel.getSelfPlayer()

        setHasOptionsMenu(false)
        return binding.root
    }

    /**
     * This function is used to fetch the news from an API
     * and display them using a recycler view
     */
    private fun displayNews() {
        // TODO fetch the news from an API

        viewModelFactory = UserHomeScreenViewModelFactory(UserHomeScreenFragmentArgs.fromBundle
            (requireArguments()).token)
        viewModel = ViewModelProvider(this, viewModelFactory)[UserHomeScreenViewModel::class.java]

        newsRecyclerView = binding.newsRecyclerView
        newsRecyclerView.layoutManager = LinearLayoutManager(context)
        newsRecyclerView.setHasFixedSize(true)

        // introduce some space inside the recycler view between
        // each news view
        val dividerItemDecoration =
            DividerItemDecoration(newsRecyclerView.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            context?.let {
                ContextCompat.getDrawable(it, ro.pub.acs.playersneeded.R.drawable.divider)
            }!!
        )
        newsRecyclerView.addItemDecoration(dividerItemDecoration)

        adapter = NewsAdapter(viewModel.newsList)
        newsRecyclerView.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeScreenViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        viewModel.getSelfPlayerResult.observe(viewLifecycleOwner) {
            if (viewModel.getSelfPlayerResult.value == true) {
                // create room button action
                // transition to create room fragment
                binding.createRoomButton.setOnClickListener{
                    createRoom()
                }

                // join room button action
                // transition to join room fragment
                binding.joinRoomButton.setOnClickListener{
                    joinRoom()
                }

                // your rooms button action
                // transition to your rooms fragment
                binding.yourRoomsButton.setOnClickListener{
                    yourRooms()
                }

                // player icon action
                // transition to player fragment
                binding.playerIcon.setOnClickListener {
                    val action = UserHomeScreenFragmentDirections
                        .actionUserHomeScreenFragmentToPlayerFragment(
                            viewModel.usernamePlayer.value!!, viewModel.token)
                    NavHostFragment.findNavController(this).navigate(action)
                }
            }
        }

        // go back arrow action
        // transition to home screen
        binding.imageViewbackArrow.setOnClickListener {
            val action =
                UserHomeScreenFragmentDirections.actionUserHomeScreenFragmentToHomeScreenFragment()
            NavHostFragment.findNavController(this).navigate(action)
        }
    }

    private fun yourRooms() {
        val action =
            UserHomeScreenFragmentDirections
                .actionUserHomeScreenFragmentToYourRoomsFragment(viewModel.token,
                    viewModel.usernamePlayer.value!!)
        NavHostFragment.findNavController(this).navigate(action)
    }

    private fun joinRoom() {
        val action =
            UserHomeScreenFragmentDirections
                .actionUserHomeScreenFragmentToJoinRoomFragment(viewModel.token,
                    viewModel.usernamePlayer.value!!)
        NavHostFragment.findNavController(this).navigate(action)
    }

    private fun createRoom() {
        val action =
            UserHomeScreenFragmentDirections
                .actionUserHomeScreenFragmentToCreateRoomFragment(viewModel.token,
                    viewModel.usernamePlayer.value!!)
        NavHostFragment.findNavController(this).navigate(action)
    }
}