package ru.netology.map.ui.theme

import android.app.AlertDialog

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import ru.netology.map.R
import ru.netology.map.ViewModel.MapViewModel
import ru.netology.map.adapter.MarkerAdapter
import ru.netology.map.adapter.ViewDragHelperCallback
import ru.netology.map.dto.Marker


@AndroidEntryPoint
class MarksMenuFragment : Fragment(R.layout.marks_menu) {

    private val viewModel: MapViewModel by viewModels()
    private lateinit var adapter: MarkerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.markers_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        viewModel.markers.observe(viewLifecycleOwner) { markers ->
            adapter = MarkerAdapter(
                markers.toMutableList(),
                onEdit = { marker -> showEditDialog(marker) },
                onRemove = { marker -> viewModel.removeMarker(marker) },
                onClick = { marker -> onMarkerClick(marker) }
            )
            recyclerView.adapter = adapter

            val viewDragHelperCallback = ViewDragHelperCallback(adapter)
            val itemTouchHelper = ItemTouchHelper(viewDragHelperCallback)
            itemTouchHelper.attachToRecyclerView(recyclerView)


            viewModel.markers.observe(viewLifecycleOwner) { markers ->
                adapter.updateData(markers)
            }
        }
        view.findViewById<MaterialButton>(R.id.back).setOnClickListener {
            findNavController().navigate(R.id.action_MarksMenuFragment_to_mapFragment)
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
                lifecycleScope.launch {
                    viewModel.updateMarkerDescription(marker, newDescription)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun onMarkerClick(marker: Marker) {
        val bundle = Bundle().apply {
            putSerializable("marker", marker)
        }
        findNavController().navigate(R.id.mapFragment, bundle)
    }
}