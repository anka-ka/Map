package ru.netology.map.adapter

import androidx.recyclerview.widget.DiffUtil
import ru.netology.map.dto.Marker

class MarkerDiffCallback(
    private val oldList: List<Marker>,
    private val newList: List<Marker>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}