package ru.netology.map.ui.theme

import android.os.Bundle
import android.view.View
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
import ru.netology.map.repository.MapRepository

class MarksMenuFragment : Fragment(R.layout.marks_menu) {

    private lateinit var viewModel: MapViewModel
    private lateinit var mapView: MapView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repository = MapRepository()

        viewModel =
            ViewModelProvider(this, MapViewModelFactory(repository)).get(MapViewModel::class.java)
        mapView = requireActivity().findViewById(R.id.mapView)

        val recyclerView: RecyclerView = view.findViewById(R.id.markers_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.markers.observe(viewLifecycleOwner) { markers ->
            val adapter = MarkerAdapter(markers) { marker ->

                findNavController().popBackStack()
                viewModel.moveToMarker(marker, mapView)
            }
            recyclerView.adapter = adapter
        }
    }
}