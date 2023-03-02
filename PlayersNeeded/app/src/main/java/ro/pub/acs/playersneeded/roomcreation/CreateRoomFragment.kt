package ro.pub.acs.playersneeded.roomcreation

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.CalendarViewBindingAdapter.setDate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ro.pub.acs.playersneeded.R
import ro.pub.acs.playersneeded.databinding.FragmentCreateRoomBinding
import java.text.SimpleDateFormat
import java.util.*


class CreateRoomFragment : Fragment() {
    private lateinit var viewModel: CreateRoomViewModel
    private lateinit var viewModelFactory: CreateRoomViewModelFactory
    private lateinit var binding: FragmentCreateRoomBinding

    @SuppressLint("ResourceType")
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
            (requireArguments()).token)
        viewModel = ViewModelProvider(this, viewModelFactory)[CreateRoomViewModel::class.java]

        /* set up the dropdown for selecting the sport type */
        val listSport = ArrayList<String>(listOf(*resources.getStringArray(R.array.sports)))
        val adapterSport: ArrayAdapter<String> = object: ArrayAdapter<String>(
            requireContext(), R.drawable.spinner_item, listSport) {
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

        binding.spinnerSport.adapter = adapterSport

        /* set up the dropdown for selecting the skill level */
        val listSkill = ArrayList<String>(listOf(*resources.getStringArray(R.array.skills)))
        val adapterSkill: ArrayAdapter<String> = object: ArrayAdapter<String>(
            requireContext(), R.drawable.spinner_item, listSkill) {
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

        binding.spinnerSkillLevel.adapter = adapterSkill

        setHasOptionsMenu(false)
        return binding.root
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createRoomViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        binding.textViewDate.text =
            (day.toString() + "/" + (month + 1) + "/" + year)

        val simpleDateFormat = SimpleDateFormat("HH:mm")
        val time = simpleDateFormat.format(c.time).toString()

        binding.textViewTime.text = time

        binding.createRoomConstraintLayout.setOnClickListener {
            hideSoftKeyboard(it)
        }

        binding.textViewDate.setOnClickListener {
            setDate(day, month, year)
        }

        binding.textViewTime.setOnClickListener {
            setTime(it, hour, minute)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setTime(view: View, hour: Int, minute: Int) {
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minuteOfHour ->
                binding.textViewTime.text = "$hourOfDay:$minuteOfHour"
            },
            hour,
            minute,
            true
        )
        timePickerDialog.show()
    }

    @SuppressLint("SetTextI18n")
    private fun setDate(day: Int, month: Int, year: Int) {
        context?.let {
            DatePickerDialog(
                it,
                { _, year, monthOfYear, dayOfMonth ->
                    binding.textViewDate.text =
                        (dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
                }, year, month, day
            )
        }?.show()
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