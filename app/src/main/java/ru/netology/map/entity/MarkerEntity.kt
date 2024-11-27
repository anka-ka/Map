package ru.netology.map.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "marker_table")
data class MarkerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    val order: Int,
)