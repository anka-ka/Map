package ru.netology.map.ViewModel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.map.dto.Marker
import ru.netology.map.entity.MarkerEntity
import ru.netology.map.repository.MapRepository
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val repository: MapRepository) : ViewModel() {

    private val _markers = MutableLiveData<List<Marker>>()
    val markers: LiveData<List<Marker>> = _markers

    init {
        loadMarkers()
    }

    private fun mapToMarker(markerEntity: MarkerEntity): Marker {
        return Marker(
            id = markerEntity.id,
            description = markerEntity.description,
            point = Point(markerEntity.latitude, markerEntity.longitude)
        )
    }
    private fun mapToMarkerEntity(marker: Marker): MarkerEntity {
        return MarkerEntity(
            id = marker.id,
            description = marker.description,
            latitude = marker.point.latitude,
            longitude = marker.point.longitude
        )
    }

    fun addMarker(marker: Marker) {
        viewModelScope.launch {
            repository.addMarker(marker)
            _markers.value = repository.getMarkers()
        }
    }


    private fun loadMarkers() {
        viewModelScope.launch {
            val markers = repository.getMarkers()
            _markers.value = markers
        }
    }


    suspend fun updateMarkerDescription(marker: Marker, newDescription: String) {
        val updatedMarker = marker.copy(description = newDescription)
        repository.updateMarker(updatedMarker)
        loadMarkers()
    }


    fun addPlacemark(mapView: MapView, point: Point, iconRes: Int) {
        repository.addPlacemarkAtLocation(mapView, point, iconRes)
    }


    fun zoomIn(mapView: MapView) {
        repository.zoomIn(mapView)
    }


    fun zoomOut(mapView: MapView) {
        repository.zoomOut(mapView)
    }


    fun handleCameraPositionChanged(
        mapView: MapView,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        repository.onCameraPositionChanged(mapView, cameraPosition, cameraUpdateReason, finished)
    }


    fun updateMarkers(marker: Marker, markers: List<Marker>) {
        viewModelScope.launch {
            val markerEntities = markers.map {
                MarkerEntity(
                    id = it.id,
                    description = it.description,
                    latitude = marker.point.latitude,
                    longitude = marker.point.longitude
                )
            }
            repository.saveMarker(markerEntities)
            _markers.value = markers
        }
    }


    fun removeMarker(marker: Marker) {
        viewModelScope.launch {
            val currentMarkers = _markers.value?.toMutableList() ?: mutableListOf()
            if (currentMarkers.remove(marker)) {
                val markerEntity = MarkerEntity(
                    id = marker.id,
                    description = marker.description,
                    latitude = marker.point.latitude,
                    longitude = marker.point.longitude
                )
                repository.saveMarker(currentMarkers.map { markerEntity })
                _markers.value = currentMarkers
            } else {
                Log.d("RemoveMarker", "Маркер не найден для удаления")
            }
        }
    }

}