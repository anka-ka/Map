package ru.netology.map.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.map.R
import ru.netology.map.dto.Marker


class MarkerAdapter(
    private val markers: MutableList<Marker>,
    private val onEdit: (Marker) -> Unit,
    private val onRemove: (Marker) -> Unit,
    private val onClick: (Marker) -> Unit
) : RecyclerView.Adapter<MarkerAdapter.MarkerViewHolder>() {

    class MarkerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val markerName: TextView = itemView.findViewById(R.id.marker_description)

        fun bind(marker: Marker, onEdit: (Marker) -> Unit, onRemove: (Marker) -> Unit, onClick: (Marker) -> Unit) {
            markerName.text = marker.description
            itemView.setOnClickListener { onClick(marker) }

            setupPopupMenu(marker, onEdit, onRemove)
        }

        private fun setupPopupMenu(
            marker: Marker,
            onEdit: (Marker) -> Unit,
            onRemove: (Marker) -> Unit
        ) {
            val menu = itemView.findViewById<ImageButton>(R.id.menu)
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_marks)
                    setOnMenuItemClickListener { item ->
                        when(item.itemId) {
                            R.id.edit -> {
                                onEdit(marker)
                                true
                            }
                            R.id.remove -> {
                                onRemove(marker)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.marker_item, parent, false)
        return MarkerViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        holder.bind(markers[position], onEdit, onRemove, onClick)
    }

    override fun getItemCount(): Int = markers.size

    fun updateData(newMarkers: List<Marker>) {
        val diffCallback = MarkerDiffCallback(markers, newMarkers)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        markers.clear()
        markers.addAll(newMarkers)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateMarker(marker: Marker) {
        val index = markers.indexOfFirst { it.id == marker.id }
        if (index != -1) {
            markers[index] = marker
            notifyItemChanged(index)
        }
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition != toPosition) {
            val movedItem = markers.removeAt(fromPosition)
            markers.add(toPosition, movedItem)
            notifyItemMoved(fromPosition, toPosition)
        }
    }

}