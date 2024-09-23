package ru.netology.map.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.netology.map.R
import ru.netology.map.dto.Marker

class MarkerAdapter(private val markers: List<Marker>, private val onClick: (Marker) -> Unit) :
    RecyclerView.Adapter<MarkerAdapter.MarkerViewHolder>() {

    class MarkerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val markerName: TextView = itemView.findViewById(R.id.marker_description)

        fun bind(marker: Marker, onClick: (Marker) -> Unit) {
            markerName.text = marker.description
            itemView.setOnClickListener { onClick(marker) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarkerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.marker_item, parent, false)
        return MarkerViewHolder(view)
    }

    override fun onBindViewHolder(holder: MarkerViewHolder, position: Int) {
        holder.bind(markers[position], onClick)
    }

    override fun getItemCount(): Int = markers.size
}