package ru.netology.map.ui.theme

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yandex.mapkit.mapview.MapView
import ru.netology.map.R
import ru.netology.map.ViewModel.MapViewModel
import ru.netology.map.ViewModel.MapViewModelFactory
import ru.netology.map.adapter.MarkerAdapter

import ru.netology.map.dto.Marker
import ru.netology.map.repository.MapRepository

class MarksMenuFragment : Fragment(R.layout.marks_menu) {

    private lateinit var viewModel: MapViewModel
    private lateinit var mapView: MapView
    private lateinit var adapter: MarkerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = MapRepository()

        viewModel = ViewModelProvider(requireActivity(), MapViewModelFactory(repository))
            .get(MapViewModel::class.java)
        mapView = requireActivity().findViewById(R.id.mapView)

        val sharedPreferences = requireActivity().getSharedPreferences("markers", Context.MODE_PRIVATE)

        val savedMarkers = repository.getSavedMarkers(sharedPreferences)
        viewModel.loadMarkers(savedMarkers)

        val recyclerView: RecyclerView = view.findViewById(R.id.markers_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.markers.observe(viewLifecycleOwner) { markers ->
            val mutableMarkers = markers.toMutableList()

            adapter = MarkerAdapter(
                mutableMarkers,
                onEdit = { marker -> showEditDialog(marker) },
                onRemove = { marker ->
                    viewModel.removeMarker(
                        marker,
                        requireActivity().getSharedPreferences("markers", Context.MODE_PRIVATE)
                    )
                },
                onClick = { marker -> onMarkerClick(marker) }
            )
            recyclerView.adapter = adapter
        }


    }

    private fun showEditDialog(marker: Marker) {
        val builder = AlertDialog.Builder(requireContext())
        val editText = EditText(requireContext()).apply {
            setText(marker.description)
        }
        builder.setTitle("Edit Marker Description")
            .setView(editText)
            .setPositiveButton("Save") { dialog, _ ->
                val newDescription = editText.text.toString()
                viewModel.updateMarkerDescription(
                    marker,
                    newDescription,
                    requireActivity().getSharedPreferences("markers", Context.MODE_PRIVATE)
                )
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }


    private fun onMarkerClick(marker: Marker) {
        Log.d("MarksMenuFragment", "Marker clicked: ${marker.point.latitude}, ${marker.point.longitude}")
        val bundle = Bundle().apply {
            putSerializable("marker", marker)
        }
        findNavController().navigate(R.id.mapFragment, bundle)
    }

}