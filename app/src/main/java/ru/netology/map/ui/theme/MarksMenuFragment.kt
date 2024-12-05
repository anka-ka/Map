package ru.netology.map.ui.theme

import android.app.AlertDialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        adapter = MarkerAdapter(
            mutableListOf(),
            onEdit = { marker -> showEditDialog(marker) },
            onRemove = { marker -> viewModel.removeMarker(marker) },
            onClick = { marker -> onMarkerClick(marker) }
        )
        recyclerView.adapter = adapter

            val viewDragHelperCallback = ViewDragHelperCallback(adapter, viewModel)
            val itemTouchHelper = ItemTouchHelper(viewDragHelperCallback)
            itemTouchHelper.attachToRecyclerView(recyclerView)


            viewModel.markers.observe(viewLifecycleOwner) { markers ->
                adapter.updateData(markers)
            }

        view.findViewById<ImageButton>(R.id.back).setOnClickListener {
            findNavController().popBackStack()
        }
    }



    private fun showEditDialog(marker: Marker) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_marker, null)
        val editText = dialogView.findViewById<EditText>(R.id.editMarkerDescription).apply {
            setText(marker.description)
        }
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        btnSave.setOnClickListener {
            val newDescription = editText.text.toString()
            lifecycleScope.launch {
                viewModel.updateMarkerDescription(marker, newDescription)
            }

            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
    }

    private fun onMarkerClick(marker: Marker) {
        val bundle = Bundle().apply {
            putSerializable("marker", marker)
        }
        findNavController().navigate(R.id.mapFragment, bundle)
    }
}