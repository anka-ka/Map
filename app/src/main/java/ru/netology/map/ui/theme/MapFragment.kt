package ru.netology.map.ui.theme

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import androidx.navigation.fragment.findNavController
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import ru.netology.map.R
import ru.netology.map.ViewModel.MapViewModel
import ru.netology.map.ViewModel.MapViewModelFactory
import ru.netology.map.databinding.FragmentMapBinding
import ru.netology.map.dto.Marker
import ru.netology.map.repository.MapRepository


class MapFragment : Fragment(R.layout.fragment_map), CameraListener {

    private lateinit var mapView: MapView
    private lateinit var viewModel: MapViewModel

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val markersData = MutableLiveData<MutableList<Pair<Point, String>>>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapView
        val repository = MapRepository()
        viewModel = ViewModelProvider(requireActivity(), MapViewModelFactory(repository))
            .get(MapViewModel::class.java)

        markersData.value = mutableListOf()
        loadMarkers()

        var startLocation = Point(59.9402, 30.315)

        mapView.map.move(
            CameraPosition(startLocation, 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 5f),
            null
        )

        viewModel.setMarker(mapView, startLocation, R.drawable.baseline_location_pin_24, requireContext())
        mapView.map.addCameraListener(this)


        mapView.map.addTapListener(object : GeoObjectTapListener {
            override fun onObjectTap(geoObjectTapEvent: GeoObjectTapEvent): Boolean {
                val geoObject = geoObjectTapEvent
                    .geoObject
                    .metadataContainer
                    .getItem(GeoObjectSelectionMetadata::class.java)
                binding.mapView.map.selectGeoObject(geoObject)
                return true

            }
        })

        mapView.map.addInputListener(object : InputListener {

            override fun onMapTap(map: Map, point: Point) {
                showMarkerInputDialog(point)
            }

            override fun onMapLongTap(p0: Map, p1: Point) {
                TODO("Not yet implemented")
            }
        })
        binding.zoomInButton.setOnClickListener {
            viewModel.zoomIn(mapView)
        }

        binding.zoomOutButton.setOnClickListener {
            viewModel.zoomOut(mapView)
        }


        binding.marksMenu.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_MarksMenuFragment)
        }

        markersData.observe(viewLifecycleOwner) { markerList ->
            markerList.forEach { (point, description) ->
                viewModel.setMarker(mapView, point, R.drawable.baseline_location_pin_24_red, requireContext())
            }
        }

    }

    private fun addMarkerAndSave(point: Point, markerName: String) {

        viewModel.setMarker(
            mapView,
            point,
            R.drawable.baseline_location_pin_24_red,
            requireContext()
        )

        val currentData = markersData.value ?: mutableListOf()
        currentData.add(Pair(point, markerName))

        markersData.value = currentData

        val sharedPreferences =
            requireActivity().getSharedPreferences("markers", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val json = Gson().toJson(currentData.map { Marker(it.first, it.second) })
        Log.d("Markers", "Saving markers: $json")
        editor.putString("marker_list", json)
        editor.apply()
    }


    private fun showMarkerInputDialog(point: Point) {
        val builder = AlertDialog.Builder(requireContext())
        val input = EditText(requireContext())
        builder.setView(input)

        builder.setTitle("Введите название маркера")
        builder.setPositiveButton("Добавить") { dialog, which ->
            val markerName = input.text.toString()
            val marker = Marker(point, markerName)
            viewModel.addMarker(marker)
            addMarkerAndSave(point, markerName)
        }
        builder.setNegativeButton("Отмена") { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun loadMarkers() {
        val sharedPreferences = requireActivity().getSharedPreferences("markers", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("marker_list", null)

        if (json != null) {
            val type = object : TypeToken<MutableList<Marker>>() {}.type
            val markerList: MutableList<Marker> = Gson().fromJson(json, type)
            Log.d("Markers", "Loaded markers: $json")
            val currentData = markersData.value ?: mutableListOf()
            markerList.forEach { marker ->
                currentData.add(Pair(marker.point, marker.description))

                viewModel.setMarker(mapView, marker.point, R.drawable.baseline_location_pin_24_red, requireContext())
            }
            markersData.value = currentData

        }
    }


    override fun onStop() {

        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {

        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        viewModel.onCameraPositionChanged(mapView, cameraPosition, cameraUpdateReason, finished)
    }

}
