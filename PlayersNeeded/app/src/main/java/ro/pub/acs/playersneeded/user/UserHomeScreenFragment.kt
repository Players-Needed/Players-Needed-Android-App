package ro.pub.acs.playersneeded.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ro.pub.acs.playersneeded.R
import ro.pub.acs.playersneeded.databinding.FragmentUserHomeScreenBinding
import ro.pub.acs.playersneeded.news.NewsAdapter


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

        viewModelFactory = UserHomeScreenViewModelFactory(UserHomeScreenFragmentArgs.fromBundle
            (requireArguments()).token!!)
        viewModel = ViewModelProvider(this, viewModelFactory)[UserHomeScreenViewModel::class.java]

        newsRecyclerView = binding.newsRecyclerView
        newsRecyclerView.layoutManager = LinearLayoutManager(context)
        newsRecyclerView.setHasFixedSize(true)

        adapter = NewsAdapter(viewModel.newsList)
        newsRecyclerView.adapter = adapter

        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.homeScreenViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.createRoomButton.setOnClickListener{ createRoom() }
        binding.joinRoomButton.setOnClickListener{ joinRoom() }
        binding.yourRoomsButton.setOnClickListener{ yourRooms() }
    }

    private fun yourRooms() {
        val action =
            UserHomeScreenFragmentDirections
                .actionUserHomeScreenFragmentToYourRoomsFragment(viewModel.token)
        NavHostFragment.findNavController(this).navigate(action)
    }

    private fun joinRoom() {
        TODO("Not yet implemented")
    }

    private fun createRoom() {
        val action =
            UserHomeScreenFragmentDirections
                .actionUserHomeScreenFragmentToCreateRoomFragment(viewModel.token)
        NavHostFragment.findNavController(this).navigate(action)
    }
}