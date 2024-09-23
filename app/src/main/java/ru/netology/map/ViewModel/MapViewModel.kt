package ru.netology.map.ViewModel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import ru.netology.map.dto.Marker
import ru.netology.map.repository.MapRepository

class MapViewModel(private val repository: MapRepository) : ViewModel() {
    private val _placemark = MutableLiveData<PlacemarkMapObject>()
    val placemark: LiveData<PlacemarkMapObject> = _placemark

    private val _markers = MutableLiveData<List<Marker>>()
    val markers: LiveData<List<Marker>> get() = _markers

    init {
        _markers.value = emptyList()
    }

    fun addMarker(marker: Marker) {
        val currentList = _markers.value?.toMutableList() ?: mutableListOf()
        currentList.add(marker)
        _markers.value = currentList
    }



    fun setMarker(mapView: MapView, point: Point, imageResource: Int, context: Context) {
        val placemark = repository.addPlacemarkAtLocation(mapView, point, imageResource, context)
        _placemark.value = placemark
    }

    fun onCameraPositionChanged(
        mapView: MapView,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        repository.onCameraPositionChanged(mapView, cameraPosition, cameraUpdateReason, finished)
    }

    fun zoomIn(mapView: MapView) {
        repository.zoomIn(mapView)
    }

    fun zoomOut(mapView: MapView) {
        repository.zoomOut(mapView)
    }



    fun moveToMarker(marker: Marker, mapView: MapView) {

        val map = mapView.map
        val cameraPosition = CameraPosition(marker.point, 14.0f, 0.0f, 0.0f)
        map.move(
            cameraPosition,
            Animation(Animation.Type.SMOOTH, 2f),
            null
        )
    }

}