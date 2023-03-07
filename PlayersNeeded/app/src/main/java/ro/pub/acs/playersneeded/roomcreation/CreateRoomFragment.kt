package ro.pub.acs.playersneeded.roomcreation

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.StrictMode
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.adapters.CalendarViewBindingAdapter.setDate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import ro.pub.acs.playersneeded.R
import ro.pub.acs.playersneeded.databinding.FragmentCreateRoomBinding
import java.text.SimpleDateFormat
import java.util.*


class CreateRoomFragment : Fragment() {
    private lateinit var viewModel: CreateRoomViewModel
    private lateinit var viewModelFactory: CreateRoomViewModelFactory
    private lateinit var binding: FragmentCreateRoomBinding

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
            inflater, ro.pub.acs.playersneeded.R.layout.fragment_create_room, container,
            false)

        viewModelFactory = CreateRoomViewModelFactory(
            CreateRoomFragmentArgs.fromBundle
            (requireArguments()).token)
        viewModel = ViewModelProvider(this, viewModelFactory)[CreateRoomViewModel::class.java]

        /* set up the dropdown for selecting the sport type */
        val listSport = ArrayList<String>(listOf(*resources.getStringArray(ro.pub.acs.playersneeded.R.array.sports)))
        val adapterSport: ArrayAdapter<String> = object: ArrayAdapter<String>(
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

        binding.spinnerSport.adapter = adapterSport

        /* set up the dropdown for selecting the skill level */
        val listSkill = ArrayList<String>(listOf(*resources.getStringArray(ro.pub.acs.playersneeded.R.array.skills)))
        val adapterSkill: ArrayAdapter<String> = object: ArrayAdapter<String>(
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

        val dialog = activity?.let { Dialog(it) }

        binding.textViewLocation.setOnClickListener {
            setLocation(it, dialog)
        }

        binding.createRoomButton.setOnClickListener {
            viewModel.sendRoomDetails()
        }
    }

    private fun setLocation(view: View, dialog: Dialog?) {
        dialog?.setContentView(R.layout.fragment_map_view);
        if (dialog != null) {
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        };
        dialog?.setCancelable(false)

        val ctx: Context? = context
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        val map = dialog?.findViewById(R.id.map) as MapView
        map.tileProvider.clearTileCache()
        Configuration.getInstance().cacheMapTileCount = 12.toShort()
        Configuration.getInstance().cacheMapTileOvershoot = 12.toShort()
        map.setTileSource(object : OnlineTileSourceBase(
            "",
            1,
            20,
            512,
            ".png",
            arrayOf("https://a.tile.openstreetmap.org/")
        ) {
            override fun getTileURLString(pMapTileIndex: Long): String {
                return (baseUrl
                        + MapTileIndex.getZoom(pMapTileIndex)
                        + "/" + MapTileIndex.getX(pMapTileIndex)
                        + "/" + MapTileIndex.getY(pMapTileIndex)
                        + mImageFilenameEnding)
            }
        })

        map.setMultiTouchControls(true)
        val mapController: IMapController = map.controller
        val startPoint: GeoPoint = GeoPoint(44.44891, 26.05214)
        mapController.setZoom(11.0)
        mapController.setCenter(startPoint)
        map.invalidate()

        val setLocationButton = dialog.findViewById(R.id.addLocationButton) as Button

        setLocationButton.setOnClickListener {
            writeLocation(map, dialog)
        }

        viewModel.lat.value?.let { viewModel.lon.value?.let { it1 -> GeoPoint(it, it1) } }
            ?.let { createMarker(map, it) }

        dialog.show()

        val mReceive: MapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                createMarker(map, p)
                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                //do whatever you need here
                return false
            }
        }
        val evOverlay = MapEventsOverlay(mReceive)
        map.overlays.add(evOverlay)
    }

    @SuppressLint("SetTextI18n")
    private fun writeLocation(map: MapView, dialog: Dialog) {
        val latitude = (map.overlays[map.overlays.size - 1] as Marker).position.latitude
        val longitude = (map.overlays[map.overlays.size - 1] as Marker).position.longitude

        viewModel.lat.value = latitude
        viewModel.lon.value = longitude

        binding.textViewLocation.text = "Lat. $latitude Lon. $longitude"

        var addresses: List<Address>? = null
        val geocoder: Geocoder? = context?.let { Geocoder(it, Locale.getDefault()) }

        if (geocoder != null) {
            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            ) as List<Address>
        }

        val address: String =
            addresses!![0].getAddressLine(0)

        if (addresses!![0].locality != null) {
            val city: String = addresses!![0].locality
        }

        binding.createRoomTextInputLayoutRoomAddress.setText(address)

        Log.i("Create Room", viewModel.lat.value.toString() + " " +
                viewModel.lon.value.toString())
        dialog.hide()
    }

    private fun createMarker(map: MapView, p: GeoPoint) {
        val myMarker = Marker(map)

        for (i in 0 until map.overlays.size) {
            if (i >= map.overlays.size)
                break
            val overlay: Overlay = map.overlays[i]
            if (overlay is Marker && (overlay as Marker).id == "selectedLocation") {
                map.overlays.remove(overlay)
            }
        }
        map.invalidate()

        myMarker.position = GeoPoint(p.latitude, p.longitude)
        myMarker.setAnchor((Marker.ANCHOR_TOP + 0.25).toFloat(), (Marker.ANCHOR_TOP + 0.25)
            .toFloat())
        myMarker.id = "selectedLocation"
        myMarker.isDraggable = true
        map.overlays.add(myMarker)
        map.invalidate()
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