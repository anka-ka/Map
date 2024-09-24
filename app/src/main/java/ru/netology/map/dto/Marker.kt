package ru.netology.map.dto

import com.yandex.mapkit.geometry.Point
import java.io.Serializable

data class Marker(
    val point: Point,
    val description: String): Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Marker) return false
        return point.latitude == other.point.latitude && point.longitude == other.point.longitude
    }

    override fun hashCode(): Int {
        return (point.latitude.hashCode() * 31 + point.longitude.hashCode())
    }

}