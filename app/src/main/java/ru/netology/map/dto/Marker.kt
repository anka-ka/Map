package ru.netology.map.dto

import com.yandex.mapkit.geometry.Point

data class Marker(
    val point: Point,
    val description: String)