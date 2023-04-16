package ro.pub.acs.playersneeded.yourrooms

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.osmdroid.config.Configuration
import ro.pub.acs.playersneeded.databinding.FragmentYourRoomsBinding
import ro.pub.acs.playersneeded.room.RoomAdapter
import ro.pub.acs.playersneeded.roomcreation.CreateRoomFragmentDirections
import ro.pub.acs.playersneeded.roomscreen.RoomFragmentDirections
import java.util.*


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
            YourRoomsFragmentArgs.fromBundle(requireArguments()).token,
            YourRoomsFragmentArgs.fromBundle(requireArguments()).usernamePlayer)
        viewModel = ViewModelProvider(this, viewModelFactory)[YourRoomsViewModel::class.java]

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

        viewModel.populateRoomList()

        viewModel.eventDataSuccess.observe(viewLifecycleOwner) {
            if (it) {

                Log.i("YourRoomsFragment", "Pulled available rooms")

                adapter = RoomAdapter(viewModel.roomList, viewModel.token, this,
                    viewModel.usernamePlayer)
                roomRecyclerView.adapter = adapter
                viewModel.roomDataGot()
            }
        }

        setHasOptionsMenu(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // go back to the previous screen (user home screen)
        binding.imageViewbackArrow.setOnClickListener {
            val action =
                YourRoomsFragmentDirections
                    .actionYourRoomsFragmentToUserHomeScreenFragment3(viewModel.token)
            NavHostFragment.findNavController(this).navigate(action)
        }

        val dialog = activity?.let { Dialog(it) }

        binding.textViewPreferences.setOnClickListener {
            setPreferences(it, dialog)
        }

        // player icon action
        // transition to player fragment
        binding.playerIcon.setOnClickListener {
            val action = YourRoomsFragmentDirections
                .actionYourRoomsFragmentToPlayerFragment(viewModel.usernamePlayer, viewModel.token)
            NavHostFragment.findNavController(this).navigate(action)
        }

        binding.yourRoomsConstraintLayout.setOnClickListener {
            hideSoftKeyboard(it)
        }
    }

    private fun setPreferences(view: View, dialog: Dialog?) {
        dialog?.setContentView(ro.pub.acs.playersneeded.R.layout.fragment_filter_preferences);
        if (dialog != null) {
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        };
        dialog?.setCancelable(true)
        dialog?.show()

        setSpinnerAdapters(dialog)

        val applyFiltersButton = dialog?.findViewById(ro.pub.acs.playersneeded.R.id.applyFiltersButton) as Button

        applyFiltersButton.setOnClickListener {
            setFilterValues(dialog)
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        val dateFromInput = dialog.findViewById(ro.pub.acs.playersneeded.R.id.textViewDateStart) as TextView

        dateFromInput.setOnClickListener {
            setDate(day, month, year, dateFromInput)
        }

        val timeFromInput = dialog.findViewById(ro.pub.acs.playersneeded.R.id.textViewTimeStart) as TextView

        timeFromInput.setOnClickListener {
            setTime(hour, minute, timeFromInput)
        }

        val dateUntilInput = dialog.findViewById(ro.pub.acs.playersneeded.R.id.textViewDateEnd) as TextView

        dateUntilInput.setOnClickListener {
            setDate(day, month, year, dateUntilInput)
        }

        val timeUntilInput = dialog.findViewById(ro.pub.acs.playersneeded.R.id.textViewTimeEnd) as TextView

        timeUntilInput.setOnClickListener {
            setTime(hour, minute, timeUntilInput)
        }
    }

    private fun setFilterValues(dialog: Dialog?) {
        val roomNameInput = dialog?.findViewById(ro.pub.acs.playersneeded.R.id.filterPreferenceInputRoomName) as EditText
        val sportTypeInput = dialog.findViewById(ro.pub.acs.playersneeded.R.id.spinnerSport) as Spinner
        val skillLevelInput = dialog.findViewById(ro.pub.acs.playersneeded.R.id.spinnerSkillLevel) as Spinner
        val dateFromInput = dialog.findViewById(ro.pub.acs.playersneeded.R.id.textViewDateStart) as TextView
        val timeFromInput = dialog.findViewById(ro.pub.acs.playersneeded.R.id.textViewTimeStart) as TextView
        val dateUntilInput = dialog.findViewById(ro.pub.acs.playersneeded.R.id.textViewDateEnd) as TextView
        val timeUntilInput = dialog.findViewById(ro.pub.acs.playersneeded.R.id.textViewTimeEnd) as TextView

        viewModel.refreshFilters()

        if (roomNameInput.text.toString() != "")
            viewModel.setRoomName(roomNameInput.text.toString())
        if (sportTypeInput.selectedItem != "Sport")
            viewModel.setSportType(sportTypeInput.selectedItem.toString())
        if (skillLevelInput.selectedItem != "Skill Level")
            viewModel.setSkillLevel(skillLevelInput.selectedItem.toString())
        if (dateFromInput.text != "Date")
            viewModel.setDateFrom(dateFromInput.text.toString())
        if (timeFromInput.text != "Time")
            viewModel.setTimeFrom(timeFromInput.text.toString())
        if (dateUntilInput.text != "Date")
            viewModel.setDateUntil(dateUntilInput.text.toString())
        if (timeUntilInput.text != "Time")
            viewModel.setTimeUntil(timeUntilInput.text.toString())

        viewModel.populateRoomList()
        dialog.hide()
    }

    private fun setSpinnerAdapters(dialog: Dialog?) {
        /* set up the dropdown for selecting the sport type */
        val listSport = ArrayList<String>(listOf(*resources.getStringArray(ro.pub.acs.playersneeded.R.array.sports)))
        val adapterSport: ArrayAdapter<String> = @SuppressLint("ResourceType")
        object: ArrayAdapter<String>(
            requireContext(), ro.pub.acs.playersneeded.R.drawable.spinner_item, listSport) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                if(position == 0)
                    view.setTextColor(Color.GRAY)
                return view
            }

        }

        adapterSport.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val sportTypeInput = dialog?.findViewById(ro.pub.acs.playersneeded.R.id.spinnerSport) as Spinner
        sportTypeInput.adapter = adapterSport

        /* set up the dropdown for selecting the skill level */
        val listSkill = ArrayList<String>(listOf(*resources.getStringArray(ro.pub.acs.playersneeded.R.array.skills)))
        val adapterSkill: ArrayAdapter<String> = @SuppressLint("ResourceType")
        object: ArrayAdapter<String>(
            requireContext(), ro.pub.acs.playersneeded.R.drawable.spinner_item, listSkill) {
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                if(position == 0)
                    view.setTextColor(Color.GRAY)
                return view
            }

        }

        adapterSkill.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val skillLevelInput = dialog.findViewById(ro.pub.acs.playersneeded.R.id.spinnerSkillLevel) as Spinner
        skillLevelInput.adapter = adapterSkill
    }

    @SuppressLint("SetTextI18n")
    private fun setDate(day: Int, month: Int, year: Int, textView: TextView) {
        var text: String

        context?.let {
            DatePickerDialog(
                it,
                { _, year, monthOfYear, dayOfMonth ->
                    run {
                        text = "$year-"
                        text += if (monthOfYear < 9)
                            "0" + (monthOfYear + 1) + "-"
                        else
                            (monthOfYear + 1).toString() + "-"
                        if (dayOfMonth < 9)
                            text += "0$dayOfMonth"
                        else
                            text += dayOfMonth
                        textView.text = text
                    }
                }, year, month, day
            )
        }?.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setTime(hour: Int, minute: Int, textView: TextView) {
        var text: String

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minuteOfHour ->
                run {
                    text = if (hourOfDay < 10) {
                        "0$hourOfDay"
                    } else {
                        hourOfDay.toString()
                    }
                    text += ":"
                    text += if (minuteOfHour < 10) {
                        "0$minuteOfHour"
                    } else {
                        minuteOfHour.toString()
                    }
                    textView.text = "$text:00"
                }
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
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