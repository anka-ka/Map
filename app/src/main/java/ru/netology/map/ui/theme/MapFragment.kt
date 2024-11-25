package ru.netology.map.ui.theme

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.InputListener
import androidx.fragment.app.viewModels
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.map.R
import ru.netology.map.ViewModel.MapViewModel
import ru.netology.map.databinding.FragmentMapBinding
import ru.netology.map.dto.Marker

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), CameraListener {


    private lateinit var mapView: MapView
    private val viewModel: MapViewModel by viewModels()

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPermissionLauncher()
        setupMap()
        setupObservers()
        binding.zoomInButton.setOnClickListener { viewModel.zoomIn(mapView) }
        binding.zoomOutButton.setOnClickListener { viewModel.zoomOut(mapView) }
        binding.marksMenu.setOnClickListener {
            findNavController().navigate(R.id.action_mapFragment_to_MarksMenuFragment)
        }

        checkPermissionsAndGetLocation()
    }

    private fun setupPermissionLauncher() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    getCurrentLocation()
                } else {
                    showPermissionDeniedSnackbar()
                }
            }
    }

    private fun setupMap() {
        mapView = binding.mapView
        mapView.map.addCameraListener(this)


        mapView.map.addInputListener(object : InputListener {
            override fun onMapTap(map: Map, point: Point) {
                showMarkerInputDialog(point)
            }

            override fun onMapLongTap(map: Map, point: Point) {
                //
            }
        })
    }

    private fun setupObservers() {
        viewModel.markers.observe(viewLifecycleOwner) { markers ->
            mapView.map.mapObjects.clear()
            markers.forEach { marker ->
                viewModel.addPlacemark(mapView, marker.point, R.drawable.baseline_location_pin_24_red)
            }
        }
    }

    private fun checkPermissionsAndGetLocation() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }

    private fun getCurrentLocation() {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            val targetLocation = location?.let {
                Point(it.latitude, it.longitude)
            } ?: Point(59.9402, 30.315)

            moveMapToLocation(targetLocation)
            viewModel.addPlacemark(
                mapView,
                targetLocation,
                R.drawable.baseline_location_pin_24
            )
        }.addOnFailureListener {
            showLocationErrorSnackbar()
        }
    }

    private fun moveMapToLocation(location: Point) {
        mapView.map.move(
            CameraPosition(location, 17.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0.5f),
            null
        )
    }

    private fun showPermissionDeniedSnackbar() {
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

    private fun showLocationErrorSnackbar() {
        Snackbar.make(
            binding.root,
            "Не удалось получить местоположение. Включите службы геолокации.",
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showMarkerInputDialog(point: Point) {
        val inputField = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Введите название маркера")
            .setView(inputField)
            .setPositiveButton("Добавить") { _, _ ->
                val description = inputField.text.toString()
                val newMarker = Marker(point = point, description = description)
                viewModel.addMarker(newMarker)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {
            viewModel.handleCameraPositionChanged(mapView, cameraPosition, cameraUpdateReason, finished)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}