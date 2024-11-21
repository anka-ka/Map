package ru.netology.map.repository

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yandex.mapkit.Animation
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
import ru.netology.map.R
import ru.netology.map.dto.Marker


class MapRepository {
    private var placemarkMapObject: PlacemarkMapObject? = null
    private var zoomValue: Float = 0f
    val ZOOM_BOUNDARY = 16.4f


    fun addPlacemarkAtLocation(
        mapView: MapView,
        point: Point,
        imageResource: Int,
        context: Context
    ): PlacemarkMapObject {

        val mapObjects = mapView.map.mapObjects
        val marker = createBitmapFromVector(context, imageResource)
        val placemark = mapObjects.addPlacemark(
            point,
            ImageProvider.fromBitmap(marker)
        )
        return placemark
    }

    private fun createBitmapFromVector(context: Context, art: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, art) ?: return null
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = android.graphics.Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
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

    fun getSavedMarkers(sharedPreferences: SharedPreferences): List<Marker> {
        val json = sharedPreferences.getString("marker_list", "[]") ?: "[]"
        val type = object : TypeToken<List<Marker>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun updateMarker(updatedMarker: Marker, sharedPreferences: SharedPreferences) {
        val currentMarkers = getSavedMarkers(sharedPreferences).toMutableList()

        val index = currentMarkers.indexOfFirst {
            it.point.latitude == updatedMarker.point.latitude &&
                    it.point.longitude == updatedMarker.point.longitude
        }

        if (index != -1) {
            currentMarkers[index] = updatedMarker
            saveMarkers(currentMarkers, sharedPreferences)
        } else {
            Log.e("MapRepository", "Marker not found for update: $updatedMarker. Current markers: $currentMarkers")
        }
    }


    private fun saveMarkers(markers: List<Marker>, sharedPreferences: SharedPreferences) {
        val json = Gson().toJson(markers)
        sharedPreferences.edit().putString("marker_list", json).apply()
    }
}
