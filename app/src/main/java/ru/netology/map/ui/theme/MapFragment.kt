package ru.netology.map.ui.theme

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.layers.GeoObjectTapEvent
import com.yandex.mapkit.layers.GeoObjectTapListener
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.GeoObjectSelectionMetadata
import com.yandex.mapkit.map.InputListener
import android.provider.Settings
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import ru.netology.map.R
import ru.netology.map.ViewModel.MapViewModel
import ru.netology.map.ViewModel.MapViewModelFactory
import ru.netology.map.databinding.FragmentMapBinding
import ru.netology.map.dto.Marker
import ru.netology.map.repository.MapRepository

class MapFragment : Fragment(R.layout.fragment_map), CameraListener {
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }

    private lateinit var mapView: MapView
    private lateinit var viewModel: MapViewModel

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val markersData = MutableLiveData<MutableList<Pair<Point, String>>>()

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

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

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    getCurrentLocation()
                } else {
                    Snackbar.make(
                        binding.root,
                        "Разрешение на доступ к местоположению отклонено",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Попробовать снова") {
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                        .show()
                }
            }

        mapView = binding.mapView

        val repository = MapRepository()
        viewModel = ViewModelProvider(requireActivity(), MapViewModelFactory(repository))
            .get(MapViewModel::class.java)

        markersData.value = mutableListOf()
        loadMarkers()

        checkPermissionsAndGetLocation()
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
                viewModel.setMarker(
                    mapView,
                    point,
                    R.drawable.baseline_location_pin_24_red,
                    requireContext()
                )
            }
        }
    }

    private fun checkPermissionsAndGetLocation() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }

            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(requireContext())

            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLocation = Point(location.latitude, location.longitude)
                    moveMapToLocation(currentLocation)
                    viewModel.setMarker(
                        mapView,
                        currentLocation,
                        R.drawable.baseline_location_pin_24,
                        requireContext()
                    )
                } else {
                    val startLocation = Point(59.9402, 30.315)
                    moveMapToLocation(startLocation)
                    viewModel.setMarker(
                        mapView,
                        startLocation,
                        R.drawable.baseline_location_pin_24,
                        requireContext()
                    )
                    showLocationErrorSnackbar()
                    startLocationUpdates(fusedLocationClient)
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun showLocationErrorSnackbar() {
        val snackbar = Snackbar.make(
            binding.root,
            "Не удалось получить ваше местоположение. Пожалуйста, включите службы геолокации.",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Открыть настройки") {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        snackbar.show()


        binding.root.postDelayed({
            snackbar.dismiss()
        }, 10000)
    }

    private fun moveMapToLocation(location: Point) {
        mapView.map.move(
            CameraPosition(location, 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 5f),
            null
        )
    }

    private fun startLocationUpdates(fusedLocationClient: FusedLocationProviderClient) {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.firstOrNull()?.let { location ->
                    val currentLocation = Point(location.latitude, location.longitude)
                    moveMapToLocation(currentLocation)
                    viewModel.setMarker(
                        mapView,
                        currentLocation,
                        R.drawable.baseline_location_pin_24,
                        requireContext()
                    )

                    fusedLocationClient.removeLocationUpdates(this)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
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
        val sharedPreferences =
            requireActivity().getSharedPreferences("markers", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("marker_list", null)

        if (json != null) {
            val type = object : TypeToken<MutableList<Marker>>() {}.type
            val markerList: MutableList<Marker> = Gson().fromJson(json, type)
            val currentData = markersData.value ?: mutableListOf()
            markerList.forEach { marker ->
                currentData.add(Pair(marker.point, marker.description))
                viewModel.setMarker(
                    mapView,
                    marker.point,
                    R.drawable.baseline_location_pin_24_red,
                    requireContext()
                )
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