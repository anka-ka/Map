package ru.netology.map.ViewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.mapview.MapView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.map.dto.Marker
import ru.netology.map.repository.MapRepository
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(private val repository: MapRepository) : ViewModel() {

    private val _markers = MutableLiveData<List<Marker>>()
    val markers: LiveData<List<Marker>> = _markers

    init {
        loadMarkers()
    }

    fun addMarker(marker: Marker) {
        viewModelScope.launch {
            repository.addMarker(marker)
            _markers.value = repository.getMarkers()
        }
    }


    fun loadMarkers() {
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


    fun removeMarker(marker: Marker) {
        viewModelScope.launch {
            repository.deleteMarkerById(marker.id)
            _markers.value = _markers.value?.filter { it.id != marker.id } ?: emptyList()

        }
    }

    fun moveToMarker(marker: Marker, mapView: MapView) {
        val point = marker.point
        Log.d("MapViewModel", "Moving to marker at: ${point.latitude}, ${point.longitude}")
        val cameraPosition = CameraPosition(point, 17.0f, 0.0f, 0.0f)
        mapView.map.move(cameraPosition, Animation(Animation.Type.SMOOTH, 5f), null)
    }
}