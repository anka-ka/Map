package ru.netology.map.ui.theme

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import androidx.lifecycle.lifecycleScope
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.netology.map.R
import ru.netology.map.ViewModel.MapViewModel
import ru.netology.map.databinding.FragmentMapBinding
import ru.netology.map.dto.Marker
import kotlin.math.abs

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map), CameraListener {


    private lateinit var mapView: MapView
    private val viewModel: MapViewModel by viewModels()
    private lateinit var mapTapListener: InputListener

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
        binding.settings.setOnClickListener{
            findNavController().navigate(R.id.action_mapFragment_to_SettingsFragment)
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


        mapTapListener = object : InputListener {
            override fun onMapTap(map: Map, point: Point) {
                showMarkerInputDialog(point)
            }

            override fun onMapLongTap(map: Map, point: Point) {
                Log.d("Long tap", "long tap at point: $point")
                val marker = getMarkerAtPoint(point)
                if (marker != null) {
                    showMarkerDescription(marker)
                } else {
                    Log.d("Long tap", "No marker found at this location.")
                }
            }
        }

        mapView.map.addInputListener(mapTapListener)
    }

    private fun getMarkerAtPoint(point: Point): Marker? {
        val tolerance = 0.0001

        return viewModel.markers.value?.find {
            abs(it.point.latitude - point.latitude) < tolerance &&
                    abs(it.point.longitude - point.longitude) < tolerance
        }
    }

    private fun setupObservers() {
        viewModel.markers.observe(viewLifecycleOwner) { markers ->
            mapView.map.mapObjects.clear()
            markers.forEach { marker ->
                viewModel.addPlacemark(
                    mapView,
                    marker.point,
                    R.drawable.baseline_location_pin_24_red
                )
            }
        }
    }

    private fun showMarkerDescription(marker: Marker) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_marker_description, null)

        val descriptionTextView: TextView = dialogView.findViewById(R.id.dialog_description)
        descriptionTextView.text = marker.description

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val closeButton: Button = dialogView.findViewById(R.id.close_button)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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
        val marker = arguments?.getSerializable("marker") as? Marker
        if (marker != null) {
            viewModel.moveToMarker(marker, mapView)
        } else {
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
            R.string.permission_denied,
            Snackbar.LENGTH_LONG
        )
            .setAction(R.string.try_again) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .show()
    }

    private fun showLocationErrorSnackbar() {
        Snackbar.make(
            binding.root,
            R.string.failed_to_get_location,
            Snackbar.LENGTH_LONG
        ).show()
    }

    private fun showMarkerInputDialog(point: Point) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_marker_input, null)

        val inputField = dialogView.findViewById<EditText>(R.id.inputField)
        val addButton = dialogView.findViewById<Button>(R.id.addButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()
        addButton.setOnClickListener {
            lifecycleScope.launch {
                val order = viewModel.getNextOrder()
                val description = inputField.text.toString()
                val newMarker = Marker(point = point, description = description, order = order)
                viewModel.addMarker(newMarker)
            }
            dialog.dismiss()
        }
        cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onCameraPositionChanged(
        map: Map,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {
            viewModel.handleCameraPositionChanged(
                mapView,
                cameraPosition,
                cameraUpdateReason,
                finished
            )
        }
    }

    override fun onStart() {
        super.onStart()
        if (::mapView.isInitialized) {
            mapView.onStart()
            MapKitFactory.getInstance().onStart()
        }
    }

    override fun onStop() {
        super.onStop()
        if (::mapView.isInitialized) {
            mapView.map.removeInputListener(mapTapListener)
            mapView.onStop()
            MapKitFactory.getInstance().onStop()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::mapView.isInitialized) {
            mapView.onStart()
            MapKitFactory.getInstance().onStart()
        }
    }
}