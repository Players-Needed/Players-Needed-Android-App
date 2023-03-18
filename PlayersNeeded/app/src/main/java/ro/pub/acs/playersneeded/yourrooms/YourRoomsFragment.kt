package ro.pub.acs.playersneeded.yourrooms

import android.R
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.osmdroid.config.Configuration
import ro.pub.acs.playersneeded.databinding.FragmentYourRoomsBinding
import ro.pub.acs.playersneeded.room.RoomAdapter


class YourRoomsFragment : Fragment() {
    private lateinit var viewModel: YourRoomsViewModel
    private lateinit var viewModelFactory: YourRoomsViewModelFactory
    private lateinit var binding: FragmentYourRoomsBinding
    private lateinit var roomRecyclerView: RecyclerView
    private lateinit var adapter: RecyclerView.Adapter<RoomAdapter.RoomViewHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences
            (context))
    }

    @SuppressLint("ResourceType")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, ro.pub.acs.playersneeded.R.layout.fragment_your_rooms, container,
            false)

        viewModelFactory = YourRoomsViewModelFactory(
            YourRoomsFragmentArgs.fromBundle
                (requireArguments()).token)
        viewModel = ViewModelProvider(this, viewModelFactory)[YourRoomsViewModel::class.java]

        viewModel.populateRoomList()

        roomRecyclerView = binding.roomRecyclerView
        roomRecyclerView.layoutManager = LinearLayoutManager(context)
        roomRecyclerView.setHasFixedSize(true)

        val dividerItemDecoration =
            DividerItemDecoration(roomRecyclerView.context, DividerItemDecoration.VERTICAL)
        dividerItemDecoration.setDrawable(
            context?.let {
                ContextCompat.getDrawable(it, ro.pub.acs.playersneeded.R.drawable.divider)
            }!!
        )
        roomRecyclerView.addItemDecoration(dividerItemDecoration)

        adapter = RoomAdapter(viewModel.roomList)
        roomRecyclerView.adapter = adapter

        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageViewbackArrow.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}