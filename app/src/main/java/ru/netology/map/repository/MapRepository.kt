package ru.netology.map.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ru.netology.map.R
import ru.netology.map.dao.MarkerDao
import ru.netology.map.dto.Marker
import ru.netology.map.entity.MarkerEntity
import javax.inject.Inject


class MapRepository @Inject constructor(
    private val markerDao: MarkerDao
) {
    val ZOOM_BOUNDARY = 16.4f
    private var placemarkMapObject: PlacemarkMapObject? = null
    private var zoomValue: Float = 0f

    suspend fun addMarker(marker: Marker) {
        val markerEntity = MarkerEntity(
            id = marker.id,
            description = marker.description,
            latitude = marker.point.latitude,
            longitude = marker.point.longitude
        )
        markerDao.insert(markerEntity)
    }

    suspend fun getMarkers(): List<Marker> {
        return markerDao.getAllMarkers().map { markerEntity ->
            Marker(
                id = markerEntity.id,
                description = markerEntity.description,
                point = Point(markerEntity.latitude, markerEntity.longitude)
            )
        }
    }

    suspend fun saveMarker(markers: List<MarkerEntity>) {
        markers.forEach { marker ->
            markerDao.insert(marker)
        }
    }

    suspend fun updateMarker(marker: Marker) {
        val markerEntity = MarkerEntity(
            id = marker.id,
            description = marker.description,
            latitude = marker.point.latitude,
            longitude = marker.point.longitude
        )
        markerDao.update(markerEntity)
    }

    fun addPlacemarkAtLocation(
        mapView: MapView,
        point: Point,
        imageResource: Int
    ) {
        val mapObjects = mapView.map.mapObjects
        val bitmap = createBitmapFromVector(mapView.context, imageResource)
        bitmap?.let {
            mapObjects.addPlacemark(point, ImageProvider.fromBitmap(it))
        }
    }

    fun zoomIn(mapView: MapView) {
        val currentZoom = mapView.map.cameraPosition.zoom
        mapView.map.move(
            CameraPosition(mapView.map.cameraPosition.target, currentZoom + 1, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0.5f),
            null
        )
    }

    fun zoomOut(mapView: MapView) {
        val currentZoom = mapView.map.cameraPosition.zoom
        mapView.map.move(
            CameraPosition(mapView.map.cameraPosition.target, currentZoom - 1, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0.5f),
            null
        )
    }

    fun onCameraPositionChanged(
        mapView: MapView,
        cameraPosition: CameraPosition,
        cameraUpdateReason: CameraUpdateReason,
        finished: Boolean
    ) {
        if (finished) {
            when {
                cameraPosition.zoom >= ZOOM_BOUNDARY && zoomValue < ZOOM_BOUNDARY -> {
                    placemarkMapObject?.setIcon(
                        ImageProvider.fromBitmap(
                            createBitmapFromVector(
                                mapView.context,
                                R.drawable.baseline_location_pin_24_blue
                            )
                        )
                    )
                }

                cameraPosition.zoom < ZOOM_BOUNDARY && zoomValue >= ZOOM_BOUNDARY -> {
                    placemarkMapObject?.setIcon(
                        ImageProvider.fromBitmap(
                            createBitmapFromVector(
                                mapView.context,
                                R.drawable.baseline_location_pin_24_red
                            )
                        )
                    )
                }
            }
            zoomValue = cameraPosition.zoom
        }
    }
    suspend fun deleteMarkerById(id: Long) {
        markerDao.deleteById(id)
    }

    private fun createBitmapFromVector(context: Context, drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}